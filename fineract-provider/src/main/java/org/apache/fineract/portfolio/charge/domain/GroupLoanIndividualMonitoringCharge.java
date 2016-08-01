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
package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_glim_charges")
public class GroupLoanIndividualMonitoringCharge extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "glim_id")
    private GroupLoanIndividualMonitoring groupLoanIndividualMonitoring;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "charge_id")
    private Charge charge;

    @Column(name = "fee_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeAmount;

    @Column(name = "revised_fee_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal revisedFeeAmount;

    @Transient
    private boolean emiRoundingGoalSeek;

    public GroupLoanIndividualMonitoringCharge(final GroupLoanIndividualMonitoring glim, final Client client, final Charge charge,
            final BigDecimal feeAmount, final BigDecimal revisedFeeAmount, final boolean emiRoundingGoalSeek) {
        this.groupLoanIndividualMonitoring = glim;
        this.client = client;
        this.charge = charge;
        this.feeAmount = feeAmount;
        this.revisedFeeAmount = revisedFeeAmount;
        this.emiRoundingGoalSeek = emiRoundingGoalSeek;
    }

    public GroupLoanIndividualMonitoringCharge() {

    }

    public static GroupLoanIndividualMonitoringCharge instance(final GroupLoanIndividualMonitoring glim, final Client client,
            final Charge charge, final BigDecimal feeAmount, final BigDecimal revisedFeeAmount, final boolean emiRoundingGoalSeek) {
        return new GroupLoanIndividualMonitoringCharge(glim, client, charge, feeAmount, revisedFeeAmount, emiRoundingGoalSeek);
    }

    public GroupLoanIndividualMonitoring getGlim() {
        return this.groupLoanIndividualMonitoring;
    }

    public void setGlim(GroupLoanIndividualMonitoring glim) {
        this.groupLoanIndividualMonitoring = glim;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Charge getCharge() {
        return this.charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public BigDecimal getFeeAmount() {
        return this.feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getRevisedFeeAmount() {
        return this.revisedFeeAmount;
    }

    public void setRevisedFeeAmount(BigDecimal revisedFeeAmount) {
        this.revisedFeeAmount = revisedFeeAmount;
    }
    
    public boolean isEmiRoundingGoalSeek() {
        return this.emiRoundingGoalSeek;
    }

}
