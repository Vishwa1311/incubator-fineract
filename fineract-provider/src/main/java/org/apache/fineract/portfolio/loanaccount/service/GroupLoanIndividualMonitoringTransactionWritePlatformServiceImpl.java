package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanEventApiJsonValidator;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public GroupLoanIndividualMonitoringTransactionWritePlatformServiceImpl(final LoanWritePlatformService loanWritePlatformService,
            final GroupLoanIndividualMonitoringTransactionRepositoryWrapper groupLoanIndividualMonitoringTransactionRepositoryWrapper,
            final LoanAccountDomainService loanAccountDomainService, final LoanRepositoryWrapper loanRepositoryWrapper,
            final LoanEventApiJsonValidator loanEventApiJsonValidator, final LoanAssembler loanAssembler,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final GroupLoanIndividualMonitoringTransactionAssembler groupLoanIndividualMonitoringTransactionAssembler,
            final GroupLoanIndividualMonitoringAssembler glimAssembler, final GroupLoanIndividualMonitoringRepository glimRepository) {
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
        List<GroupLoanIndividualMonitoring> glimList = this.glimRepository.findByLoanId(loanId);
        loan.updateGlim(glimList);
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
        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .with(changes) //
                .build();
    }
}
