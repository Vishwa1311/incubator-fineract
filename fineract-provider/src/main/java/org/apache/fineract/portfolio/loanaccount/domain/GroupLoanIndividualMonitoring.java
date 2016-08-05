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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.client.domain.Client;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_loan_glim")
public class GroupLoanIndividualMonitoring extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "proposed_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal proposedAmount;

    @Column(name = "approved_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal approvedAmount;

    @Column(name = "disbursed_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal disbursedAmount;

    @ManyToOne
    @JoinColumn(name = "loanpurpose_cv_id", nullable = true)
    private CodeValue loanPurpose;

    @Column(name = "is_client_selected", nullable = true)
    private Boolean isClientSelected;

    @Column(name = "charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal chargeAmount;

    @Column(name = "adjusted_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal adjustedAmount;

    @Column(name = "installment_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal installmentAmount;

    @Column(name = "total_payble_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalPaybleAmount;

    @Column(name = "paid_interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidInterestAmount;

    @Column(name = "paid_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalPaidAmount;

    @Column(name = "interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestAmount;
    
    @Column(name = "percentage", scale = 6, precision = 19, nullable = true)
    private BigDecimal percentage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupLoanIndividualMonitoring", orphanRemoval = true)
    private Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharge = new HashSet<>();
    
    @Column(name = "paid_principal_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidPrincipalAmount;

    @Column(name = "paid_charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidChargeAmount;

    @Column(name = "waived_interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal waivedInterestAmount;

    @Column(name = "waived_charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal waivedChargeAmount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupLoanIndividualMonitoring", orphanRemoval = true)
    private Set<GroupLoanIndividualMonitoringTransaction> groupLoanIndividualMonitoringTransactions = new HashSet<>();

    public GroupLoanIndividualMonitoring() {
        
    }

    public GroupLoanIndividualMonitoring(final Loan loan, final Client client) {
        this.loan = loan;
        this.client = client;
    }

    public GroupLoanIndividualMonitoring(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            final Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharge, final BigDecimal percentage) {
        this.loan = loan;
        this.client = client;
        this.proposedAmount = proposedAmount;
        this.approvedAmount = approvedAmount;
        this.disbursedAmount = disbursedAmount;
        this.loanPurpose = loanPurpose;
        this.isClientSelected = isClientSelected;
        this.chargeAmount = null;
        this.paidInterestAmount = null;
        this.totalPaybleAmount = null;
        this.installmentAmount = null;
        this.adjustedAmount = null;
        this.totalPaidAmount = null;
        this.interestAmount = null;
        this.groupLoanIndividualMonitoringCharge = groupLoanIndividualMonitoringCharge;
        this.percentage = null;
        this.paidPrincipalAmount = null;
        this.paidChargeAmount = null;
        this.waivedInterestAmount = null;
        this.waivedChargeAmount = null;
    }

    public static GroupLoanIndividualMonitoring createDefaultInstance(final Loan loan, final Client client) {
        return new GroupLoanIndividualMonitoring(loan, client);
    }

    public static GroupLoanIndividualMonitoring createInstance(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected, Set<GroupLoanIndividualMonitoringCharge> clientCharges) {
        BigDecimal percentage = null;
        return new GroupLoanIndividualMonitoring(loan, client, proposedAmount, approvedAmount, disbursedAmount, loanPurpose,
                isClientSelected, clientCharges, percentage);
    }

    public static GroupLoanIndividualMonitoring createInstance(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            final BigDecimal adjustedAmount, final BigDecimal installmentAmount, final BigDecimal totalPaybleAmount,
            final BigDecimal paidInterestAmount, final BigDecimal totalPaidAmount, final BigDecimal interestAmount,
            final Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharges,final BigDecimal percentage,final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount) {
        final BigDecimal chargeAmount = null;
        return new GroupLoanIndividualMonitoring(loan, client, proposedAmount, approvedAmount, disbursedAmount, loanPurpose,
                isClientSelected, chargeAmount, adjustedAmount, installmentAmount, totalPaybleAmount, paidInterestAmount, totalPaidAmount,
                interestAmount, groupLoanIndividualMonitoringCharges, percentage, paidPrincipalAmount, paidChargeAmount, waivedInterestAmount, waivedChargeAmount);
    }

    public GroupLoanIndividualMonitoring(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            final BigDecimal chargeAmount, final BigDecimal adjustedAmount, final BigDecimal installmentAmount,
            final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount, final BigDecimal paidAmount,
            final BigDecimal interestAmount, final Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharge,
            final BigDecimal percentage,final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount) {
        this.loan = loan;
        this.client = client;
        this.proposedAmount = proposedAmount;
        this.approvedAmount = approvedAmount;
        this.disbursedAmount = disbursedAmount;
        this.loanPurpose = loanPurpose;
        this.isClientSelected = isClientSelected;
        this.chargeAmount = chargeAmount;
        this.adjustedAmount = adjustedAmount;
        this.installmentAmount = installmentAmount;
        this.totalPaybleAmount = totalPaybleAmount;
        this.paidInterestAmount = paidInterestAmount;
        this.totalPaidAmount = paidAmount;
        this.interestAmount = interestAmount;
        this.groupLoanIndividualMonitoringCharge = groupLoanIndividualMonitoringCharge;
        this.percentage = percentage;
        this.paidPrincipalAmount = paidPrincipalAmount;
        this.paidChargeAmount = paidChargeAmount;
        this.waivedInterestAmount = waivedInterestAmount;
        this.waivedChargeAmount = waivedChargeAmount;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Client getClient() {
        return this.client;
    }

    public BigDecimal getProposedAmount() {
        return this.proposedAmount;
    }

    public void setProposedAmount(BigDecimal proposedAmount) {
        this.proposedAmount = proposedAmount;
    }

    public BigDecimal getApprovedAmount() {
        return this.approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getDisbursedAmount() {
        return this.disbursedAmount;
    }

    public void setDisbursedAmount(BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    public CodeValue getLoanPurpose() {
        return this.loanPurpose;
    }

    public void setLoanPurpose(CodeValue loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Boolean isClientSelected() {
        return isClientSelected;
    }

    public void setIsClientSelected(Boolean isClientSelected) {
        this.isClientSelected = isClientSelected;
    }

    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getAdjustedAmount() {
        return this.adjustedAmount;
    }

    public void setAdjustedAmount(BigDecimal adjustedAmount) {
        this.adjustedAmount = adjustedAmount;
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getTotalPaybleAmount() {
        return this.totalPaybleAmount;
    }

    public void setTotalPaybleAmount(BigDecimal totalPaybleAmount) {
        this.totalPaybleAmount = totalPaybleAmount;
    }

    public BigDecimal getPaidInterestAmount() {
        return this.paidInterestAmount;
    }

    public void setPaidInterestAmount(BigDecimal paidInterestAmount) {
        this.paidInterestAmount = paidInterestAmount;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public BigDecimal getTotalPaidAmount() {
        return this.totalPaidAmount;
    }

    public void setPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public BigDecimal getInterestAmount() {
        return this.interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }
    
    public Set<GroupLoanIndividualMonitoringCharge> getGroupLoanIndividualMonitoringCharges() {
        return this.groupLoanIndividualMonitoringCharge;
    }

    public void updatePercentage(BigDecimal updatedPercentage) {
        this.percentage = updatedPercentage;
    }

	public BigDecimal getPaidPrincipalAmount() {
		return this.paidPrincipalAmount;
	}

	public void setPaidPrincipalAmount(BigDecimal paidPrincipalAmount) {
		this.paidPrincipalAmount = paidPrincipalAmount;
	}

	public BigDecimal getPaidChargeAmount() {
		return this.paidChargeAmount;
	}

	public void setPaidChargeAmount(BigDecimal paidChargeAmount) {
		this.paidChargeAmount = paidChargeAmount;
	}

	public BigDecimal getWaivedInterestAmount() {
		return this.waivedInterestAmount;
	}

	public void setWaivedInterestAmount(BigDecimal waivedInterestAmount) {
		this.waivedInterestAmount = waivedInterestAmount;
	}

	public BigDecimal getWaivedChargeAmount() {
		return this.waivedChargeAmount;
	}

	public void setWaivedChargeAmount(BigDecimal waivedChargeAmount) {
		this.waivedChargeAmount = waivedChargeAmount;
	}    
        
    public void updateGlimTransaction(final GroupLoanIndividualMonitoringTransaction glimTransaction) {
        this.groupLoanIndividualMonitoringTransactions.add(glimTransaction);
    }

    public void undoGlimTransaction() {
        this.groupLoanIndividualMonitoringTransactions.clear();
    }

    public void resetDerievedComponents() {
        this.paidChargeAmount = BigDecimal.ZERO;
        this.paidInterestAmount = BigDecimal.ZERO;
        this.paidPrincipalAmount = BigDecimal.ZERO;
        this.totalPaidAmount = BigDecimal.ZERO;
        this.waivedChargeAmount = BigDecimal.ZERO;
        this.waivedInterestAmount = BigDecimal.ZERO;

    }

}
