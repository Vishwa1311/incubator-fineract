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
    private BigDecimal transactionAmount;
    private BigDecimal remainingTransactionAmount;

    public GroupLoanIndividualMonitoringData(final Long id, final Long loanId, final BigDecimal totalLoanAmount, final Long clientId,
            final String clientName, final String clientExternalID, final BigDecimal proposedAmount, final BigDecimal approvedAmount,
            final BigDecimal disbursedAmount, final CodeValueData loanPurpose, final Boolean isClientSelected,
            final BigDecimal chargeAmount, final BigDecimal adjustedAmount, final BigDecimal installmentAmount,
            final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount, final BigDecimal paidAmount,
            final BigDecimal interestAmount, final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount, final BigDecimal remainingTransactionAmount, BigDecimal transactionAmount) {
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
        this.remainingTransactionAmount = remainingTransactionAmount;
        this.transactionAmount = transactionAmount;
        /*// calculate unpaid interest
        if (this.paidInterestAmount != null && this.paidInterestAmount.compareTo(BigDecimal.ZERO) == 1) {
            this.totalUnpaidInterest = this.interestAmount.subtract(this.paidInterestAmount);
        }*/
    }

    public static GroupLoanIndividualMonitoringData instance(final Long id, final Long loanId, final BigDecimal totalLoanAmount,
            final Long clientId, final String clientName, final String clientExternalID, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValueData loanPurpose,
            final Boolean isClientSelected, final BigDecimal chargeAmount, final BigDecimal adjustedAmount,
            final BigDecimal installmentAmount, final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount,
            final BigDecimal paidAmount, final BigDecimal interestAmount,final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount) {
        BigDecimal totalUnpaidInterest = null;
        BigDecimal transactionAmount = null;
        return new GroupLoanIndividualMonitoringData(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID, proposedAmount,
                approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount, installmentAmount,
                totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount, paidChargeAmount, waivedInterestAmount, 
                waivedChargeAmount, totalUnpaidInterest, transactionAmount);
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
            BigDecimal transactionAmount) {
        final Long loanId = null;
        final BigDecimal totalLoanAmount = null;
        BigDecimal proposedAmount = null;
        BigDecimal approvedAmount = null;
        BigDecimal disbursedAmount = null;
        CodeValueData loanPurpose = null;
        Boolean isClientSelected = null;
        BigDecimal chargeAmount = null;
        BigDecimal adjustedAmount = null;
        BigDecimal installmentAmount = null;
        BigDecimal totalPaybleAmount = null;
        BigDecimal paidAmount = null;
        BigDecimal paidPrincipalAmount = null;
        BigDecimal paidChargeAmount = null;
        BigDecimal waivedInterestAmount = null;
        BigDecimal waivedChargeAmount = null;
        String clientExternalID = null;
        return new GroupLoanIndividualMonitoringData(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID, proposedAmount,
                approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount, installmentAmount,
                totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount, paidChargeAmount,
                waivedInterestAmount, waivedChargeAmount, remainingTransactionAmount, transactionAmount);
    }
    
}
