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
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;
import org.apache.fineract.portfolio.tax.domain.TaxGroupMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class GroupLoanIndividualMonitoringAssembler {

    private final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepository;
    private final CodeValueRepository codeValueRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final ChargeRepositoryWrapper chargeRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public GroupLoanIndividualMonitoringAssembler(GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepository,
            CodeValueRepository codeValueRepository, final ClientRepositoryWrapper clientRepository,
            final ChargeRepositoryWrapper chargeRepository, final FromJsonHelper fromApiJsonHelper) {
        this.groupLoanIndividualMonitoringRepository = groupLoanIndividualMonitoringRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientRepository = clientRepository;
        this.chargeRepository = chargeRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void saveFromJson(final JsonCommand command, final Loan newLoanApplication) {
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                Long glimClientId = member.get(LoanApiConstants.idParamName).getAsLong();
                Client client = this.clientRepository.getActiveClientInUserScope(glimClientId);
                BigDecimal proposedAmount = null;
                Boolean isClientSelected = member.has(LoanApiConstants.isClientSelectedParamName)
                        && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean();
                if (member.has(LoanApiConstants.amountParamName)) {
                    proposedAmount = member.get(LoanApiConstants.amountParamName).getAsBigDecimal();
                }
                CodeValue loanPurpose = null;
                if (member.has(LoanApiConstants.loanPurposeIdParamName)) {
                    Long loanPurposeId = member.get(LoanApiConstants.loanPurposeIdParamName).getAsLong();
                    loanPurpose = this.codeValueRepository.findOne(loanPurposeId);
                }
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = GroupLoanIndividualMonitoring.createInstance(
                        newLoanApplication, client, proposedAmount, null, null, loanPurpose, isClientSelected, null);
                this.groupLoanIndividualMonitoringRepository.save(groupLoanIndividualMonitoring);
            }
        }
    }

    public List<GroupLoanIndividualMonitoring> updateFromJson(JsonElement element, String amountType, Loan loan, int numberOfRepayments,
            BigDecimal interestRate) {
        List<GroupLoanIndividualMonitoring> glimList = new ArrayList<>();
        if (this.fromApiJsonHelper.parameterExists(LoanApiConstants.clientMembersParamName, element)) {
            JsonArray clientMembers = this.fromApiJsonHelper.extractJsonArrayNamed(LoanApiConstants.clientMembersParamName, element);
            HashMap<Long, BigDecimal> chargesMap = new HashMap<>();
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                BigDecimal amount = null;
                BigDecimal percentage = BigDecimal.ZERO;
                BigDecimal totalInterest = BigDecimal.ZERO;
                Boolean isClientSelected = member.has(LoanApiConstants.isClientSelectedParamName)
                        && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean();
                Long glimId = member.get(LoanApiConstants.glimIdParamName).getAsLong();
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepository
                        .findOneWithNotFoundDetection(glimId);
                groupLoanIndividualMonitoring.setIsClientSelected(isClientSelected);
                if (isClientSelected) {
                    if (member.has(LoanApiConstants.amountParamName)) {
                        amount = member.get(LoanApiConstants.amountParamName).getAsBigDecimal();
                    }
                    if (member.has(LoanApiConstants.loanPurposeIdParamName)) {
                        Long loanPurposeId = member.get(LoanApiConstants.loanPurposeIdParamName).getAsLong();
                        CodeValue loanPurpose = this.codeValueRepository.findOne(loanPurposeId);
                        groupLoanIndividualMonitoring.setLoanPurpose(loanPurpose);
                    }
                    if (amountType.equalsIgnoreCase("proposedAmount")) {
                        groupLoanIndividualMonitoring.setProposedAmount(amount);
                    } else if (amountType.equalsIgnoreCase("approvedAmount")) {
                        groupLoanIndividualMonitoring.setApprovedAmount(amount);
                    } else {
                        groupLoanIndividualMonitoring.setDisbursedAmount(amount);
                    }

                    // total interest calculation
                    totalInterest = calculateTotalInterest(interestRate, numberOfRepayments, amount);
                    groupLoanIndividualMonitoring.setInterestAmount(totalInterest);

                    // calculate percentage of principal amount
                    percentage = calculatePercentageOfAmount(element, amount, percentage);
                    groupLoanIndividualMonitoring.updatePercentage(percentage);
                    Set<GroupLoanIndividualMonitoringCharge> glimCharges = groupLoanIndividualMonitoring
                            .getGroupLoanIndividualMonitoringCharges();
                    recalculateTotalFeeCharges(loan, chargesMap, amount, glimCharges);
                    glimList.add(groupLoanIndividualMonitoring);
                }

            }
            // update the recalculated charge amounts in loan charge obj
            updateLoanChargesForGlim(loan, chargesMap);
        }
        return glimList;
    }

    public void updateLoanChargesForGlim(Loan loan, HashMap<Long, BigDecimal> chargesMap) {
        for (LoanCharge loancharge : loan.charges()) {
            BigDecimal recalculatedChargeAmount = chargesMap.get(loancharge.getCharge().getId());
            loancharge.updateAmount(recalculatedChargeAmount);
        }
    }

    public void recalculateTotalFeeCharges(Loan loan, HashMap<Long, BigDecimal> chargesMap, BigDecimal amount,
            Set<GroupLoanIndividualMonitoringCharge> glimCharges) {
        for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
            BigDecimal feeCharge = percentageOf(amount, glimCharge.getCharge().getAmount());
            BigDecimal totalChargeAmount = feeCharge;
            totalChargeAmount = applyTaxComponentsOnCharges(glimCharge.getCharge(), feeCharge, totalChargeAmount);
            /*if (glimCharge.getCharge().isEmiRoundingGoalSeek()) {
                totalChargeAmount = BigDecimal.valueOf(Math.ceil(totalChargeAmount.doubleValue()));
            }*/
            totalChargeAmount = minimumAndMaximumCap(totalChargeAmount, glimCharge.getCharge().getMinCap(), glimCharge
                    .getCharge().getMaxCap(), loan);
            glimCharge.setFeeAmount(totalChargeAmount);
            if (chargesMap.containsKey(glimCharge.getCharge().getId())) {
                chargesMap.put(glimCharge.getCharge().getId(),
                        chargesMap.get(glimCharge.getCharge().getId()).add(totalChargeAmount));
            } else {
                chargesMap.put(glimCharge.getCharge().getId(), totalChargeAmount);
            }
        }
    }

    public List<GroupLoanIndividualMonitoring> individualClientCalculation(Loan newLoanApplication, final JsonElement element,
            final LoanApplicationTerms loanApplicationTerms) {
        BigDecimal interestRate = loanApplicationTerms.getAnnualNominalInterestRate();
        Integer numberOfRepayment = loanApplicationTerms.getNumberOfRepayments();
        List<GroupLoanIndividualMonitoring> glimList = new ArrayList<GroupLoanIndividualMonitoring>();
        if (this.fromApiJsonHelper.parameterExists(LoanApiConstants.clientMembersParamName, element)) {
            BigDecimal proposedAmount = null;
            JsonArray clientMembers = this.fromApiJsonHelper.extractJsonArrayNamed(LoanApiConstants.clientMembersParamName, element);
            proposedAmount = createGlimAndGlimCharges(newLoanApplication, element, interestRate, numberOfRepayment, glimList, proposedAmount,
                    clientMembers);
            if (newLoanApplication != null) {
                Set<LoanCharge> loanCharges = newLoanApplication.charges();
                adjustRoundOffValuesToApplicableCharges(loanCharges, numberOfRepayment, glimList);
            }
        }
        return glimList;
    }

    public void adjustRoundOffValuesToApplicableCharges(Set<LoanCharge> loanCharges, Integer numberOfRepayment,
            List<GroupLoanIndividualMonitoring> glimList) {
        for (final GroupLoanIndividualMonitoring glim : glimList) {
            if (glim.isClientSelected()) {
                BigDecimal totalCharge = BigDecimal.ZERO;
                BigDecimal emiAmount = BigDecimal.ZERO;
                BigDecimal totalPaybleAmount = BigDecimal.ZERO;
                BigDecimal totalAmountBeforeAdjustment = BigDecimal.ZERO;
                BigDecimal revisedTotalFee = BigDecimal.ZERO;
                Set<GroupLoanIndividualMonitoringCharge> clientCharges = glim.getGroupLoanIndividualMonitoringCharges();
                for (GroupLoanIndividualMonitoringCharge glimCharge : clientCharges) {
                    glimCharge.setGlim(glim);
                    totalCharge = totalCharge.add(glimCharge.getFeeAmount());

                }
                glim.setChargeAmount(totalCharge);
                // adjust total amount
                if (glim.getDisbursedAmount() != null) {
                    totalAmountBeforeAdjustment = glim.getDisbursedAmount().add(glim.getInterestAmount()).add(totalCharge);
                } else if (glim.getApprovedAmount() != null) {
                    totalAmountBeforeAdjustment = glim.getApprovedAmount().add(glim.getInterestAmount()).add(totalCharge);
                } else {
                    totalAmountBeforeAdjustment = glim.getProposedAmount().add(glim.getInterestAmount()).add(totalCharge);
                }

                emiAmount = BigDecimal.valueOf(Math.ceil(totalAmountBeforeAdjustment.doubleValue() / numberOfRepayment.doubleValue()));
                 
                glim.setInstallmentAmount(emiAmount);
                totalPaybleAmount = BigDecimal.valueOf(Math.round(emiAmount.multiply(BigDecimal.valueOf(numberOfRepayment)).doubleValue()));
                glim.setTotalPaybleAmount(totalPaybleAmount);

                // adjust rounding off amount to applicable charge
                for (GroupLoanIndividualMonitoringCharge glimCharge : clientCharges) {
                    if (glimCharge.getCharge().isEmiRoundingGoalSeek()) {
                        BigDecimal differenceAmount = totalPaybleAmount.subtract(totalAmountBeforeAdjustment);
                        revisedTotalFee = glimCharge.getFeeAmount().add(differenceAmount);
                        glimCharge.setRevisedFeeAmount(BigDecimal.valueOf(Math.round(revisedTotalFee.doubleValue())));
                        totalCharge = totalCharge.add(differenceAmount);
                        for (LoanCharge loanCharge : loanCharges) {
                            if (loanCharge.getCharge().isEmiRoundingGoalSeek()
                                    && loanCharge.getCharge().getId().equals(glimCharge.getCharge().getId())) {
                                BigDecimal chargeAmount = loanCharge.amount().add(differenceAmount);
                                loanCharge.updateAmount(chargeAmount);
                            }
                        }
                    }
                }
                glim.setChargeAmount(BigDecimal.valueOf(Math.round(totalCharge.doubleValue())));
            }
        }
    }

    public BigDecimal createGlimAndGlimCharges(Loan newLoanApplication, final JsonElement element, BigDecimal interestRate,
            Integer numberOfRepayment, List<GroupLoanIndividualMonitoring> glimList, BigDecimal proposedAmount, JsonArray clientMembers) {
        for (JsonElement clientMember : clientMembers) {
            JsonObject member = clientMember.getAsJsonObject();
            Boolean isClientSelected = member.has(LoanApiConstants.isClientSelectedParamName)
                    && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean();
            
            if (isClientSelected) {
                BigDecimal amount = member.get(LoanApiConstants.amountParamName).getAsBigDecimal();
                Long glimClientId = member.get(LoanApiConstants.idParamName).getAsLong();
                Client client = this.clientRepository.getActiveClientInUserScope(glimClientId);
                if (member.has(LoanApiConstants.amountParamName)) {
                    proposedAmount = amount;
                }
                CodeValue loanPurpose = null;
                if (member.has(LoanApiConstants.loanPurposeIdParamName)) {
                    Long loanPurposeId = member.get(LoanApiConstants.loanPurposeIdParamName).getAsLong();
                    loanPurpose = this.codeValueRepository.findOne(loanPurposeId);
                }

                // charges calculation
                Set<GroupLoanIndividualMonitoringCharge> clientCharges = new HashSet<GroupLoanIndividualMonitoringCharge>();
                calculateTotalCharges(newLoanApplication, element, proposedAmount, client, clientCharges);

                // total interest calculation
                BigDecimal totalInterest = calculateTotalInterest(interestRate, numberOfRepayment, proposedAmount);

                final BigDecimal adjustedAmount = BigDecimal.ZERO;
                final BigDecimal installmentAmount = BigDecimal.ZERO;
                final BigDecimal totalPaybleAmount = BigDecimal.ZERO;
                final BigDecimal paidInterestAmount = BigDecimal.ZERO;
                final BigDecimal paidAmount = BigDecimal.ZERO;
                BigDecimal percentage = BigDecimal.ZERO;

                // calculate percentage of principal amount
                percentage = calculatePercentageOfAmount(element, proposedAmount, percentage);

                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = GroupLoanIndividualMonitoring.createInstance(
                        newLoanApplication, client, proposedAmount, null, null, loanPurpose, isClientSelected, adjustedAmount,
                        installmentAmount, totalPaybleAmount, paidInterestAmount, paidAmount, totalInterest, clientCharges, percentage);
                glimList.add(groupLoanIndividualMonitoring);
            }

        }
        return proposedAmount;
    }

    private void calculateTotalCharges(Loan newLoanApplication, final JsonElement element, BigDecimal proposedAmount, Client client,
            Set<GroupLoanIndividualMonitoringCharge> clientCharges) {
        if (this.fromApiJsonHelper.parameterExists("charges", element)) {
            JsonArray charges = this.fromApiJsonHelper.extractJsonArrayNamed("charges", element);
            for (JsonElement jsonElement : charges) {
                JsonObject jsonCharge = jsonElement.getAsJsonObject();
                Long chargeId = jsonCharge.get("chargeId").getAsLong();
                BigDecimal chargeAmount = jsonCharge.get("amount").getAsBigDecimal();
                Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                BigDecimal feeCharge = percentageOf(proposedAmount, chargeAmount);
                BigDecimal totalChargeAmount = feeCharge;
                totalChargeAmount = applyTaxComponentsOnCharges(charge, feeCharge, totalChargeAmount);
               /* if (charge.isEmiRoundingGoalSeek()) {
                    totalChargeAmount = BigDecimal.valueOf(Math.ceil(totalChargeAmount.doubleValue()));
                }*/
                totalChargeAmount = minimumAndMaximumCap(totalChargeAmount, charge.getMinCap(), charge.getMaxCap(),
                        newLoanApplication);
                GroupLoanIndividualMonitoringCharge glimCharge = GroupLoanIndividualMonitoringCharge.instance(null, client, charge,
                        totalChargeAmount, null, charge.isEmiRoundingGoalSeek());
                clientCharges.add(glimCharge);
            }
        }
    }

    private BigDecimal applyTaxComponentsOnCharges(Charge charge, BigDecimal feeCharge, BigDecimal totalChargeAmount) {
        TaxGroup taxGroup = charge.getTaxGroup();
        BigDecimal totalTaxPercentage = BigDecimal.ZERO;
        if (taxGroup != null) {
            List<TaxGroupMappings> taxGroupMappings = taxGroup.getTaxGroupMappings();
            for (TaxGroupMappings taxGroupMapping : taxGroupMappings) {
                totalTaxPercentage = totalTaxPercentage.add(taxGroupMapping.getTaxComponent().getPercentage());
            }
            totalChargeAmount = (feeCharge.add(percentageOf(feeCharge, totalTaxPercentage)));
            totalChargeAmount = BigDecimal.valueOf(Math.round(Double.valueOf("" + totalChargeAmount)));
        }
        return totalChargeAmount;
    }

    private BigDecimal calculatePercentageOfAmount(final JsonElement element, BigDecimal proposedAmount, BigDecimal percentage) {
        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
        if (principal != null) {
            percentage = BigDecimal.valueOf(proposedAmount.doubleValue() / principal.doubleValue());
            percentage = percentage.multiply(BigDecimal.valueOf(100));
        }
        return percentage;
    }

    private BigDecimal calculateTotalInterest(BigDecimal interestRate, Integer numberOfRepayment, BigDecimal proposedAmount) {
        double interest = BigDecimal.ZERO.doubleValue();
        interest = pmt(interestRate.doubleValue(), numberOfRepayment.intValue(), proposedAmount.doubleValue());
        BigDecimal totalInterest = BigDecimal.valueOf(Math.floor(interest));
        return totalInterest;
    }

    public static BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {

        BigDecimal percentageOf = BigDecimal.ZERO;
        if (isGreaterThanZero(value)) {
            final MathContext mc = new MathContext(8, MoneyHelper.getRoundingMode());
            final BigDecimal multiplicand = percentage.divide(BigDecimal.valueOf(100l), mc);
            percentageOf = value.multiply(multiplicand, mc);
        }
        return percentageOf;
    }

    private static boolean isGreaterThanZero(final BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 1;
    }
    
    private static double pmt(double rateOfInterest, int numberOfRepayment, double principal) {
        double fv = 0;
        int type = 0;
        return pmt(rateOfInterest, numberOfRepayment, principal, fv, type);
    }

    private static double pmt(double r, int nper, double pv, double fv, int type) {
        r = r/12/100*1;
        double pmt = -r * (pv * Math.pow(1 + r, nper) + fv) / ((1 + r*type) * (Math.pow(1 + r, nper) - 1));
        pmt = -pmt * nper - pv;
        return pmt;
    }
    
    public BigDecimal calculateInstallmentAmount(BigDecimal installmentAmount, final BigDecimal totalFeeCharges, final Integer numberOfRepayments) {
        BigDecimal feePerInstallment = BigDecimal.ZERO;
        if (installmentAmount != null) {
            feePerInstallment = BigDecimal.valueOf(totalFeeCharges.doubleValue() / numberOfRepayments.doubleValue());
            installmentAmount = installmentAmount.subtract(feePerInstallment);
        }
        return installmentAmount;
    }
    
    // update EMI amount for Glim Loan
    public void updateInstallmentAmount(List<GroupLoanIndividualMonitoring> glimList, LoanApplicationTerms loanApplicationTerms) {
        BigDecimal installmentAmount = BigDecimal.ZERO;
        for (GroupLoanIndividualMonitoring glim : glimList) {
            BigDecimal totalFeeCharges = BigDecimal.ZERO;
            for (GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge : glim.getGroupLoanIndividualMonitoringCharges()) {
                if (groupLoanIndividualMonitoringCharge.getRevisedFeeAmount() != null) {
                    totalFeeCharges = totalFeeCharges.add(groupLoanIndividualMonitoringCharge.getRevisedFeeAmount());
                } else {
                    totalFeeCharges = totalFeeCharges.add(groupLoanIndividualMonitoringCharge.getFeeAmount());
                }
            }
            installmentAmount = installmentAmount.add(calculateInstallmentAmount(glim.getInstallmentAmount(), totalFeeCharges,
                    loanApplicationTerms.getNumberOfRepayments()));
        }
        if (installmentAmount.compareTo(BigDecimal.ZERO) == 1) {
            loanApplicationTerms.setFixedEmiAmount(installmentAmount);
        }
    }
    
    // minimum And Maximum Capping on loan charge
    private BigDecimal minimumAndMaximumCap(final BigDecimal totalChargeAmount, BigDecimal minCap, BigDecimal maxCap, Loan loan) {
        BigDecimal minMaxCap = BigDecimal.ZERO;
        if (minCap != null) {
            final int minimumCap = totalChargeAmount.compareTo(minCap);
            if (minimumCap == -1) {
                minMaxCap = minCap;
                return minMaxCap;
            }
        }
        if (maxCap != null) {
            final int maximumCap = totalChargeAmount.compareTo(maxCap);
            if (maximumCap == 1) {
                minMaxCap = maxCap;
                return minMaxCap;
            }
        }
        minMaxCap = totalChargeAmount;
        // this will round the amount value
        if (loan != null && minMaxCap != null) {
            minMaxCap = Money.of(loan.getCurrency(), minMaxCap).getAmount();
        }
        return minMaxCap;
    }
}
