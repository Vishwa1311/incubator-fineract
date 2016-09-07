package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_glim_repayment_schedule")
public class LoanGlimRepaymentScheduleInstallment extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "glim_id")
    private GroupLoanIndividualMonitoring groupLoanIndividualMonitoring;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_repayment_schedule_id")
    private LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment;

    @Column(name = "principal_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal principal;

    @Column(name = "interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestCharged;

    @Column(name = "fee_charges_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeChargesCharged;

    @Column(name = "penalty_charges_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyCharges;

    protected LoanGlimRepaymentScheduleInstallment() {}

    private LoanGlimRepaymentScheduleInstallment(final GroupLoanIndividualMonitoring groupLoanIndividualMonitoring,
            final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment, final BigDecimal principal,
            final BigDecimal interestCharged, final BigDecimal feeChargesCharged, final BigDecimal penaltyCharges) {
        this.groupLoanIndividualMonitoring = groupLoanIndividualMonitoring;
        this.loanRepaymentScheduleInstallment = loanRepaymentScheduleInstallment;
        this.principal = principal;
        this.interestCharged = interestCharged;
        this.feeChargesCharged = feeChargesCharged;
        this.penaltyCharges = penaltyCharges;
    }

    public static LoanGlimRepaymentScheduleInstallment create(final GroupLoanIndividualMonitoring groupLoanIndividualMonitoring,
            final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment, final BigDecimal principal,
            final BigDecimal interestCharged, final BigDecimal feeChargesCharged, final BigDecimal penaltyCharges) {
        return new LoanGlimRepaymentScheduleInstallment(groupLoanIndividualMonitoring, loanRepaymentScheduleInstallment, principal,
                interestCharged, feeChargesCharged, penaltyCharges);
    }
}