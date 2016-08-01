package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

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
                        .instance(groupLoanIndividualMonitoring, loanTransaction);
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
    	BigDecimal paidInstallmentCharge = installmentCharge.getAmount().multiply(BigDecimal.valueOf(Double.valueOf(paidInstallment.toString())));
    	if(isPartialPaid){
    		return paidInstallmentCharge.add(installmentCharge.getAmount()).subtract(glim.getPaidChargeAmount());
    	}
    	return paidInstallmentCharge.subtract(glim.getPaidChargeAmount()) ;
        /*Integer paidInstallment = glim.getTotalPaidAmount().divide(glim.getInstallmentAmount()).intValue();
        BigDecimal paidInstallmentAmount = (paidInstallment==0)?BigDecimal.ZERO:glim.getInstallmentAmount().multiply(BigDecimal.valueOf(Double.valueOf(paidInstallment.toString())));
        BigDecimal partialPaidAmountForInstallment = (paidInstallment==0)?glim.getTotalPaidAmount():glim.getTotalPaidAmount().subtract(paidInstallmentAmount);
        Boolean isPartialPaid = partialPaidAmountForInstallment.compareTo(BigDecimal.ZERO)>0;
        BigDecimal paidCharge = glim.getPaidChargeAmount();
        Integer currentInstallment = paidInstallment+1;
        Money installmentCharge = Money.of(currency, glim.getChargeAmount().divide(BigDecimal.valueOf(numberOfInstallments.longValue()))) ;
        Integer paidChargeForInstallment = (glim.getPaidChargeAmount().compareTo(BigDecimal.ZERO)>0)?0:glim.getPaidChargeAmount().divide(installmentCharge.getAmount()).intValue();
        BigDecimal paidInstallmentCharge = (paidChargeForInstallment==0)?BigDecimal.ZERO:installmentCharge.getAmount().multiply(BigDecimal.valueOf(Double.valueOf(paidChargeForInstallment.toString())));
        BigDecimal partialPaidChargeForInstallment = (paidChargeForInstallment==0)?glim.getPaidChargeAmount():glim.getPaidChargeAmount().subtract(paidInstallmentCharge);
        if(isPartialPaid){
        	
        }else{
        	if(transactionAmount.compareTo(glim.getInstallmentAmount())==0){
        		return installmentCharge.getAmount();
        	}else if(transactionAmount.compareTo(glim.getInstallmentAmount())>0){
        		
        	}else{
        		
        	}
        }
	*/	
    	
    }
    
    public static BigDecimal getInterestSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount, Integer numberOfInstallments, MonetaryCurrency currency, Money groupLevelInterstPortion, BigDecimal outstandingInterest){
    	return null;
    }
}
