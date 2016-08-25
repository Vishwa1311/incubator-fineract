package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.charge.data.GroupLoanIndividualMonitoringChargeData;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.exception.LoanChargeNotFoundException;
import org.apache.fineract.portfolio.charge.service.GroupLoanIndividualMonitoringChargeReadPlatformService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.DefaultLoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargePaidBy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.exception.ClientAlreadyWriteOffException;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanEventApiJsonValidator;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class GroupLoanIndividualMonitoringTransactionWritePlatformServiceImpl implements
        GroupLoanIndividualMonitoringTransactionWritePlatformService {

    private final LoanWritePlatformService loanWritePlatformService;
    private final GroupLoanIndividualMonitoringTransactionRepositoryWrapper groupLoanIndividualMonitoringTransactionRepositoryWrapper;
    private final LoanAccountDomainService loanAccountDomainService;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final LoanEventApiJsonValidator loanEventApiJsonValidator;
    private final LoanAssembler loanAssembler;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final GroupLoanIndividualMonitoringTransactionAssembler groupLoanIndividualMonitoringTransactionAssembler;
    private final GroupLoanIndividualMonitoringAssembler glimAssembler;
    private final GroupLoanIndividualMonitoringRepository glimRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final LoanUtilService loanUtilService;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final LoanTransactionRepository loanTransactionRepository;
    private final LoanRepository loanRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final PlatformSecurityContext context;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final LoanChargeRepository loanChargeRepository;
    private final GroupLoanIndividualMonitoringChargeReadPlatformService glimChargeReadPlatformService;
    private final GroupLoanIndividualMonitoringChargeRepositoryWrapper glimChargeRepositoryWrapper;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private final NoteRepository noteRepository;
    private final GroupLoanIndividualMonitoringReadPlatformService groupLoanIndividualMonitoringReadPlatformService;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionWritePlatformServiceImpl(final LoanWritePlatformService loanWritePlatformService,
            final GroupLoanIndividualMonitoringTransactionRepositoryWrapper groupLoanIndividualMonitoringTransactionRepositoryWrapper,
            final LoanAccountDomainService loanAccountDomainService, final LoanRepositoryWrapper loanRepositoryWrapper,
            final LoanEventApiJsonValidator loanEventApiJsonValidator, final LoanAssembler loanAssembler,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final GroupLoanIndividualMonitoringTransactionAssembler groupLoanIndividualMonitoringTransactionAssembler,
            final GroupLoanIndividualMonitoringAssembler glimAssembler, final GroupLoanIndividualMonitoringRepository glimRepository,
            final BusinessEventNotifierService businessEventNotifierService, final LoanUtilService loanUtilService,
            final LoanChargeReadPlatformService loanChargeReadPlatformService, final LoanTransactionRepository loanTransactionRepository,
            final LoanRepository loanRepository, final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository, 
            final JournalEntryWritePlatformService journalEntryWritePlatformService, final PlatformSecurityContext context,
            final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository, final LoanChargeRepository loanChargeRepository, 
            final GroupLoanIndividualMonitoringChargeReadPlatformService glimChargeReadPlatformService, 
            final GroupLoanIndividualMonitoringChargeRepositoryWrapper glimChargeRepositoryWrapper, final CodeValueRepositoryWrapper codeValueRepository,
            final AccountTransfersWritePlatformService accountTransfersWritePlatformService, final NoteRepository noteRepository,
            final GroupLoanIndividualMonitoringReadPlatformService groupLoanIndividualMonitoringReadPlatformService) {
        this.loanWritePlatformService = loanWritePlatformService;
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper = groupLoanIndividualMonitoringTransactionRepositoryWrapper;
        this.loanAccountDomainService = loanAccountDomainService;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.loanEventApiJsonValidator = loanEventApiJsonValidator;
        this.loanAssembler = loanAssembler;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.groupLoanIndividualMonitoringTransactionAssembler = groupLoanIndividualMonitoringTransactionAssembler;
        this.glimAssembler = glimAssembler;
        this.glimRepository = glimRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.loanUtilService = loanUtilService;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.loanTransactionRepository = loanTransactionRepository;
        this.loanRepository = loanRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.context = context;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.loanChargeRepository = loanChargeRepository;
        this.glimChargeReadPlatformService = glimChargeReadPlatformService;
        this.glimChargeRepositoryWrapper = glimChargeRepositoryWrapper;
        this.codeValueRepository = codeValueRepository;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.noteRepository = noteRepository;
        this.groupLoanIndividualMonitoringReadPlatformService = groupLoanIndividualMonitoringReadPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult repayGLIM(Long loanId, JsonCommand command) {

        // this.loanWritePlatformService.makeLoanRepayment(loanId, command,
        // false);
        this.loanEventApiJsonValidator.validateNewRepaymentTransaction(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        changes.put("paymentTypeId", command.stringValueOfParameterNamed("paymentTypeId"));

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, loan);
        loan.updateGlim(glimMembers);
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        final Boolean isHolidayValidationDone = false;
        final boolean isRecoveryRepayment = false;
        final HolidayDetailDTO holidayDetailDto = null;
        boolean isAccountTransfer = false;
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        LoanTransaction loanTransaction = this.loanAccountDomainService.makeRepayment(loan, commandProcessingResultBuilder,
                transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId, isRecoveryRepayment, isAccountTransfer,
                holidayDetailDto, isHolidayValidationDone);
        // FinanceLib.pmt(r, n, p, f, t);
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.groupLoanIndividualMonitoringTransactionAssembler
                .assembleGLIMTransactions(command, loanTransaction);      
        this.glimAssembler.updateGLIMAfterRepayment(glimTransactions);
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.saveAsList(glimTransactions);
        this.groupLoanIndividualMonitoringTransactionAssembler.updateLoanWriteOffStatusForGLIM(loan);
        
        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .with(changes) //
                .build();
    }

    @Override
    public CommandProcessingResult waiveCharge(Long loanId, JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        List<LoanCharge> loanCharges = new ArrayList<LoanCharge>(loan.charges());

        checkClientOrGroupActive(loan);
        this.loanEventApiJsonValidator.validateGLIMWaiveChargeTransaction(command.json());
        Integer loanInstallmentNumber = null;
        MonetaryCurrency currency = loan.getCurrency();

        final JsonArray glimList = command.arrayOfParameterNamed("clientMembers");

        if (glimList != null) {
            for (JsonElement glimJson : glimList) {
                if (glimJson.getAsJsonObject().get("isClientSelected").getAsBoolean()) {
                    for (int i = 0; i < loanCharges.size(); i++) {
                        final Long glimId = glimJson.getAsJsonObject().get("id").getAsLong();
                        GroupLoanIndividualMonitoring glim = this.glimRepository.findOne(glimId);

                        BigDecimal writeOfAmount = zeroIfNull(glim.getPrincipalWrittenOffAmount()).add(
                                zeroIfNull(glim.getInterestWrittenOffAmount())).add(zeroIfNull(glim.getChargeWrittenOffAmount()));
                        if (writeOfAmount.compareTo(BigDecimal.ZERO) > 0 && glim.getTransactionAmount().compareTo(BigDecimal.ZERO) > 0) { throw new ClientAlreadyWriteOffException(); }

                        LoanCharge loanCharge = loanCharges.get(i);
                        Long chargeId = loanCharge.getCharge().getId();

                        Integer numberOfInstallment = loan.getLoanRepaymentScheduleDetail().getNumberOfRepayments();
                        BigDecimal installmentAmount = Money.of(
                                currency,
                                BigDecimal.valueOf(loanCharge.getAmount(currency).getAmount().doubleValue()
                                        / Double.valueOf(numberOfInstallment.toString()))).getAmount();
                        BigDecimal paidCharge = loanCharge.getAmountPaid(currency).getAmount()
                                .add(loanCharge.getAmountWaived(currency).getAmount());
                        Integer paidInstallment = BigDecimal.valueOf((paidCharge.doubleValue() / installmentAmount.doubleValue()))
                                .intValue() + 1;
                        final Map<String, Object> changes = new LinkedHashMap<>(3);

                        final List<Long> existingTransactionIds = new ArrayList<>();
                        final List<Long> existingReversedTransactionIds = new ArrayList<>();
                        LocalDate recalculateFrom = null;
                        ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);

                        Money accruedCharge = Money.zero(loan.getCurrency());
                        List<LoanTransaction> waiveLoanTransactions = new ArrayList<>();
                        for (GroupLoanIndividualMonitoringCharge glimCharge : glim.getGroupLoanIndividualMonitoringCharges()) {
                            if (glimCharge.getCharge().getId().equals(chargeId)) {
                                BigDecimal totalCharge = glimCharge.getRevisedFeeAmount() == null ? glimCharge.getFeeAmount() : glimCharge
                                        .getRevisedFeeAmount();
                                BigDecimal paidClientCharge = glim.getPaidChargeAmount() == null ? BigDecimal.ZERO : Money.of(
                                        currency,
                                        GroupLoanIndividualMonitoringTransactionAssembler.getShare(zeroIfNull(glim.getPaidChargeAmount()),
                                                totalCharge, glim.getChargeAmount(), currency)).getAmount();
                                BigDecimal perInstallmentCharge = Money.of(currency,
                                        BigDecimal.valueOf(totalCharge.doubleValue() / numberOfInstallment.doubleValue())).getAmount();
                                /*BigDecimal perInstallmentCharge = Money.of(currency, getDefaultChargeSharePerInstallment(loan, glimId, glim.getChargeAmount(), 
                                        ));*/
                                BigDecimal paidInstallmentForharge = Money.of(currency,
                                        BigDecimal.valueOf(paidClientCharge.doubleValue() / perInstallmentCharge.doubleValue()))
                                        .getAmount();
                                BigDecimal partialPaidCharge = BigDecimal.ZERO;
                                Double diff = paidClientCharge.doubleValue()
                                        - (perInstallmentCharge.doubleValue() * Double.valueOf(paidInstallmentForharge.intValue() + ""));
                                BigDecimal amount = perInstallmentCharge.subtract(BigDecimal.valueOf(diff));
                                boolean isPartialPaid = diff > 0;

                                for (int k = paidInstallmentForharge.intValue() + 1; k <= numberOfInstallment; k++) {
                                    if (k == numberOfInstallment) {
                                        BigDecimal adjustedAmount = totalCharge.subtract(BigDecimal.valueOf(perInstallmentCharge
                                                .doubleValue() * Double.valueOf((numberOfInstallment - 1) + "")));
                                        amount = (isPartialPaid) ? adjustedAmount.subtract(BigDecimal.valueOf(diff)) : adjustedAmount;

                                    } else if (k == paidInstallmentForharge.intValue() + 1) {
                                        amount = amount;
                                        isPartialPaid = false;
                                    } else {
                                        amount = perInstallmentCharge;
                                    }

                                    loanInstallmentNumber = k;
                                    final LoanTransaction waiveTransaction = loan.waiveGlimLoanCharge(loanCharge,
                                            defaultLoanLifecycleStateMachine(), changes, existingTransactionIds,
                                            existingReversedTransactionIds, loanInstallmentNumber, scheduleGeneratorDTO, accruedCharge,
                                            currentUser, Money.of(currency, amount));
                                    waiveLoanTransactions.add(waiveTransaction);

                                }
                            }
                        }

                        Money totalAmountWaived = Money.zero(currency);
                        Money totalFeeChargesWaived = Money.zero(currency);
                        Money totalpenaltyChargesWaived = Money.zero(currency);
                        Money unrecognizedIncome = Money.zero(currency);
                        for (LoanTransaction waiveLoanTransaction : waiveLoanTransactions) {
                            totalAmountWaived = totalAmountWaived.plus(waiveLoanTransaction.getAmount(currency));
                            totalFeeChargesWaived = totalFeeChargesWaived.plus(waiveLoanTransaction.getFeeChargesPortion(currency));
                            totalpenaltyChargesWaived = totalpenaltyChargesWaived.plus(waiveLoanTransaction
                                    .getPenaltyChargesPortion(currency));
                        }

                        final LoanTransaction waiveLoanChargeTransaction = LoanTransaction.waiveLoanCharge(loan, loan.getOffice(),
                                totalAmountWaived, loan.getDisbursementDate(), totalFeeChargesWaived, totalpenaltyChargesWaived,
                                unrecognizedIncome, DateUtils.getLocalDateTimeOfTenant(), currentUser);
                        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(waiveLoanChargeTransaction, loanCharge,
                                waiveLoanChargeTransaction.getAmount(currency).getAmount(), loanInstallmentNumber);
                        waiveLoanChargeTransaction.getLoanChargesPaid().add(loanChargePaidBy);
                        loan.getLoanTransactions().add(waiveLoanChargeTransaction);

                        loan.updateLoanSummarAndStatus();
                        this.loanTransactionRepository.save(waiveLoanChargeTransaction);

                        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

                        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WAIVE_CHARGE,
                                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));

                        glim.setWaivedChargeAmount(totalFeeChargesWaived.getAmount().add(zeroIfNull(glim.getWaivedChargeAmount())));
                        this.glimRepository.save(glim);
                        GroupLoanIndividualMonitoringChargeData glimChargeData = this.glimChargeReadPlatformService
                                .retrieveGLIMChargeByGlimIdAndChargeId(glimId, chargeId);
                        Long glimChargeDataId = glimChargeData.getId();
                        GroupLoanIndividualMonitoringCharge glimCharge = this.glimChargeRepositoryWrapper
                                .findOneWithNotFoundDetection(glimChargeDataId);
                        glimCharge.setWaivedChargeAmount(totalFeeChargesWaived.getAmount().add(
                                zeroIfNull(glimCharge.getWaivedChargeAmount())));
                        this.glimChargeRepositoryWrapper.save(glimCharge);

                    }
                }
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId).build();
    }

    private LoanCharge retrieveLoanChargeBy(final Long loanId, final Long loanChargeId) {
        final LoanCharge loanCharge = this.loanChargeRepository.findOne(loanChargeId);
        if (loanCharge == null) { throw new LoanChargeNotFoundException(loanChargeId); }

        if (loanCharge.hasNotLoanIdentifiedBy(loanId)) { throw new LoanChargeNotFoundException(loanChargeId, loanId); }
        return loanCharge;
    }

    private LoanLifecycleStateMachine defaultLoanLifecycleStateMachine() {
        final List<LoanStatus> allowedLoanStatuses = Arrays.asList(LoanStatus.values());
        return new DefaultLoanLifecycleStateMachine(allowedLoanStatuses);
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private void postJournalEntries(final Loan loan, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds) {

        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
    }

    private void saveLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                }
            }
            this.loanRepository.save(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }


    private void checkClientOrGroupActive(final Loan loan) {
        final Client client = loan.client();
        if (client != null) {
            if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
        }
        final Group group = loan.group();
        if (group != null) {
            if (group.isNotActive()) { throw new GroupNotActiveException(group.getId()); }
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult waiveInterest(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateTransaction(command.json());
        
        this.loanEventApiJsonValidator.validateGlimForWaiveInterest(command.json());
        
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        
        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, loan);
        loan.updateGlim(glimMembers);
        final String noteText = command.stringValueOfParameterNamed("note");
        final CommandProcessingResultBuilder builderResult = new CommandProcessingResultBuilder();
        LoanTransaction loanTransaction = this.loanAccountDomainService.waiveInterest(loan, builderResult, transactionDate,
                transactionAmount, noteText, changes, existingTransactionIds, existingReversedTransactionIds);
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.groupLoanIndividualMonitoringTransactionAssembler.waiveInterestForClients(command, loanTransaction);
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.saveAsList(glimTransactions);
        
        return builderResult.withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .with(changes) //
                .build();
    }
    
    public static BigDecimal zeroIfNull(BigDecimal amount){
    	return (amount==null)?BigDecimal.ZERO:amount;
    }

    @Override
    public CommandProcessingResult writeOff(Long loanId, JsonCommand command) {

        this.loanEventApiJsonValidator.validateGlimForWriteOff(command.json());

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        if (command.hasParameter("writeoffReasonId")) {
            Long writeoffReasonId = command.longValueOfParameterNamed("writeoffReasonId");
            CodeValue writeoffReason = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                    LoanApiConstants.WRITEOFFREASONS, writeoffReasonId);
            changes.put("writeoffReasonId", writeoffReasonId);
            loan.updateWriteOffReason(writeoffReason);
        }

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        final String noteText = command.stringValueOfParameterNamed("note");
        final CommandProcessingResultBuilder builderResult = new CommandProcessingResultBuilder();
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, loan);
        loan.updateGlim(glimMembers);
        LoanTransaction loanTransaction = this.loanAccountDomainService.writeOffForGlimLoan(command, loan, builderResult, noteText,
                changes, existingTransactionIds, existingReversedTransactionIds);
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.groupLoanIndividualMonitoringTransactionAssembler.writeOffForClients(command, loanTransaction);
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.saveAsList(glimTransactions);
        this.groupLoanIndividualMonitoringTransactionAssembler.updateLoanWriteOffStatusForGLIM(loan);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .with(changes) //
                .build();
    }
    
}
