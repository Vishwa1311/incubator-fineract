package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.exception.ClientAlreadyWriteOffException;
import org.apache.fineract.portfolio.loanaccount.exception.ClientInstallmentNotEqualToTransactionAmountException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
import org.joda.time.LocalDate;
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
    private final GroupLoanIndividualMonitoringRepository glimRepository;
    private final LoanTransactionRepositoryWrapper loanTransactionRepositoryWrapper;
    private final GroupLoanIndividualMonitoringAssembler glimAssembler;
    private final LoanRepository loanRepository;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionAssembler(final FromJsonHelper fromApiJsonHelper,
            final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepositoryWrapper,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory,
            final GroupLoanIndividualMonitoringRepository glimRepository, final LoanTransactionRepositoryWrapper loanTransactionRepositoryWrapper, 
            final GroupLoanIndividualMonitoringAssembler glimAssembler, final LoanRepository loanRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.groupLoanIndividualMonitoringRepositoryWrapper = groupLoanIndividualMonitoringRepositoryWrapper;
        this.transactionProcessorFactory = transactionProcessorFactory;
        this.glimRepository = glimRepository;
        this.loanTransactionRepositoryWrapper = loanTransactionRepositoryWrapper; 
        this.glimAssembler = glimAssembler;
        this.loanRepository = loanRepository;
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
                individualTransactionAmount = member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal();
                if (isGreaterThanZero(individualTransactionAmount)) {
                    totalInstallmentAmount = totalInstallmentAmount.add(individualTransactionAmount);
                    GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                            .findOneWithNotFoundDetection(glimId);
                    GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                            .instance(groupLoanIndividualMonitoring, loanTransaction, loanTransaction.getTypeOf().getValue());
                    loanRepaymentScheduleTransactionProcessor.handleGLIMRepayment(groupLoanIndividualMonitoringTransaction, individualTransactionAmount);
                    /*groupLoanIndividualMonitoring.updateGlimTransaction(groupLoanIndividualMonitoringTransaction);*/
                    glimTransactions.add(groupLoanIndividualMonitoringTransaction);
                }
                
            }
            if(totalInstallmentAmount.compareTo(transactionAmount)!=0){
            	throw new ClientInstallmentNotEqualToTransactionAmountException();
            }
        }
        return glimTransactions;
    }
    
    public static BigDecimal getChargeSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount, Integer numberOfInstallments, MonetaryCurrency currency){

    	BigDecimal totalPaidAmount = glim.getTotalPaidAmount() == null ? BigDecimal.ZERO : glim.getTotalPaidAmount().add(transactionAmount);
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
    
    public static BigDecimal getInterestSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount,Loan loan, BigDecimal currentTransactionInterestPortion){
    	BigDecimal totalInterestToBePaidByGroup = currentTransactionInterestPortion;
        List<LoanRepaymentScheduleInstallment>  reaymentSscheduleList = loan.getRepaymentScheduleInstallments();
        MonetaryCurrency currency = loan.getCurrency();
        for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : reaymentSscheduleList) {
        	BigDecimal interestAmount = loanRepaymentScheduleInstallment.getInterestPaid(currency).getAmount();
			totalInterestToBePaidByGroup = totalInterestToBePaidByGroup.add(interestAmount);
		}
        BigDecimal totalPaidAmount = glim.getTotalPaidAmount().add(transactionAmount);
    	Integer paidInstallment = BigDecimal.valueOf(totalPaidAmount.doubleValue()/glim.getInstallmentAmount().doubleValue()).intValue();    	
        BigDecimal totalInterestToBePaidByClient = GroupLoanIndividualMonitoringAssembler.percentageOf(totalInterestToBePaidByGroup, BigDecimal.valueOf(glim.getDisbursedAmount().doubleValue()*100/loan.getApprovedPrincipal().doubleValue()));
        
        BigDecimal interestToBePaidByClient = totalInterestToBePaidByClient.subtract(glim.getPaidInterestAmount());
        Integer numberOfInstallments = loan.repaymentScheduleDetail().getNumberOfRepayments();
        if(paidInstallment==numberOfInstallments){
        	interestToBePaidByClient = glim.getInterestAmount().subtract(glim.getPaidInterestAmount());
        }
        
    	return interestToBePaidByClient;           	
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> waiveInterestForClients(final JsonCommand command,
            final LoanTransaction loanTransaction) {
        JsonArray clients = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        final Locale locale = command.extractLocale();
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (JsonElement element : clients) {
            final Boolean isClientSelected = this.fromApiJsonHelper.extractBooleanNamed(LoanApiConstants.isClientSelectedParamName, element);
            if (isClientSelected != null && isClientSelected) {
                final Long glimId = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.idParameterName, element);
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                        .findOneWithNotFoundDetection(glimId);
                
                BigDecimal writeOfAmount = zeroIfNull(groupLoanIndividualMonitoring.getPrincipalWrittenOffAmount()).add(zeroIfNull(groupLoanIndividualMonitoring.getInterestWrittenOffAmount())).add(zeroIfNull(groupLoanIndividualMonitoring.getChargeWrittenOffAmount()));
        		if(writeOfAmount.compareTo(BigDecimal.ZERO)>0 && groupLoanIndividualMonitoring.getTransactionAmount().compareTo(BigDecimal.ZERO)>0){
        			throw new ClientAlreadyWriteOffException();
        		}
                
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
        }
        return glimTransactions;
    }
    
    public List<GroupLoanIndividualMonitoringData> handleGLIMRepaymentTemplate(List<GroupLoanIndividualMonitoringData> glimData,
            LoanTransactionData loanTransactionData, Loan loan, Date transactionDate) {
        LocalDate transactionDateAsLocalDate = (transactionDate == null) ? new LocalDate() : new LocalDate(transactionDate);
        /*
         * List<GroupLoanIndividualMonitoring> listForAdujustment =
         * loan.getGroupLoanIndividualMonitoringList(); Long adjustedGlimId =
         * (listForAdujustment.get(listForAdujustment.size()-1)).getId();
         */
        transactionDateAsLocalDate = new LocalDate(transactionDate);
        List<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallment = loan.getRepaymentScheduleInstallments();
        MonetaryCurrency currency = loan.getCurrency();
        // Double numberOfRepayment =
        // loan.getLoanRepaymentScheduleDetail().getNumberOfRepayments().doubleValue();
        /*
         * BigDecimal totalLoanCharge = BigDecimal.ZERO; for(LoanCharge
         * loanCharge : loan.charges()){ totalLoanCharge =
         * totalLoanCharge.add(loanCharge.getAmount(currency).getAmount()); }
         */
        for (GroupLoanIndividualMonitoringData glimIndividualData : glimData) {
            BigDecimal writeOfAmount = zeroIfNull(glimIndividualData.getPrincipalWrittenOffAmount()).add(
                    zeroIfNull(glimIndividualData.getInterestWrittenOffAmount())).add(
                    zeroIfNull(glimIndividualData.getChargeWrittenOffAmount()));
            BigDecimal installmentAmount = BigDecimal.ZERO;
            if (!(writeOfAmount.compareTo(BigDecimal.ZERO) > 0)) {
                BigDecimal interestAmount = BigDecimal.ZERO;
                BigDecimal chargeAmount = BigDecimal.ZERO;
                BigDecimal waivedChargeAmount = BigDecimal.ZERO;
                BigDecimal waivedInterestAmount = BigDecimal.ZERO;
                BigDecimal defaultInstallmentAmount = glimIndividualData.getInstallmentAmount();
                Boolean isChargeWaived = ((zeroIfNull(glimIndividualData.getWaivedChargeAmount()) != BigDecimal.ZERO));
                Boolean isInterestWaived = ((zeroIfNull(glimIndividualData.getWaivedInterestAmount()) != BigDecimal.ZERO));

                for (int i = 0; i < loanRepaymentScheduleInstallment.size(); i++) {
                    LoanRepaymentScheduleInstallment scheduleInstallment = loanRepaymentScheduleInstallment.get(i);
                    LocalDate dueDate = scheduleInstallment.getDueDate();
                    BigDecimal installmentCharge = BigDecimal.ZERO;
                    BigDecimal installmentInterest = BigDecimal.ZERO;

                    if (dueDate.isBefore(transactionDateAsLocalDate) || dueDate.isEqual(transactionDateAsLocalDate) || i == 0) {
                        if (i + 1 == loanRepaymentScheduleInstallment.size()) {
                            installmentCharge = glimIndividualData.getChargeAmount().subtract(chargeAmount);
                            installmentInterest = glimIndividualData.getInterestAmount().subtract(interestAmount);
                        } else {
                            installmentCharge = getDefaultChargeSharePerInstallment(loan, glimIndividualData.getId(),
                                    glimIndividualData.getChargeAmount(), scheduleInstallment.getFeeChargesCharged(currency).getAmount(),
                                    loan.getSummary().getTotalFeeChargesCharged());

                            installmentInterest = getDefaultInterestSharePerInstallment(loan, glimIndividualData.getId(),
                                    glimIndividualData.getInterestAmount(), scheduleInstallment.getInterestCharged(currency).getAmount(),
                                    loan.getSummary().getTotalInterestCharged());
                        }

                        installmentAmount = installmentAmount.add(defaultInstallmentAmount);
                        chargeAmount = chargeAmount.add(installmentCharge);
                        interestAmount = interestAmount.add(installmentInterest);
                    } else {
                        break;
                    }
                }
                installmentAmount = installmentAmount.subtract(zeroIfNull(glimIndividualData.getPaidAmount()));
                if (isChargeWaived) {
                    waivedChargeAmount = chargeAmount.subtract(zeroIfNull(glimIndividualData.getPaidChargeAmount()));
                    waivedChargeAmount = waivedChargeAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : waivedChargeAmount;
                }
                if (isInterestWaived) {
                    waivedInterestAmount = interestAmount.subtract(zeroIfNull(glimIndividualData.getPaidInterestAmount()));
                    waivedInterestAmount = waivedInterestAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : waivedInterestAmount;
                }
                installmentAmount = installmentAmount.subtract(waivedChargeAmount).subtract(waivedInterestAmount);
                installmentAmount = installmentAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : installmentAmount;

            }
            glimIndividualData.setTransactionAmount(Money.of(currency, installmentAmount).getAmount());

        }

        return glimData;
    }
    
    public static BigDecimal zeroIfNull(BigDecimal amount){
    	return (amount==null)?BigDecimal.ZERO:amount;
    }
    
    public static BigDecimal getDefaultInterestSharePerInstallment(Loan loan, Long glimId, BigDecimal glimAmount, BigDecimal amount,
            BigDecimal totalAmount) {
        MonetaryCurrency currency = loan.getCurrency();
        List<GroupLoanIndividualMonitoring> listForAdujustment = loan.getDefautGlimMembers();
        Long adjustedGlimId = (listForAdujustment.get(listForAdujustment.size() - 1)).getId();
        if (adjustedGlimId != glimId) { return getShare(amount, glimAmount, totalAmount, currency); }
        BigDecimal othersShare = BigDecimal.ZERO;
        for (GroupLoanIndividualMonitoring indGlim : listForAdujustment) {
            if (indGlim.getId() != adjustedGlimId) {
                othersShare = othersShare.add(getShare(amount, indGlim.getInterestAmount(), totalAmount, currency));
            }
        }
        return amount.subtract(othersShare);

    }
    
    public static BigDecimal getDefaultChargeSharePerInstallment(Loan loan, Long glimId, BigDecimal glimAmount, BigDecimal amount,
            BigDecimal totalAmount) {
        MonetaryCurrency currency = loan.getCurrency();
        
        List<GroupLoanIndividualMonitoring> listForAdujustment = loan.getDefautGlimMembers();
        Long adjustedGlimId = (listForAdujustment.get(listForAdujustment.size() - 1)).getId();
        if (adjustedGlimId != glimId) { return getShare(amount, glimAmount, totalAmount, currency); }
        BigDecimal othersShare = BigDecimal.ZERO;
        for (GroupLoanIndividualMonitoring indGlim : listForAdujustment) {
            if (indGlim.getId() != adjustedGlimId) {
                othersShare = othersShare.add(getShare(amount, indGlim.getChargeAmount(), totalAmount, currency));
            }
        }
        return amount.subtract(othersShare);

}
    
    public static Map<String, BigDecimal> getSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount, Loan loan,
            Integer installmentNumber, Map<String, BigDecimal> installmentPaidMap, LoanTransaction loanTransaction) {

        BigDecimal totalPaidAmount = transactionAmount.add(zeroIfNull(glim.getTotalPaidAmount())).add(
                installmentPaidMap.get("installmentTransactionAmount"));
        MonetaryCurrency currency = loan.getCurrency();
        Integer numberOfInstallments = loan.getLoanRepaymentScheduleDetail().getNumberOfRepayments();
        List<LoanRepaymentScheduleInstallment> scheduleList = loan.getRepaymentScheduleInstallments();
        /*BigDecimal totalLoanCharge = BigDecimal.ZERO;
        for (LoanCharge loanCharge : loan.charges()) {
            totalLoanCharge = totalLoanCharge.add(loanCharge.getAmount(currency).getAmount());
        }*/
        /*List<GroupLoanIndividualMonitoring> listForAdujustment = loan.getGroupLoanIndividualMonitoringList();
        Long adjustedGlimId = (listForAdujustment.get(listForAdujustment.size()-1)).getId();*/
        
        BigDecimal paidCharge = BigDecimal.ZERO;
        BigDecimal paidInterest = BigDecimal.ZERO;
        BigDecimal paidPrincipal = BigDecimal.ZERO;

        BigDecimal glimPaidCharge = zeroIfNull(glim.getPaidChargeAmount()).add(installmentPaidMap.get("unpaidCharge"));
        BigDecimal glimPaidInterest = zeroIfNull(glim.getPaidInterestAmount()).add(installmentPaidMap.get("unpaidInterest"));
        BigDecimal glimPaidPrincipal = zeroIfNull(glim.getPaidPrincipalAmount()).add(installmentPaidMap.get("unpaidPrincipal"));
        BigDecimal adjustedPaidInterest = zeroIfNull(glim.getPaidInterestAmount()).add(installmentPaidMap.get("unpaidInterest"));
        BigDecimal adjustedPaidPrincipal = zeroIfNull(glim.getPaidPrincipalAmount()).add(installmentPaidMap.get("unpaidPrincipal"));
        BigDecimal adjustedPaidCharge = zeroIfNull(glim.getPaidChargeAmount()).add(installmentPaidMap.get("unpaidCharge"));

        Boolean isChargeWaived = isGreaterThanZero(glim.getWaivedChargeAmount());
        Boolean isInterestWaived = isGreaterThanZero(glim.getWaivedInterestAmount());
        for (int i = 0; i < scheduleList.size(); i++) {
            BigDecimal installmentAmount = glim.getInstallmentAmount();
            LoanRepaymentScheduleInstallment schedule = scheduleList.get(i);
            BigDecimal installmentCharge = getDefaultChargeSharePerInstallment(loan, glim.getId(), glim.getChargeAmount(), schedule.getFeeChargesCharged(currency).getAmount(), loan.getSummary().getTotalFeeChargesCharged());
            BigDecimal installmentInterest = getDefaultInterestSharePerInstallment(loan,glim.getId(),glim.getInterestAmount(),schedule.getInterestCharged(currency).getAmount(),loan.getSummary().getTotalInterestCharged());
            BigDecimal installmentPrincipal = installmentAmount.subtract(installmentInterest).subtract(installmentCharge);
            if (i + 1 == numberOfInstallments && installmentNumber == numberOfInstallments) {
                installmentInterest = glim.getInterestAmount().subtract(adjustedPaidInterest);
                installmentCharge = glim.getChargeAmount().subtract(adjustedPaidCharge);
                installmentPrincipal = glim.getDisbursedAmount().subtract(adjustedPaidPrincipal);
            }

            if (isGreaterThanZero(totalPaidAmount) && !loanTransaction.isInterestWaiver()) {
                if (isGreater(totalPaidAmount, installmentCharge)) {
                    if (isGreaterThanZero(glimPaidCharge)) {
                        if (isGreater(glimPaidCharge, installmentCharge)) {
                            glimPaidCharge = glimPaidCharge.subtract(installmentCharge);
                        } else {
                            if (!isChargeWaived) {
                                paidCharge = (i + 1 == numberOfInstallments) ? paidCharge = paidCharge.add(installmentCharge) : paidCharge
                                        .add(installmentCharge.subtract(glimPaidCharge));
                                glimPaidCharge = glimPaidCharge.subtract(glimPaidCharge);
                            } else {
                                installmentAmount = installmentAmount.subtract(installmentCharge.subtract(glimPaidCharge));
                            }
                        }
                        totalPaidAmount = totalPaidAmount.subtract(installmentCharge);
                    } else {
                        if (!isChargeWaived) {
                            paidCharge = paidCharge.add(installmentCharge);
                            totalPaidAmount = totalPaidAmount.subtract(installmentCharge);
                        } else {
                            installmentAmount = installmentAmount.subtract(installmentCharge);
                        }
                    }

                } else {

                    if (isGreaterThanZero(glimPaidCharge)) {
                        BigDecimal remaingCharge = totalPaidAmount.subtract(glimPaidCharge);
                        if (!isChargeWaived) {
                            paidCharge = paidCharge.add(remaingCharge);
                            totalPaidAmount = totalPaidAmount.subtract(totalPaidAmount);
                        } else {
                            totalPaidAmount = totalPaidAmount.subtract(glimPaidCharge);
                            installmentAmount = installmentAmount.subtract(installmentCharge.subtract(glimPaidCharge));
                        }
                        glimPaidCharge = glimPaidCharge.subtract(glimPaidCharge);
                    } else {
                        if (!isChargeWaived) {
                            paidCharge = totalPaidAmount;
                            totalPaidAmount = totalPaidAmount.subtract(totalPaidAmount);
                        } else {
                            installmentAmount = installmentAmount.subtract(installmentCharge);
                        }

                    }

                }
            }

            if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            if (isGreaterThanZero(totalPaidAmount) && !loanTransaction.isChargesWaiver()) {
                if (isGreater(totalPaidAmount, installmentInterest)) {
                    if (isGreaterThanZero(glimPaidInterest)) {
                        if (isGreater(glimPaidInterest, installmentInterest)) {
                            glimPaidInterest = glimPaidInterest.subtract(installmentInterest);
                            totalPaidAmount = totalPaidAmount.subtract(installmentInterest);
                        } else {
                            if (!isInterestWaived) {
                                paidInterest = (i + 1 == numberOfInstallments) ? paidInterest.add(installmentInterest) : paidInterest
                                        .add(installmentInterest.subtract(glimPaidInterest));
                                totalPaidAmount = totalPaidAmount.subtract(installmentInterest);
                                glimPaidInterest = glimPaidInterest.subtract(glimPaidInterest);
                            } else {
                                installmentAmount = installmentAmount.subtract(installmentInterest.subtract(glimPaidInterest));
                                totalPaidAmount = totalPaidAmount.subtract(glimPaidInterest);
                            }
                        }
                    } else {
                        if (!isInterestWaived) {
                            paidInterest = paidInterest.add(installmentInterest);
                            totalPaidAmount = totalPaidAmount.subtract(installmentInterest);
                        } else {
                            installmentAmount = installmentAmount.subtract(installmentInterest);
                        }
                    }

                } else {
                    if (isGreaterThanZero(glimPaidInterest)) {
                        BigDecimal remaingInterest = totalPaidAmount.subtract(glimPaidInterest);
                        if (!isInterestWaived) {
                            paidInterest = paidInterest.add(remaingInterest);
                            glimPaidInterest = glimPaidInterest.subtract(glimPaidInterest);
                            totalPaidAmount = totalPaidAmount.subtract(totalPaidAmount);
                        } else {
                            totalPaidAmount = totalPaidAmount.subtract(glimPaidInterest);
                            installmentAmount = installmentAmount.subtract(installmentInterest.subtract(glimPaidInterest));
                            glimPaidInterest = glimPaidInterest.subtract(glimPaidInterest);
                        }

                    } else {
                        if (!isInterestWaived) {
                            paidInterest = totalPaidAmount;
                            totalPaidAmount = totalPaidAmount.subtract(totalPaidAmount);
                        } else {
                            installmentAmount = installmentAmount.subtract(installmentInterest);
                        }

                    }

                }
            }
            if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            if (isGreaterThanZero(totalPaidAmount) && (loanTransaction.isRepayment() || loanTransaction.isWriteOff())) {
                if (isGreater(totalPaidAmount, installmentPrincipal)) {
                    if (isGreaterThanZero(glimPaidPrincipal)) {
                        if (isGreater(glimPaidPrincipal, installmentPrincipal)) {
                            glimPaidPrincipal = glimPaidPrincipal.subtract(installmentPrincipal);
                        } else {
                            paidPrincipal = (i + 1 == numberOfInstallments) ? paidPrincipal.add(installmentPrincipal) : paidPrincipal
                                    .add(installmentPrincipal.subtract(glimPaidPrincipal));
                            glimPaidPrincipal = glimPaidPrincipal.subtract(glimPaidPrincipal);
                        }
                    } else {
                        paidPrincipal = paidPrincipal.add(installmentPrincipal);
                    }
                    totalPaidAmount = totalPaidAmount.subtract(installmentPrincipal);

                } else {
                    if (isGreaterThanZero(glimPaidPrincipal)) {
                        paidPrincipal = paidPrincipal.add(totalPaidAmount.subtract(glimPaidPrincipal));
                        glimPaidPrincipal = glimPaidPrincipal.subtract(glimPaidPrincipal);
                    } else {
                        paidPrincipal = paidPrincipal.add(totalPaidAmount);
                    }

                }
            }

            if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0 || i + 1 == installmentNumber) {
                break;
            }

        }

        Map<String, BigDecimal> splitMap = new HashMap<String, BigDecimal>();
        splitMap.put("unpaidCharge", paidCharge);
        splitMap.put("unpaidInterest", paidInterest);
        splitMap.put("unpaidPrincipal", paidPrincipal);
        splitMap.put("installmentTransactionAmount", paidCharge.add(paidInterest).add(paidPrincipal));
        return splitMap;
    }


    public static BigDecimal getShare(BigDecimal givenValue, BigDecimal shareAmount, BigDecimal totalAmount, MonetaryCurrency currency) {
        Money amount = Money.of(currency, BigDecimal.valueOf((givenValue.multiply(shareAmount).doubleValue() / totalAmount.doubleValue())));
        return amount.getAmount();
    }

    public static Boolean isGreaterThanZero(BigDecimal amount) {
        return (amount == null) ? true : zeroIfNull(amount).compareTo(BigDecimal.ZERO) > 0;
    }

    public static Boolean isGreater(BigDecimal first, BigDecimal second) {
        return zeroIfNull(first).compareTo(zeroIfNull(second)) >= 0;
    }
    
    public void updateLoanWriteOffStatusForGLIM(Loan loan) {
        List<GroupLoanIndividualMonitoring> glimMembersForStatusUpdate = this.glimRepository.findByLoanIdAndIsClientSelected(loan.getId(), true);
        for (GroupLoanIndividualMonitoring glim : glimMembersForStatusUpdate) {
            if (glim.getIsActive() && glim.isClientSelected()) {
                BigDecimal totalAmountWrittenOff = zeroIfNull(glim.getPrincipalWrittenOffAmount()).add(zeroIfNull(glim.getInterestWrittenOffAmount())).add(zeroIfNull(glim.getChargeWrittenOffAmount()));
                BigDecimal outStandingAmount = zeroIfNull(glim.getTotalPaybleAmount()).subtract(zeroIfNull(glim.getTotalPaidAmount()))
                        .subtract(zeroIfNull(glim.getWaivedChargeAmount())).subtract(zeroIfNull(glim.getWaivedInterestAmount())).subtract(totalAmountWrittenOff);
                if (outStandingAmount.compareTo(BigDecimal.ZERO) == 0) {
                    glim.setIsActive(false);
                }
            }
        }
        this.glimRepository.save(glimMembersForStatusUpdate);
        Boolean isGlimWriteOff = this.glimAssembler.isGLIMApplicableForWriteOf(glimMembersForStatusUpdate);
        if (isGlimWriteOff) {
            loan.setLoanStatus(LoanStatus.CLOSED_WRITTEN_OFF.getValue());
            this.loanRepository.save(loan);
        }
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> writeOffForClients(final JsonCommand command,
            final LoanTransaction loanTransaction) {
        JsonArray clients = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        final Locale locale = command.extractLocale();
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (JsonElement element : clients) {
            final Boolean isClientSelected = this.fromApiJsonHelper
                    .extractBooleanNamed(LoanApiConstants.isClientSelectedParamName, element);
            if (isClientSelected != null && isClientSelected) {
                final Long glimId = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.idParameterName, element);
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                        .findOneWithNotFoundDetection(glimId);

                final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                        LoanApiConstants.transactionAmountParamName, element, locale);
                final BigDecimal principalWrittenOffAmount = groupLoanIndividualMonitoring.getDisbursedAmount().subtract(
                        zeroIfNull(groupLoanIndividualMonitoring.getPaidPrincipalAmount()));
                final BigDecimal interestWrittenOffAmount = groupLoanIndividualMonitoring.getInterestAmount().subtract(
                        zeroIfNull(groupLoanIndividualMonitoring.getPaidInterestAmount()));
                final BigDecimal chargeWrittenOffAmount = groupLoanIndividualMonitoring.getChargeAmount().subtract(
                        zeroIfNull(groupLoanIndividualMonitoring.getPaidChargeAmount()));
                groupLoanIndividualMonitoring.setPrincipalWrittenOffAmount(principalWrittenOffAmount);
                groupLoanIndividualMonitoring.setInterestWrittenOffAmount(interestWrittenOffAmount);
                groupLoanIndividualMonitoring.setChargeWrittenOffAmount(chargeWrittenOffAmount);
                GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                        .instance(groupLoanIndividualMonitoring, loanTransaction, loanTransaction.getTypeOf().getValue(),
                                principalWrittenOffAmount, interestWrittenOffAmount, chargeWrittenOffAmount, BigDecimal.ZERO,
                                transactionAmount);
                groupLoanIndividualMonitoring.updateGlimTransaction(groupLoanIndividualMonitoringTransaction);
                glimTransactions.add(groupLoanIndividualMonitoringTransaction);

            }
        }
        return glimTransactions;
    }

}
