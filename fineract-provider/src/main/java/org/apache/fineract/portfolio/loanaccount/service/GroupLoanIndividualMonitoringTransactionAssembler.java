package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.exception.ClientInstallmentNotEqualToTransactionAmountException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
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
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        BigDecimal transactionAmount =  command.bigDecimalValueOfParameterNamed(LoanApiConstants.transactionAmountParamName);
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                    .determineProcessor(loanTransaction.getLoan().transactionProcessingStrategy());
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                Long glimId = member.get(LoanApiConstants.idParameterName).getAsLong();
                individualTransactionAmount = member.get(LoanApiConstants.installmentAmountParamName).getAsBigDecimal();
                totalInstallmentAmount = totalInstallmentAmount.add(individualTransactionAmount);
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                        .findOneWithNotFoundDetection(glimId);
                GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                        .instance(groupLoanIndividualMonitoring, loanTransaction, loanTransaction.getTypeOf().getValue());
                loanRepaymentScheduleTransactionProcessor.handleGLIMRepayment(groupLoanIndividualMonitoringTransaction, individualTransactionAmount);
                groupLoanIndividualMonitoring.updateGlimTransaction(groupLoanIndividualMonitoringTransaction);
                glimTransactions.add(groupLoanIndividualMonitoringTransaction);
            }
            if(totalInstallmentAmount.compareTo(transactionAmount)!=0){
            	throw new ClientInstallmentNotEqualToTransactionAmountException();
            }
        }
        return glimTransactions;
    }
    
    public static BigDecimal getChargeSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount, Integer numberOfInstallments, MonetaryCurrency currency){

    	BigDecimal totalPaidAmount = glim.getTotalPaidAmount().add(transactionAmount);
    	Integer paidInstallment = BigDecimal.valueOf(totalPaidAmount.doubleValue()/glim.getInstallmentAmount().doubleValue()).intValue();
    	BigDecimal paidInstallmentAmount = (paidInstallment==0)?BigDecimal.ZERO:glim.getInstallmentAmount().multiply(BigDecimal.valueOf(Double.valueOf(paidInstallment.toString())));
        BigDecimal partialPaidAmountForInstallment = (paidInstallment==0)?totalPaidAmount:totalPaidAmount.subtract(paidInstallmentAmount);
        Boolean isPartialPaid = partialPaidAmountForInstallment.compareTo(BigDecimal.ZERO)>0;
    	Money installmentCharge = Money.of(currency, BigDecimal.valueOf(glim.getChargeAmount().doubleValue()/numberOfInstallments.doubleValue())) ;
    	if(paidInstallment.intValue()==numberOfInstallments.intValue()){
    		return glim.getChargeAmount().subtract(glim.getPaidChargeAmount());
    	}
    	BigDecimal paidInstallmentCharge = installmentCharge.getAmount().multiply(BigDecimal.valueOf(Double.valueOf(paidInstallment.toString())));
    	if(isPartialPaid){
    		return paidInstallmentCharge.add(installmentCharge.getAmount()).subtract(glim.getPaidChargeAmount());
    	}
    	return paidInstallmentCharge.subtract(glim.getPaidChargeAmount()) ;           	
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> waiveInterestForClients(final JsonCommand command,
            final LoanTransaction loanTransaction) {
        JsonArray clients = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        final Locale locale = command.extractLocale();
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (JsonElement element : clients) {
            final Long glimId = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.idParameterName, element);
            GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                    .findOneWithNotFoundDetection(glimId);
            
            final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed(LoanApiConstants.transactionAmountParamName, element, locale);
            BigDecimal totalInterestOutstandingOnLoan = BigDecimal.ZERO;
            BigDecimal paidInterestAmount = groupLoanIndividualMonitoring.getPaidInterestAmount();
            BigDecimal totalInterestAmount = groupLoanIndividualMonitoring.getInterestAmount();
            BigDecimal totalWaivedInterest = groupLoanIndividualMonitoring.getWaivedInterestAmount();
            if (paidInterestAmount != null && totalInterestAmount != null) {
                totalInterestOutstandingOnLoan = totalInterestAmount.subtract(paidInterestAmount);
            }

            if (totalInterestOutstandingOnLoan.compareTo(BigDecimal.ZERO) == 1) {
                if (transactionAmount.compareTo(totalInterestOutstandingOnLoan) == 1) {
                    final String errorMessage = "The amount of interest to waive cannot be greater than total interest outstanding on loan.";
                    throw new InvalidLoanStateTransitionException("waive.interest", "amount.exceeds.total.outstanding.interest",
                            errorMessage, transactionAmount, totalInterestOutstandingOnLoan);
                }
                //totalInterestOutstandingOnLoan = totalInterestOutstandingOnLoan.subtract(transactionAmount);
                totalWaivedInterest = totalWaivedInterest.add(transactionAmount);
                groupLoanIndividualMonitoring.setWaivedInterestAmount(totalWaivedInterest);
                GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                        .waiveInterest(groupLoanIndividualMonitoring, loanTransaction, transactionAmount, loanTransaction.getTypeOf().getValue());
                groupLoanIndividualMonitoring.updateGlimTransaction(groupLoanIndividualMonitoringTransaction);
                glimTransactions.add(groupLoanIndividualMonitoringTransaction);
            }

        }
        return glimTransactions;
    }
}
