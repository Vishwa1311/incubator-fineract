/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * All monetary transactions against a loan are modelled through this entity.
 * Disbursements, Repayments, Waivers, Write-off etc
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "m_loan_transaction", uniqueConstraints = { @UniqueConstraint(columnNames = { "external_id" }, name = "external_id_UNIQUE") })
public final class LoanTransaction extends AbstractAuditableCustom<AppUser, Long>{
   
    private final static Logger logger = LoggerFactory.getLogger(LoanTransaction.class);
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_detail_id", nullable = true)
    private PaymentDetail paymentDetail;

    @Column(name = "transaction_type_enum", nullable = false)
    private final Integer typeOf;
    
    @Column(name = "transaction_sub_type_enum",nullable = true)
    private final Integer subTypeOf;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private final Date dateOf;

    @Temporal(TemporalType.DATE)
    @Column(name = "submitted_on_date", nullable = false)
    private final Date submittedOnDate;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "principal_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal principalPortion;

    @Column(name = "interest_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestPortion;

    @Column(name = "fee_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeChargesPortion;

    @Column(name = "penalty_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyChargesPortion;

    @Column(name = "overpayment_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal overPaymentPortion;

    @Column(name = "unrecognized_income_portion", scale = 6, precision = 19, nullable = true)
    private BigDecimal unrecognizedIncomePortion;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loanTransaction", orphanRemoval = true)
    private Set<LoanChargePaidBy> loanChargesPaid = new HashSet<>();

    @Column(name = "outstanding_loan_balance_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal outstandingLoanBalance;

    @Column(name = "manually_adjusted_or_reversed", nullable = false)
    private boolean manuallyAdjustedOrReversed;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL,  orphanRemoval = true)
    @JoinColumn(name = "loan_transaction_id", referencedColumnName= "id" , nullable = false)
    private Set<LoanTransactionToRepaymentScheduleMapping> loanTransactionToRepaymentScheduleMappings = new HashSet<>();

    protected LoanTransaction() {
        this.loan = null;
        this.dateOf = null;
        this.typeOf = null;
        this.subTypeOf = null;
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    public static LoanTransaction disbursement(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate disbursementDate, final String externalId) {
        final Integer loanTransactionSubType = null; 
        return new LoanTransaction(null, office, LoanTransactionType.DISBURSEMENT, loanTransactionSubType,paymentDetail, amount.getAmount(), disbursementDate,
                externalId);
    }

    public static LoanTransaction repayment(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT, loanTransactionSubType,paymentDetail, amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction recoveryRepayment(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.RECOVERY_REPAYMENT, loanTransactionSubType,paymentDetail, amount.getAmount(), paymentDate,
                externalId);
    }

    public static LoanTransaction loanPayment(final Loan loan, final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId, final LoanTransactionType transactionType) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, transactionType, loanTransactionSubType,paymentDetail, amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction repaymentAtDisbursement(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT_AT_DISBURSEMENT, loanTransactionSubType,paymentDetail, amount.getAmount(),
                paymentDate, externalId);
    }

    public static LoanTransaction waiver(final Office office, final Loan loan, final Money amount, final LocalDate waiveDate,
            final Money waived, final Money unrecognizedPortion) {
        final Integer loanTransactionSubType = null;
        LoanTransaction loanTransaction = new LoanTransaction(loan, office, LoanTransactionType.WAIVE_INTEREST, loanTransactionSubType, amount.getAmount(),
                waiveDate, null);
        loanTransaction.updateInterestComponent(waived, unrecognizedPortion);
        return loanTransaction;
    }

    public static LoanTransaction accrueInterest(final Office office, final Loan loan, final Money amount,
            final LocalDate interestAppliedDate) {
        BigDecimal principalPortion = null;
        BigDecimal feesPortion = null;
        BigDecimal penaltiesPortion = null;
        BigDecimal interestPortion = amount.getAmount();
        BigDecimal overPaymentPortion = null;
        boolean reversed = false;
        PaymentDetail paymentDetail = null;
        String externalId = null;
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.ACCRUAL.getValue(), loanTransactionSubType,interestAppliedDate.toDate(), interestPortion,
                principalPortion, interestPortion, feesPortion, penaltiesPortion, overPaymentPortion, reversed, paymentDetail, externalId);
    }

    public static LoanTransaction initiateTransfer(final Office office, final Loan loan, final LocalDate transferDate) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.INITIATE_TRANSFER.getValue(), loanTransactionSubType,transferDate.toDateTimeAtStartOfDay()
                .toDate(), loan.getSummary().getTotalOutstanding(), loan.getSummary().getTotalPrincipalOutstanding(), loan.getSummary()
                .getTotalInterestOutstanding(), loan.getSummary().getTotalFeeChargesOutstanding(), loan.getSummary()
                .getTotalPenaltyChargesOutstanding(), null, false, null, null);
    }

    public static LoanTransaction approveTransfer(final Office office, final Loan loan, final LocalDate transferDate) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.APPROVE_TRANSFER.getValue(), loanTransactionSubType,transferDate.toDateTimeAtStartOfDay()
                .toDate(), loan.getSummary().getTotalOutstanding(), loan.getSummary().getTotalPrincipalOutstanding(), loan.getSummary()
                .getTotalInterestOutstanding(), loan.getSummary().getTotalFeeChargesOutstanding(), loan.getSummary()
                .getTotalPenaltyChargesOutstanding(), null, false, null, null);
    }

    public static LoanTransaction withdrawTransfer(final Office office, final Loan loan, final LocalDate transferDate) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.WITHDRAW_TRANSFER.getValue(), loanTransactionSubType,transferDate.toDateTimeAtStartOfDay()
                .toDate(), loan.getSummary().getTotalOutstanding(), loan.getSummary().getTotalPrincipalOutstanding(), loan.getSummary()
                .getTotalInterestOutstanding(), loan.getSummary().getTotalFeeChargesOutstanding(), loan.getSummary()
                .getTotalPenaltyChargesOutstanding(), null, false, null, null);
    }

    public static LoanTransaction refund(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REFUND, loanTransactionSubType,paymentDetail, amount.getAmount(), paymentDate, externalId);
    }
    
    public static LoanTransaction refundWithLoanTransactionSubType(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId, final Integer loanTransactionSubType) {
        return new LoanTransaction(null, office, LoanTransactionType.REFUND, loanTransactionSubType,paymentDetail, amount.getAmount(), paymentDate, externalId);
    }
    
    public static LoanTransaction copyTransactionProperties(final LoanTransaction loanTransaction) {
        return new LoanTransaction(loanTransaction.loan, loanTransaction.officeId, loanTransaction.typeOf, loanTransaction.subTypeOf,
                loanTransaction.dateOf, loanTransaction.amount, loanTransaction.principalPortion, loanTransaction.interestPortion,
                loanTransaction.feeChargesPortion, loanTransaction.penaltyChargesPortion, loanTransaction.overPaymentPortion,
                loanTransaction.reversed, loanTransaction.paymentDetail, loanTransaction.externalId, loanTransaction.getCreatedDate(),
                loanTransaction.getCreatedBy());
    }
    
    private LoanTransaction(final Loan loan, final Long officeId, final Integer typeOf, final Integer loanTransactionSubType,final Date dateOf, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overPaymentPortion, final boolean reversed,
            final PaymentDetail paymentDetail, final String externalId, final DateTime createdDate, final AppUser appUser) {
        this.loan = loan;
        this.typeOf = typeOf;
        this.subTypeOf = loanTransactionSubType;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overPaymentPortion = overPaymentPortion;
        this.reversed = reversed;
        this.paymentDetail = paymentDetail;
        this.officeId = officeId;
        this.externalId = externalId;
        this.submittedOnDate = DateUtils.getDateOfTenant();
        this.setCreatedDate(createdDate);
        this.setCreatedBy(appUser);
    }

    public static LoanTransaction accrueLoanCharge(final Loan loan, final Office office, final Money amount, final LocalDate applyDate,
            final Money feeCharges, final Money penaltyCharges) {
        String externalId = null;
        final Integer loanTransactionSubType = null;
        final LoanTransaction applyCharge = new LoanTransaction(loan, office, LoanTransactionType.ACCRUAL, loanTransactionSubType,amount.getAmount(), applyDate,
                externalId);
        applyCharge.updateChargesComponents(feeCharges, penaltyCharges);
        return applyCharge;
    }

    public static LoanTransaction refundForActiveLoan(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REFUND_FOR_ACTIVE_LOAN, loanTransactionSubType,paymentDetail, amount.getAmount(),
                paymentDate, externalId);
    }

    public static boolean transactionAmountsMatch(final MonetaryCurrency currency, final LoanTransaction loanTransaction,
            final LoanTransaction newLoanTransaction) {
        if (loanTransaction.getAmount(currency).isEqualTo(newLoanTransaction.getAmount(currency))
                && loanTransaction.getPrincipalPortion(currency).isEqualTo(newLoanTransaction.getPrincipalPortion(currency))
                && loanTransaction.getInterestPortion(currency).isEqualTo(newLoanTransaction.getInterestPortion(currency))
                && loanTransaction.getFeeChargesPortion(currency).isEqualTo(newLoanTransaction.getFeeChargesPortion(currency))
                && loanTransaction.getPenaltyChargesPortion(currency).isEqualTo(newLoanTransaction.getPenaltyChargesPortion(currency))
                && loanTransaction.getOverPaymentPortion(currency).isEqualTo(newLoanTransaction.getOverPaymentPortion(currency))) { return true; }
        return false;
    }

    private LoanTransaction(final Loan loan, final Office office, final Integer typeOf, final Integer loanTransactionSubType,final Date dateOf, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overPaymentPortion, final boolean reversed,
            final PaymentDetail paymentDetail, final String externalId, final LocalDateTime createdDate, final AppUser appUser) {
        this.loan = loan;
        this.typeOf = typeOf;
        this.subTypeOf = loanTransactionSubType;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overPaymentPortion = overPaymentPortion;
        this.reversed = reversed;
        this.paymentDetail = paymentDetail;
        if(office != null){
            this.officeId = office.getId();
        }
        
        this.externalId = externalId;
        this.submittedOnDate = DateUtils.getDateOfTenant();
        this.setCreatedDate(createdDate.toDateTime());
        this.setCreatedBy(appUser);
    }
    
    private LoanTransaction(final Loan loan, final Office office, final Integer typeOf, final Integer loanTransactionSubType,final Date dateOf, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overPaymentPortion, final boolean reversed,
            final PaymentDetail paymentDetail, final String externalId) {
        this.loan = loan;
        this.typeOf = typeOf;
        this.subTypeOf = loanTransactionSubType;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overPaymentPortion = overPaymentPortion;
        this.reversed = reversed;
        this.paymentDetail = paymentDetail;
        if(office != null){
        	this.officeId = office.getId();
        }
        this.externalId = externalId;
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    public static LoanTransaction waiveLoanCharge(final Loan loan, final Office office, final Money waived, final LocalDate waiveDate,
            final Money feeChargesWaived, final Money penaltyChargesWaived, final Money unrecognizedCharge) {
        final Integer loanTransactionSubType = null;
        final LoanTransaction waiver = new LoanTransaction(loan, office, LoanTransactionType.WAIVE_CHARGES, loanTransactionSubType,waived.getAmount(), waiveDate,
                null);
        waiver.updateChargesComponents(feeChargesWaived, penaltyChargesWaived, unrecognizedCharge);

        return waiver;
    }

    public static LoanTransaction writeoff(final Loan loan, final Office office, final LocalDate writeOffDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.WRITEOFF, loanTransactionSubType,null, writeOffDate, externalId);
    }

    private LoanTransaction(final Loan loan, final Office office, final LoanTransactionType type, final Integer transactionSubType,final BigDecimal amount,
            final LocalDate date, final String externalId) {
        this.loan = loan;
        this.typeOf = type.getValue();
        this.subTypeOf = transactionSubType;
        this.amount = amount;
        this.dateOf = date.toDateTimeAtStartOfDay().toDate();
        this.externalId = externalId;
        if(office != null){
            this.officeId = office.getId();
        }
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    
    private LoanTransaction(final Loan loan, final Office office, final LoanTransactionType type, final Integer transactionSubType,final PaymentDetail paymentDetail,
            final BigDecimal amount, final LocalDate date, final String externalId) {
        this.loan = loan;
        this.typeOf = type.getValue();
        this.subTypeOf = transactionSubType;
        this.paymentDetail = paymentDetail;
        this.amount = amount;
        this.dateOf = date.toDateTimeAtStartOfDay().toDate();
        this.externalId = externalId;
        if(office != null){
            this.officeId = office.getId();
        }
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }
    
    public void reverse() {
    	if(this.isAnyTypeOfRepayment() && !this.isManuallyAdjustedOrReversed()){
	    	logger.debug("Reversal for Loan[" + this.loan.getId() + "] transactionId[" + this.getId() + "]");
	    	StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			for(StackTraceElement element: stackTraceElements){
				logger.debug(element.toString());
			}
    	}
        this.reversed = true;
        this.loanTransactionToRepaymentScheduleMappings.clear();
    }

    public void resetDerivedComponents() {
        this.principalPortion = null;
        this.interestPortion = null;
        this.feeChargesPortion = null;
        this.penaltyChargesPortion = null;
        this.overPaymentPortion = null;
        this.outstandingLoanBalance = null;
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    /**
     * This updates the derived fields of a loan transaction for the principal,
     * interest and interest waived portions.
     * 
     * This accumulates the values passed to the already existent values for
     * each of the portions.
     */
    public void updateComponents(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
        final MonetaryCurrency currency = principal.getCurrency();
        this.principalPortion = defaultToNullIfZero(getPrincipalPortion(currency).plus(principal).getAmount());
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).plus(interest).getAmount());
        updateChargesComponents(feeCharges, penaltyCharges);
    }

    public void updateChargesComponents(final Money feeCharges, final Money penaltyCharges) {
        final MonetaryCurrency currency = feeCharges.getCurrency();
        this.feeChargesPortion = defaultToNullIfZero(getFeeChargesPortion(currency).plus(feeCharges).getAmount());
        this.penaltyChargesPortion = defaultToNullIfZero(getPenaltyChargesPortion(currency).plus(penaltyCharges).getAmount());
    }

    private void updateChargesComponents(final Money feeCharges, final Money penaltyCharges, final Money unrecognizedCharges) {
        final MonetaryCurrency currency = feeCharges.getCurrency();
        this.feeChargesPortion = defaultToNullIfZero(getFeeChargesPortion(currency).plus(feeCharges).getAmount());
        this.penaltyChargesPortion = defaultToNullIfZero(getPenaltyChargesPortion(currency).plus(penaltyCharges).getAmount());
        this.unrecognizedIncomePortion = defaultToNullIfZero(getUnrecognizedIncomePortion(currency).plus(unrecognizedCharges).getAmount());
    }

    private void updateInterestComponent(final Money interest, final Money unrecognizedInterest) {
        final MonetaryCurrency currency = interest.getCurrency();
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).plus(interest).getAmount());
        this.unrecognizedIncomePortion = defaultToNullIfZero(getUnrecognizedIncomePortion(currency).plus(unrecognizedInterest).getAmount());
    }

    public void adjustInterestComponent(final MonetaryCurrency currency) {
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).minus(getUnrecognizedIncomePortion(currency)).getAmount());
    }

    public void updateComponentsAndTotal(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
        updateComponents(principal, interest, feeCharges, penaltyCharges);

        final MonetaryCurrency currency = principal.getCurrency();
        this.amount = getPrincipalPortion(currency).plus(getInterestPortion(currency)).plus(getFeeChargesPortion(currency))
                .plus(getPenaltyChargesPortion(currency)).getAmount();
    }

    public void updateOverPayments(final Money overPayment) {
        final MonetaryCurrency currency = overPayment.getCurrency();
        this.overPaymentPortion = defaultToNullIfZero(getOverPaymentPortion(currency).plus(overPayment).getAmount());
    }

    public Money getPrincipalPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.principalPortion);
    }

    public BigDecimal getPrincipalPortion() {
        return this.principalPortion;
    }

    public Money getInterestPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.interestPortion);
    }

    public Money getUnrecognizedIncomePortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.unrecognizedIncomePortion);
    }

    public Money getFeeChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.feeChargesPortion);
    }

    public Money getPenaltyChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.penaltyChargesPortion);
    }

    public Money getOverPaymentPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.overPaymentPortion);
    }

    public Money getAmount(final MonetaryCurrency currency) {
        return Money.of(currency, this.amount);
    }

    public LocalDate getTransactionDate() {
        return new LocalDate(this.dateOf);
    }

    public Date getDateOf() {
        return this.dateOf;
    }

    public LoanTransactionType getTypeOf() {
        return LoanTransactionType.fromInt(this.typeOf);
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public boolean isNotReversed() {
        return !isReversed();
    }

    public boolean isAnyTypeOfRepayment() {
        return isRepayment() || isRepaymentAtDisbursement() || isRecoveryRepayment();
    }

    public boolean isRepayment() {
        return LoanTransactionType.REPAYMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotRepayment() {
        return !isRepayment();
    }

    public boolean isDisbursement() {
        return LoanTransactionType.DISBURSEMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isRepaymentAtDisbursement() {
        return LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotRecoveryRepayment() {
        return !isRecoveryRepayment();
    }

    public boolean isRecoveryRepayment() {
        return LoanTransactionType.RECOVERY_REPAYMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isInterestWaiver() {
        return LoanTransactionType.WAIVE_INTEREST.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isChargesWaiver() {
        return LoanTransactionType.WAIVE_CHARGES.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotInterestWaiver() {
        return !isInterestWaiver();
    }

    public boolean isWaiver() {
        return isInterestWaiver() || isChargesWaiver();
    }

    public boolean isNotWaiver() {
        return !isInterestWaiver() && !isChargesWaiver();
    }

    public boolean isChargePayment() {
        return getTypeOf().isChargePayment() && isNotReversed();
    }

    public boolean isPenaltyPayment() {
        boolean isPenalty = false;
        if (isChargePayment()) {
            for (final LoanChargePaidBy chargePaidBy : this.loanChargesPaid) {
                isPenalty = chargePaidBy.getLoanCharge().isPenaltyCharge();
                break;
            }
        }
        return isPenalty;
    }

    public boolean isWriteOff() {
        return getTypeOf().isWriteOff() && isNotReversed();
    }

    public boolean isIdentifiedBy(final Long identifier) {
        return getId().equals(identifier);
    }

    public boolean isBelongingToLoanOf(final Loan check) {
        return this.loan.getId().equals(check.getId());
    }

    public boolean isNotBelongingToLoanOf(final Loan check) {
        return !isBelongingToLoanOf(check);
    }

    public boolean isNonZero() {
        return this.amount.subtract(BigDecimal.ZERO).doubleValue() > 0;
    }

    public boolean isGreaterThan(final Money monetaryAmount) {
        return getAmount(monetaryAmount.getCurrency()).isGreaterThan(monetaryAmount);
    }

    public boolean isGreaterThanZero(final MonetaryCurrency currency) {
        return getAmount(currency).isGreaterThanZero();
    }

    public boolean isNotZero(final MonetaryCurrency currency) {
        return !getAmount(currency).isZero();
    }

    private BigDecimal defaultToNullIfZero(final BigDecimal value) {
        BigDecimal result = value;
        if (BigDecimal.ZERO.compareTo(value) == 0) {
            result = null;
        }
        return result;
    }

    public LoanTransactionData toData(final CurrencyData currencyData, final AccountTransferData transfer) {
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(this.typeOf);
        PaymentDetailData paymentDetailData = null;
        if (this.paymentDetail != null) {
            paymentDetailData = this.paymentDetail.toData();
        }
        String officeName = null;
        return new LoanTransactionData(getId(), this.officeId, officeName, transactionType, paymentDetailData,
                currencyData, getTransactionDate(), this.amount, this.principalPortion, this.interestPortion, this.feeChargesPortion,
                this.penaltyChargesPortion, this.overPaymentPortion, this.externalId, transfer, null, outstandingLoanBalance,
                this.unrecognizedIncomePortion, this.manuallyAdjustedOrReversed);
    }

    public Map<String, Object> toMapData(final CurrencyData currencyData) {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<>();

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(this.typeOf);

        thisTransactionData.put("id", getId());
        thisTransactionData.put("officeId", this.officeId);
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("subType", this.subTypeOf);
        thisTransactionData.put("reversed", Boolean.valueOf(isReversed()));
        thisTransactionData.put("date", getTransactionDate());
        thisTransactionData.put("currency", currencyData);
        thisTransactionData.put("amount", this.amount);
        thisTransactionData.put("principalPortion", this.principalPortion);
        thisTransactionData.put("interestPortion", this.interestPortion);
        thisTransactionData.put("feeChargesPortion", this.feeChargesPortion);
        thisTransactionData.put("penaltyChargesPortion", this.penaltyChargesPortion);
        thisTransactionData.put("overPaymentPortion", this.overPaymentPortion);

        if (this.paymentDetail != null) {
            thisTransactionData.put("paymentTypeId", this.paymentDetail.getPaymentType().getId());
        }

        if (!this.loanChargesPaid.isEmpty()) {
            final List<Map<String, Object>> loanChargesPaidData = new ArrayList<>();
            for (final LoanChargePaidBy chargePaidBy : this.loanChargesPaid) {
                final Map<String, Object> loanChargePaidData = new LinkedHashMap<>();
                loanChargePaidData.put("chargeId", chargePaidBy.getLoanCharge().getCharge().getId());
                loanChargePaidData.put("isPenalty", chargePaidBy.getLoanCharge().isPenaltyCharge());
                loanChargePaidData.put("loanChargeId", chargePaidBy.getLoanCharge().getId());
                loanChargePaidData.put("amount", chargePaidBy.getAmount());

                loanChargesPaidData.add(loanChargePaidData);
            }
            thisTransactionData.put("loanChargesPaid", loanChargesPaidData);
        }

        return thisTransactionData;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public Set<LoanChargePaidBy> getLoanChargesPaid() {
        return this.loanChargesPaid;
    }

    public void setLoanChargesPaid(final Set<LoanChargePaidBy> loanChargesPaid) {
        this.loanChargesPaid = loanChargesPaid;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public boolean isRefund() {
        return LoanTransactionType.REFUND.equals(getTypeOf()) && isNotReversed();
    }

    public void updateExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public boolean isAccrual() {
        return LoanTransactionType.ACCRUAL.equals(getTypeOf()) && isNotReversed();
    }
    
    public boolean isNonMonetaryTransaction(){
    	return isNotReversed() 
    			&& (LoanTransactionType.CONTRA.equals(getTypeOf())
    					|| LoanTransactionType.MARKED_FOR_RESCHEDULING.equals(getTypeOf())
    					|| LoanTransactionType.APPROVE_TRANSFER.equals(getTypeOf())
    					|| LoanTransactionType.INITIATE_TRANSFER.equals(getTypeOf())
    					|| LoanTransactionType.REJECT_TRANSFER.equals(getTypeOf())
    					|| LoanTransactionType.WITHDRAW_TRANSFER.equals(getTypeOf()));
    }

    public void updateOutstandingLoanBalance(BigDecimal outstandingLoanBalance) {
        this.outstandingLoanBalance = outstandingLoanBalance;
    }

    public boolean isNotRefundForActiveLoan() {
        // TODO Auto-generated method stub
        return !isRefundForActiveLoan();
    }

    public boolean isRefundForActiveLoan() {
        return LoanTransactionType.REFUND_FOR_ACTIVE_LOAN.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isManuallyAdjustedOrReversed() {
        return this.manuallyAdjustedOrReversed;
    }

    public boolean isNotManuallyAdjustedOrReversed() {
        return !this.manuallyAdjustedOrReversed;
    }

    public void manuallyAdjustedOrReversed() {
        this.manuallyAdjustedOrReversed = true;
    }

    

    public boolean isLastTransaction(final LoanTransaction loanTransaction) {
        boolean isLatest = false;
        if (loanTransaction != null) {
            isLatest = this.getTransactionDate().isBefore(loanTransaction.getTransactionDate())
                    || (this.getTransactionDate().isEqual(loanTransaction.getTransactionDate()) && this.getCreatedDate().isBefore(
                            loanTransaction.getCreatedDate()));
        }
        return isLatest;
    }

    public boolean isLatestTransaction(final LoanTransaction loanTransaction) {
        boolean isLatest = false;
        if (loanTransaction != null) {
            isLatest = this.getCreatedDate().isBefore(loanTransaction.getCreatedDate());
        }
        return isLatest;
    }

    public void updateLoanTransactionToRepaymentScheduleMappings(final Collection<LoanTransactionToRepaymentScheduleMapping> mappings) {
        Collection<LoanTransactionToRepaymentScheduleMapping> retainMappings = new ArrayList<>();
        for (LoanTransactionToRepaymentScheduleMapping updatedrepaymentScheduleMapping : mappings) {
            updateMapingDetail(retainMappings, updatedrepaymentScheduleMapping);
        }
        this.loanTransactionToRepaymentScheduleMappings.retainAll(retainMappings);
    }

    private boolean updateMapingDetail(final Collection<LoanTransactionToRepaymentScheduleMapping> retainMappings,
            final LoanTransactionToRepaymentScheduleMapping updatedrepaymentScheduleMapping) {
        boolean isMappingUpdated = false;
        for (LoanTransactionToRepaymentScheduleMapping repaymentScheduleMapping : this.loanTransactionToRepaymentScheduleMappings) {
            if (updatedrepaymentScheduleMapping.getLoanRepaymentScheduleInstallment().getId() != null
                    && repaymentScheduleMapping.getLoanRepaymentScheduleInstallment().getDueDate()
                            .equals(updatedrepaymentScheduleMapping.getLoanRepaymentScheduleInstallment().getDueDate())) {
                repaymentScheduleMapping.setComponents(updatedrepaymentScheduleMapping.getPrincipalPortion(),
                        updatedrepaymentScheduleMapping.getInterestPortion(), updatedrepaymentScheduleMapping.getFeeChargesPortion(),
                        updatedrepaymentScheduleMapping.getPenaltyChargesPortion());
                isMappingUpdated = true;
                retainMappings.add(repaymentScheduleMapping);
                break;
            }
        }
        if (!isMappingUpdated) {
            this.loanTransactionToRepaymentScheduleMappings.add(updatedrepaymentScheduleMapping);
            retainMappings.add(updatedrepaymentScheduleMapping);
        }
        return isMappingUpdated;
    }

    
    public Set<LoanTransactionToRepaymentScheduleMapping> getLoanTransactionToRepaymentScheduleMappings() {
        return this.loanTransactionToRepaymentScheduleMappings;
    }
        
    public Boolean isAllowTypeTransactionAtTheTimeOfLastUndo(){
    	return isDisbursement() || isAccrual() || isRepaymentAtDisbursement();
    }

	public Integer getSubTypeOf() {
		return subTypeOf;
	}   
}