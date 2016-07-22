package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class GroupLoanIndividualMonitoringTransactionAssembler {

    private final FromJsonHelper fromApiJsonHelper;

    private final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepositoryWrapper;

    private final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionAssembler(final FromJsonHelper fromApiJsonHelper,
            final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepositoryWrapper,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.groupLoanIndividualMonitoringRepositoryWrapper = groupLoanIndividualMonitoringRepositoryWrapper;
        this.transactionProcessorFactory = transactionProcessorFactory;
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> assembleGLIMTransactions(final JsonCommand command,
            final LoanTransaction loanTransaction) {
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        BigDecimal individualTransactionAmount = BigDecimal.ZERO;
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                    .determineProcessor(loanTransaction.getLoan().transactionProcessingStrategy());
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                Long glimId = member.get(LoanApiConstants.glimIdParamName).getAsLong();
                individualTransactionAmount = member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal();
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                        .findOneWithNotFoundDetection(glimId);
                GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                        .instance(groupLoanIndividualMonitoring, loanTransaction);
                loanRepaymentScheduleTransactionProcessor.handleGLIMRepayment(groupLoanIndividualMonitoringTransaction, individualTransactionAmount);
                glimTransactions.add(groupLoanIndividualMonitoringTransaction);
            }
        }
        return glimTransactions;
    }
}
