package org.apache.fineract.portfolio.globaltransaction.service;

import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.externalRefNoParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.transactionRefNoParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.reversalTransactionAction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepository;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.globaltransaction.data.GlobalTransactionReferenceDataValidator;
import org.apache.fineract.portfolio.globaltransaction.domain.GlobalTransactionReference;
import org.apache.fineract.portfolio.globaltransaction.domain.GlobalTransactionReferenceAssembler;
import org.apache.fineract.portfolio.globaltransaction.domain.GlobalTransactionReferenceRepositoryWrapper;
import org.apache.fineract.portfolio.globaltransaction.exception.GlobalTransactionReverseException;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class GlobalTransactionReferenceWritePlatformServiceJpaRepositoryImpl implements GlobalTransactionReferenceWritePlatformService {

    private final Logger logger;
    private final GlobalTransactionReferenceRepositoryWrapper transactionReferenceRepository;
    private final GlobalTransactionReferenceDataValidator transactionReferenceDataValidator;
    private final GlobalTransactionReferenceAssembler transactionReferenceAssembler;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final SavingsAccountAssembler accountAssembler;
    private final JournalEntryRepository journalEntryRepository;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public GlobalTransactionReferenceWritePlatformServiceJpaRepositoryImpl(
            GlobalTransactionReferenceRepositoryWrapper transactionReferenceRepository,
            GlobalTransactionReferenceDataValidator transactionReferenceDataValidator,
            GlobalTransactionReferenceAssembler transactionReferenceAssembler,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final SavingsAccountAssembler accountAssembler, final JournalEntryRepository journalEntryRepository,
            final SavingsAccountDomainService savingsAccountDomainService, final ConfigurationDomainService configurationDomainService) {
        this.logger = LoggerFactory.getLogger(GlobalTransactionReferenceWritePlatformServiceJpaRepositoryImpl.class);
        this.transactionReferenceRepository = transactionReferenceRepository;
        this.transactionReferenceDataValidator = transactionReferenceDataValidator;
        this.transactionReferenceAssembler = transactionReferenceAssembler;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.accountAssembler = accountAssembler;
        this.journalEntryRepository = journalEntryRepository;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.configurationDomainService = configurationDomainService;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        try {
            // context.authenticatedUser();
            this.transactionReferenceDataValidator.validateForCreate(command.json());

            final GlobalTransactionReference transactionReference = this.transactionReferenceAssembler.assembleFrom(command);

            this.transactionReferenceRepository.save(transactionReference);

            // used for returning additional details
            final Map<String, Object> changes = new HashMap<>();
            changes.put("transactionRefNo", transactionReference.transactionRefNo());
            return new CommandProcessingResultBuilder() //
                    .withEntityId(transactionReference.getId()) //
                    .with(changes).build();
        } catch (final DataAccessException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult undoTransaction(final Long transactionId) {

        // context.authenticatedUser();

        final GlobalTransactionReference transactionReference = this.transactionReferenceRepository
                .findOneWithNotFoundDetection(transactionId);

        if ((transactionReference.isCompleteBusinessReversal() && !transactionReference.isBusinessReversal())
                || transactionReference.isReversed()) { throw new GlobalTransactionReverseException(transactionReference.transactionRefNo()); }

        // undo savings transactions
        List<Map<String, Object>> metadataTransactions = this.undoSavingsAccountTransactions(transactionReference);

        // undo journal entries of transactions
        final List<JournalEntry> nonReversedJournalEntryTransactions = this.journalEntryRepository
                .findUnReversedSystemJournalEntriesByTransactionRefId(transactionReference.getId());
        if (!CollectionUtils.isEmpty(nonReversedJournalEntryTransactions)) {
            final boolean manualEntry = false;
            this.journalEntryWritePlatformService.revertJournalEntries(nonReversedJournalEntryTransactions, manualEntry);
        }

        // undo manual journal entries
        final List<JournalEntry> nonReversedManualJournalEntryTransactions = this.journalEntryRepository
                .findUnReversedManualJournalEntriesByTransactionRefId(transactionReference.getId());
        if (!CollectionUtils.isEmpty(nonReversedManualJournalEntryTransactions)) {
            final boolean manualEntry = true;
            this.journalEntryWritePlatformService.revertJournalEntries(nonReversedManualJournalEntryTransactions, manualEntry);
        }

        transactionReference.reverse();
        transactionReference.undoBusinessReversal();

        this.transactionReferenceRepository.save(transactionReference);

        final Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("transactionRefNo", transactionReference.transactionRefNo());
        metadata.put("transactions", metadataTransactions);

        return new CommandProcessingResultBuilder() //
                .withEntityId(transactionReference.getId()) //
                .build();
    }

    private List<Map<String, Object>> undoSavingsAccountTransactions(final GlobalTransactionReference transactionReference) {

        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

        // undo savings transactions
        final List<SavingsAccountTransaction> nonReversedSavingsTransactions = transactionReference
                .retrieveNonReversedSavingsTransactions();

        final Map<String, BigDecimal> metadata = new LinkedHashMap<>(nonReversedSavingsTransactions.size());

        if (!CollectionUtils.isEmpty(nonReversedSavingsTransactions)) {

            for (SavingsAccountTransaction savingsAccountTransaction : nonReversedSavingsTransactions) {

                final SavingsAccount account = savingsAccountTransaction.savingsAccount();
                this.accountAssembler.setHelpers(account);
                final Set<Long> existingTransactionIds = new HashSet<>();
                final Set<Long> existingReversedTransactionIds = new HashSet<>();
                updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);

                final LocalDate today = DateUtils.getLocalDateOfTenant();
                final MathContext mc = new MathContext(15, RoundingMode.HALF_EVEN);

                if (account.isNotActive()) {
                    throwValidationForActiveStatus(SavingsApiConstants.undoTransactionAction);
                }

                savingsAccountTransaction.undoTransaction();

                // if its deposit/withdrawal transcation then reverse associated
                // charges as well
                if (savingsAccountTransaction.isWithdrawal() || savingsAccountTransaction.isDeposit()) {
                    transactionReference.undoSavingsCharges(account.getId());
                }

                boolean isInterestTransfer = false;

                if (savingsAccountTransaction.isPostInterestCalculationRequired()
                        && account.isBeforeLastPostingPeriod(savingsAccountTransaction.transactionLocalDate())) {
                    account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                            financialYearBeginningMonth, transactionReference);
                } else {
                    account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                            financialYearBeginningMonth);
                }

                account.validateAccountBalanceDoesNotBecomeNegative(SavingsApiConstants.undoTransactionAction);
                account.activateAccountBasedOnBalance();
                account.updateMetadata(metadata);
                // postJournalEntries(account, existingTransactionIds,
                // existingReversedTransactionIds);
            }
        }

        return serializedMetadata(metadata);

    }

    private List<Map<String, Object>> serializedMetadata(Map<String, BigDecimal> metadata) {

        final List<Map<String, Object>> serializedMetadata = new ArrayList<>(metadata.size());
        for (Map.Entry<String, BigDecimal> entry : metadata.entrySet()) {
            final Map<String, Object> serializedEntry = new LinkedHashMap<>();
            serializedEntry.put("accountNumber", entry.getKey());
            serializedEntry.put("balance", entry.getValue());
            serializedMetadata.add(serializedEntry);
        }
        return serializedMetadata;
    }

    private void updateExistingTransactionsDetails(SavingsAccount account, Set<Long> existingTransactionIds,
            Set<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(account.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
    }

    @SuppressWarnings("unused")
    private void postJournalEntries(final SavingsAccount savingsAccount, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);
        boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }

    /**
    *
    */
    private void throwValidationForActiveStatus(final String actionName) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SAVINGS_ACCOUNT_RESOURCE_NAME + actionName);
        baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("account.is.not.active");
        throw new PlatformApiDataValidationException(dataValidationErrors);
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataAccessException dve) {

        final StringBuilder errorCodeBuilder = new StringBuilder("error.msg.").append(GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME);

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("gtr_external_ref_no_UNIQUE")) {
            final String externalRefNo = command.stringValueOfParameterNamed(externalRefNoParamName);
            errorCodeBuilder.append(".duplicate.externalRefNo");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Transactions with external reference number "
                    + externalRefNo + " already exists", "externalRefNo", externalRefNo);

        }

        if (realCause.getMessage().contains("gtr_transaction_ref_no_UNIQUE")) {
            final String transactionRefNo = command.stringValueOfParameterNamed(transactionRefNoParamName);
            errorCodeBuilder.append(".duplicate.transactionRefNo");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Transaction with reference number " + transactionRefNo
                    + " already exists", "transactionRefNo", transactionRefNo);

        }

        errorCodeBuilder.append(".unknown.data.integrity.issue");
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException(errorCodeBuilder.toString(),
                "Unknown data integrity issue with savings account transaction.");
    }

    @Transactional
    @Override
    public CommandProcessingResult transactionReversal(final Long transactionId) {
        try {
            // this.context.authenticatedUser();
            final GlobalTransactionReference transactionReference = this.transactionReferenceRepository
                    .findOneWithNotFoundDetection(transactionId);

            if (transactionReference.isCompleteBusinessReversal() || transactionReference.isReversed()) { throw new GlobalTransactionReverseException(
                    transactionReference.transactionRefNo()); }
            final GlobalTransactionReference newTransactionReference = this.transactionReferenceAssembler
                    .transactionReversal(transactionReference);

            this.transactionReferenceRepository.save(newTransactionReference);

            final List<SavingsAccountTransaction> transactions = transactionReference.getSavingAccountTransactions();
            final List<SavingsAccountTransaction> reversedTransactions = new ArrayList<>(transactions.size());
            List<Map<String, Object>> metadataTransactions = this.performTransactionReversal(newTransactionReference, transactions,
                    reversedTransactions);

            // undo journal entries
            final List<JournalEntry> nonReversedJournalEntryTransactions = this.journalEntryRepository
                    .findUnReversedSystemJournalEntriesByTransactionRefId(transactionReference.getId());
            if (!CollectionUtils.isEmpty(nonReversedJournalEntryTransactions)) {
                this.journalEntryWritePlatformService.revertJournalEntriesForReversedTransactions(nonReversedJournalEntryTransactions,
                        newTransactionReference, reversedTransactions);
            }

            // undo manual journal entries
            final List<JournalEntry> nonReversedManualJournalEntryTransactions = this.journalEntryRepository
                    .findUnReversedManualJournalEntriesByTransactionRefId(transactionReference.getId());
            if (!CollectionUtils.isEmpty(nonReversedManualJournalEntryTransactions)) {
                final boolean manualEntry = true;
                this.journalEntryWritePlatformService.revertJournalEntriesWithNewReference(nonReversedManualJournalEntryTransactions,
                        manualEntry, newTransactionReference);
            }

            transactionReference.markAsCompleteReversal();
            this.transactionReferenceRepository.save(transactionReference);

            final Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("transactionRefNo", newTransactionReference.transactionRefNo());
            metadata.put("reversedTransactionRefNo", transactionReference.transactionRefNo());
            metadata.put("transactions", metadataTransactions);

            return new CommandProcessingResultBuilder() //
                    .withEntityId(transactionReference.getId()) //
                    .build();
        } catch (final DataAccessException dve) {

            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    private List<Map<String, Object>> performTransactionReversal(final GlobalTransactionReference transactionReference,
            final List<SavingsAccountTransaction> transactions, final List<SavingsAccountTransaction> reversedTransactions) {
        final Map<String, BigDecimal> metadata = new LinkedHashMap<>(transactions.size());

        if (!CollectionUtils.isEmpty(transactions)) {
            for (final SavingsAccountTransaction transactionToBeReversed : transactions) {
                final SavingsAccount account = transactionToBeReversed.savingsAccount();
                this.accountAssembler.setHelpers(account);
                final SavingsAccountTransaction reversedTransaction;
                if (transactionToBeReversed.isDeposit()) {
                    reversedTransaction = this.savingsAccountDomainService.handleDepositReversal(account, transactionReference,
                            transactionToBeReversed);
                    reversedTransactions.add(reversedTransaction);
                } else if (transactionToBeReversed.isWithdrawal()) {
                    reversedTransaction = this.savingsAccountDomainService.handleWithdrawalReversal(account, transactionReference,
                            transactionToBeReversed);
                    reversedTransactions.add(reversedTransaction);
                } else if (transactionToBeReversed.isChargeTransaction()) {
                    reversedTransaction = this.savingsAccountDomainService.handleChargeReversal(account, transactionReference,
                            transactionToBeReversed);
                    reversedTransactions.add(reversedTransaction);
                }

                account.validateAccountBalanceDoesNotBecomeNegative(reversalTransactionAction);
                account.activateAccountBasedOnBalance();
                account.updateMetadata(metadata);
            }
        }
        return serializedMetadata(metadata);
    }

}