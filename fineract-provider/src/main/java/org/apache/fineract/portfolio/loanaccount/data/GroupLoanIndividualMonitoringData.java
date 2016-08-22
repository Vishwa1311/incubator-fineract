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
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

@SuppressWarnings("unused")
public class GroupLoanIndividualMonitoringData {

    private final Long id;
    private final Long loanId;
    private final BigDecimal totalLoanAmount;
    private final Long clientId;
    private final String clientName;
    private final String clientExternalID;
    private BigDecimal proposedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal disbursedAmount;
    private CodeValueData loanPurpose;
    private Boolean isClientSelected;
    private BigDecimal chargeAmount;
    private BigDecimal adjustedAmount;
    private BigDecimal installmentAmount;
    private BigDecimal totalPaybleAmount;
    private BigDecimal paidInterestAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestAmount;
    private BigDecimal paidPrincipalAmount;
    private BigDecimal paidChargeAmount;
    private BigDecimal waivedInterestAmount;
    private BigDecimal waivedChargeAmount;
    private BigDecimal principalWrittenOffAmount;
    private BigDecimal interestWrittenOffAmount;
    private BigDecimal chargeWrittenOffAmount;
    private BigDecimal transactionAmount;
    private BigDecimal remainingTransactionAmount;
    private Boolean isActive;

    public GroupLoanIndividualMonitoringData(final Long id, final Long loanId, final BigDecimal totalLoanAmount, final Long clientId,
            final String clientName, final String clientExternalID, final BigDecimal proposedAmount, final BigDecimal approvedAmount,
            final BigDecimal disbursedAmount, final CodeValueData loanPurpose, final Boolean isClientSelected,
            final BigDecimal chargeAmount, final BigDecimal adjustedAmount, final BigDecimal installmentAmount,
            final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount, final BigDecimal paidAmount,
            final BigDecimal interestAmount, final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount, BigDecimal principalWrittenOffAmount, BigDecimal interestWrittenOffAmount, BigDecimal chargeWrittenOffAmount,
            final BigDecimal remainingTransactionAmount, BigDecimal transactionAmount, Boolean isActive) {
        this.id = id;
        this.loanId = loanId;
        this.totalLoanAmount = totalLoanAmount;
        this.clientId = clientId;
        this.clientName = clientName;
        this.proposedAmount = proposedAmount;
        this.approvedAmount = approvedAmount;
        this.disbursedAmount = disbursedAmount;
        this.loanPurpose = loanPurpose;
        this.clientExternalID = clientExternalID;
        this.isClientSelected = isClientSelected;
        this.chargeAmount = chargeAmount;
        this.adjustedAmount = adjustedAmount;
        this.installmentAmount = installmentAmount;
        this.totalPaybleAmount = totalPaybleAmount;
        this.paidInterestAmount = paidInterestAmount;
        this.paidAmount = paidAmount;
        this.interestAmount = interestAmount;
        this.paidPrincipalAmount = paidPrincipalAmount;
        this.paidChargeAmount = paidChargeAmount;
        this.waivedInterestAmount = waivedInterestAmount;
        this.waivedChargeAmount = waivedChargeAmount;
        this.principalWrittenOffAmount = principalWrittenOffAmount;
        this.interestWrittenOffAmount = interestWrittenOffAmount;
        this.chargeWrittenOffAmount = chargeWrittenOffAmount;
        this.remainingTransactionAmount = remainingTransactionAmount;
        this.transactionAmount = transactionAmount;
        this.isActive = isActive;
    }

    public static GroupLoanIndividualMonitoringData instance(final Long id, final Long loanId, final BigDecimal totalLoanAmount,
            final Long clientId, final String clientName, final String clientExternalID, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValueData loanPurpose,
            final Boolean isClientSelected, final BigDecimal chargeAmount, final BigDecimal adjustedAmount,
            final BigDecimal installmentAmount, final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount,
            final BigDecimal paidAmount, final BigDecimal interestAmount,final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount, BigDecimal principalWrittenOffAmount, BigDecimal interestWrittenOffAmount, BigDecimal chargeWrittenOffAmount, 
            final BigDecimal remainingTransactionAmount, BigDecimal transactionAmount, Boolean isActive) {

        return new GroupLoanIndividualMonitoringData(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID, proposedAmount,
                approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount, installmentAmount,
                totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount, paidChargeAmount, waivedInterestAmount, 
                waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount, chargeWrittenOffAmount, remainingTransactionAmount, 
                transactionAmount, isActive);
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount;
    }

    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getInterestAmount() {
        return this.interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public static GroupLoanIndividualMonitoringData waiveInterestDetails(final Long id, final Long clientId, final String clientName,
            final BigDecimal paidInterestAmount, final BigDecimal interestAmount, BigDecimal remainingTransactionAmount, 
            BigDecimal transactionAmount, Boolean isClientSelected) {
        final Long loanId = null;
        final BigDecimal totalLoanAmount = null;
        BigDecimal proposedAmount = null;
        BigDecimal approvedAmount = null;
        BigDecimal disbursedAmount = null;
        CodeValueData loanPurpose = null;
        BigDecimal chargeAmount = null;
        BigDecimal adjustedAmount = null;
        BigDecimal installmentAmount = null;
        BigDecimal totalPaybleAmount = null;
        BigDecimal paidAmount = null;
        BigDecimal paidPrincipalAmount = null;
        BigDecimal paidChargeAmount = null;
        BigDecimal waivedInterestAmount = null;
        BigDecimal waivedChargeAmount = null;
        BigDecimal principalWrittenOffAmount = null;
        BigDecimal interestWrittenOffAmount = null;
        BigDecimal chargeWrittenOffAmount = null;
        String clientExternalID = null;
        Boolean isActive = null;
        return new GroupLoanIndividualMonitoringData(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID, proposedAmount,
                approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount, installmentAmount,
                totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount, paidChargeAmount,
                waivedInterestAmount, waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount, chargeWrittenOffAmount,
                remainingTransactionAmount, transactionAmount, isActive);
    }
    

    public Long getId() {
        return this.id;
    }

    public BigDecimal getDisbursedAmount() {
        return this.disbursedAmount;
    }

    public void setDisbursedAmount(BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    public BigDecimal getPaidChargeAmount() {
        return this.paidChargeAmount;
    }

    public void setPaidChargeAmount(BigDecimal paidChargeAmount) {
        this.paidChargeAmount = paidChargeAmount;
    }

    public BigDecimal getWaivedChargeAmount() {
        return this.waivedChargeAmount;
    }

    public void setWaivedChargeAmount(BigDecimal waivedChargeAmount) {
        this.waivedChargeAmount = waivedChargeAmount;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getRemainingTransactionAmount() {
        return this.remainingTransactionAmount;
    }

    public void setRemainingTransactionAmount(BigDecimal remainingTransactionAmount) {
        this.remainingTransactionAmount = remainingTransactionAmount;
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

    public BigDecimal getWaivedInterestAmount() {
        return this.waivedInterestAmount;
    }

    public void setWaivedInterestAmount(BigDecimal waivedInterestAmount) {
        this.waivedInterestAmount = waivedInterestAmount;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Boolean getIsClientSelected() {
        return this.isClientSelected;
    }

    public void setIsClientSelected(Boolean isClientSelected) {
        this.isClientSelected = isClientSelected;
    }

    public BigDecimal getPaidPrincipalAmount() {
        return this.paidPrincipalAmount;
    }
    
    
    public BigDecimal getPrincipalWrittenOffAmount() {
        return principalWrittenOffAmount;
    }

    
    public void setPrincipalWrittenOffAmount(BigDecimal principalWrittenOffAmount) {
        this.principalWrittenOffAmount = principalWrittenOffAmount;
    }

    
    public BigDecimal getInterestWrittenOffAmount() {
        return interestWrittenOffAmount;
    }

    
    public void setInterestWrittenOffAmount(BigDecimal interestWrittenOffAmount) {
        this.interestWrittenOffAmount = interestWrittenOffAmount;
    }

    
    public BigDecimal getChargeWrittenOffAmount() {
        return chargeWrittenOffAmount;
    }

    
    public void setChargeWrittenOffAmount(BigDecimal chargeWrittenOffAmount) {
        this.chargeWrittenOffAmount = chargeWrittenOffAmount;
    }


	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
