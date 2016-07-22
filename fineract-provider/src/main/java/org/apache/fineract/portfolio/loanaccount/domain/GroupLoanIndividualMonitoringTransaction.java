/**
 * 
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author vishwa
 *
 */
@Entity
@Table(name = "m_loan_glim_transaction")
public class GroupLoanIndividualMonitoringTransaction extends AbstractPersistable<Long> {

    /**
     * 
     */
    @ManyToOne
    @JoinColumn(name = "glim_id")
    private GroupLoanIndividualMonitoring groupLoanIndividualMonitoring;

    @OneToOne
    @JoinColumn(name = "loan_transaction_id")
    private LoanTransaction loanTransaction;

    @Column(name = "principal_portion", scale = 6, precision = 19, nullable = true)
    private BigDecimal principalPortion;

    @Column(name = "interest_portion", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestPortion;

    @Column(name = "fee_portion", scale = 6, precision = 19, nullable = true)
    private BigDecimal feePortion;

    @Column(name = "penalty_portion", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyPortion;

    @Column(name = "total_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalAmount;

    public GroupLoanIndividualMonitoringTransaction() {
        // TODO Auto-generated constructor stub
    }

    public GroupLoanIndividualMonitoringTransaction(final GroupLoanIndividualMonitoring groupLoanIndividualMonitoring,
            final LoanTransaction loanTransaction, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feePortion, final BigDecimal penaltyPortion, final BigDecimal totalAmount) {
        this.groupLoanIndividualMonitoring = groupLoanIndividualMonitoring;
        this.loanTransaction = loanTransaction;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feePortion = feePortion;
        this.penaltyPortion = penaltyPortion;
        this.feePortion = totalAmount;
    }

    public static GroupLoanIndividualMonitoringTransaction instance(final GroupLoanIndividualMonitoring groupLoanIndividualMonitoring,
            final LoanTransaction loanTransaction, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feePortion, final BigDecimal penaltyPortion, final BigDecimal totalAmount) {
        return new GroupLoanIndividualMonitoringTransaction(groupLoanIndividualMonitoring, loanTransaction, principalPortion,
                interestPortion, feePortion, penaltyPortion, totalAmount);
    }

    public static GroupLoanIndividualMonitoringTransaction instance(final GroupLoanIndividualMonitoring groupLoanIndividualMonitoring,
            final LoanTransaction loanTransaction) {
        return new GroupLoanIndividualMonitoringTransaction(groupLoanIndividualMonitoring, loanTransaction, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static GroupLoanIndividualMonitoringTransaction instance(
            final GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction, final BigDecimal principalPortion,
            final BigDecimal interestPortion, final BigDecimal feePortion, final BigDecimal penaltyPortion, final BigDecimal totalAmount) {
        return new GroupLoanIndividualMonitoringTransaction(groupLoanIndividualMonitoringTransaction.groupLoanIndividualMonitoring,
                groupLoanIndividualMonitoringTransaction.loanTransaction, principalPortion, interestPortion, feePortion, penaltyPortion,
                totalAmount);
    }

    public void updateComponents(BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feePortion,
            final BigDecimal penaltyPortion, final BigDecimal totalAmount) {
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feePortion = feePortion;
        this.penaltyPortion = penaltyPortion;
        this.totalAmount = totalAmount;
    }

    public GroupLoanIndividualMonitoring getGroupLoanIndividualMonitoring() {
        return this.groupLoanIndividualMonitoring;
    }

    public void setGroupLoanIndividualMonitoring(GroupLoanIndividualMonitoring groupLoanIndividualMonitoring) {
        this.groupLoanIndividualMonitoring = groupLoanIndividualMonitoring;
    }

    public LoanTransaction getLoanTransaction() {
        return this.loanTransaction;
    }

    public void setLoanTransaction(LoanTransaction loanTransaction) {
        this.loanTransaction = loanTransaction;
    }

    public BigDecimal getPrincipalPortion() {
        return this.principalPortion;
    }

    public void setPrincipalPortion(BigDecimal principalPortion) {
        this.principalPortion = principalPortion;
    }

    public BigDecimal getInterestPortion() {
        return this.interestPortion;
    }

    public void setInterestPortion(BigDecimal interestPortion) {
        this.interestPortion = interestPortion;
    }

    public BigDecimal getFeePortion() {
        return this.feePortion;
    }

    public void setFeePortion(BigDecimal feePortion) {
        this.feePortion = feePortion;
    }

    public BigDecimal getPenaltyPortion() {
        return this.penaltyPortion;
    }

    public void setPenaltyPortion(BigDecimal penaltyPortion) {
        this.penaltyPortion = penaltyPortion;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

}
