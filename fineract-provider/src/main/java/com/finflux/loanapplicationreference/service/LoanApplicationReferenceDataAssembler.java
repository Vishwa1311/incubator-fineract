package com.finflux.loanapplicationreference.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.accountdetails.service.AccountEnumerations;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.api.LoanApplicationReferenceApiConstants;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceStatus;
import com.finflux.loanapplicationreference.domain.LoanApplicationCharge;
import com.finflux.loanapplicationreference.domain.LoanApplicationChargeRepositoryWrapper;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationSanction;
import com.finflux.loanapplicationreference.domain.LoanApplicationSanctionTranche;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanApplicationReferenceDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final JdbcTemplate jdbcTemplate;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final LoanProductRepository loanProductRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanApplicationChargeRepositoryWrapper loanApplicationChargeRepository;
    private final GroupRepositoryWrapper groupRepository;

    @Autowired
    public LoanApplicationReferenceDataAssembler(final FromJsonHelper fromApiJsonHelper, final RoutingDataSource dataSource,
            final CodeValueRepositoryWrapper codeValueRepository, final LoanProductRepository loanProductRepository,
            final ClientRepositoryWrapper clientRepository, final StaffRepositoryWrapper staffRepository,
            final ChargeRepositoryWrapper chargeRepository, final LoanApplicationChargeRepositoryWrapper loanApplicationChargeRepository,
            final GroupRepositoryWrapper groupRepository) {

        this.fromApiJsonHelper = fromApiJsonHelper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueRepository = codeValueRepository;
        this.loanProductRepository = loanProductRepository;
        this.clientRepository = clientRepository;
        this.staffRepository = staffRepository;
        this.chargeRepository = chargeRepository;
        this.loanApplicationChargeRepository = loanApplicationChargeRepository;
        this.groupRepository = groupRepository;
    }

    public LoanApplicationReference assembleCreateForm(final JsonCommand command) {

        final String loanApplicationReferenceNo = generateNextSequenceValue(null);

        final String externalIdOne = command
                .stringValueOfParameterNamedAllowingNull(LoanApplicationReferenceApiConstants.externalIdOneParamName);

        final String externalIdTwo = command
                .stringValueOfParameterNamedAllowingNull(LoanApplicationReferenceApiConstants.externalIdTwoParamName);

        final Long clientId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.clientIdParamName);
        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

        Staff loanOfficer = null;
        if (command.parameterExists(LoanApplicationReferenceApiConstants.loanOfficerIdParamName)) {
            final Long loanOfficerId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanOfficerIdParamName);
            loanOfficer = this.staffRepository.findOneWithNotFoundDetection(loanOfficerId);
        }

        Group group = null;
        if (command.parameterExists(LoanApplicationReferenceApiConstants.groupIdParamName)) {
            final Long groupId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.groupIdParamName);
            group = this.groupRepository.findOneWithNotFoundDetection(groupId);
        }

        final Integer statusEnum = LoanApplicationReferenceStatus.APPLICATION_CREATED.getValue();

        final String accountType = command.stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.accountTypeParamName);
        final EnumOptionData accountTypeEnumData = AccountEnumerations.loanType(accountType);
        final Integer accountTypeEnum = accountTypeEnumData.getId().intValue();

        final Long loanProductId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName);
        final LoanProduct loanProduct = this.loanProductRepository.findOne(loanProductId);
        if (loanProduct == null) { throw new LoanProductNotFoundException(loanProductId); }

        CodeValue loanPurpose = null;
        if (command.parameterExists(LoanApplicationReferenceApiConstants.loanPurposeIdParamName)) {
            final Long loanpurposeId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanPurposeIdParamName);
            loanPurpose = this.codeValueRepository.findOneWithNotFoundDetection(loanpurposeId);
        }

        final BigDecimal loanAmountRequested = command
                .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanAmountRequestedParamName);

        final Integer numberOfRepayments = command
                .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName);

        final Integer repaymentPeriodFrequencyEnum = command
                .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName);

        final Integer repayEvery = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.repayEveryParamName);

        final Integer termPeriodFrequencyEnum = command
                .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName);

        final Integer termFrequency = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.termFrequencyParamName);

        final BigDecimal fixedEmiAmount = command
                .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName);

        final Integer noOfTranche = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.noOfTrancheParamName);

        final LocalDate submittedOnDate = command
                .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.submittedOnDateParamName);

        final LocalDate expectedFirstRepaymentOnDate = null;
        validateSubmittedOnDate(submittedOnDate, expectedFirstRepaymentOnDate, loanProduct, client);

        final LoanApplicationReference loanApplicationReference = LoanApplicationReference.create(loanApplicationReferenceNo,
                externalIdOne, externalIdTwo, client, loanOfficer, group, statusEnum, accountTypeEnum, loanProduct, loanPurpose,
                loanAmountRequested, numberOfRepayments, repaymentPeriodFrequencyEnum, repayEvery, termPeriodFrequencyEnum, termFrequency,
                fixedEmiAmount, noOfTranche, submittedOnDate.toDate());

        final List<LoanApplicationCharge> loanApplicationCharges = assembleCreateLoanApplicationCharge(loanApplicationReference,
                command.json());
        loanApplicationReference.updateLoanApplicationCharges(loanApplicationCharges);

        return loanApplicationReference;
    }

    private List<LoanApplicationCharge> assembleCreateLoanApplicationCharge(final LoanApplicationReference loanApplicationReference,
            final String json) {
        final List<LoanApplicationCharge> loanApplicationCharges = new ArrayList<>();
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String chargesParameterName = "charges";
        if (element.isJsonObject() && this.fromApiJsonHelper.parameterExists(chargesParameterName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            if (topLevelJsonElement.get(chargesParameterName).isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();
                    LoanApplicationCharge loanApplicationCharge = null;
                    if (this.fromApiJsonHelper.parameterExists("loanAppChargeId", loanChargeElement)) {
                        final Long loanAppChargeId = this.fromApiJsonHelper.extractLongNamed("loanAppChargeId", loanChargeElement);
                        loanApplicationCharge = this.loanApplicationChargeRepository.findOneWithNotFoundDetection(loanAppChargeId);
                    }
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);

                    final BigDecimal amountOrPercentage = this.fromApiJsonHelper
                            .extractBigDecimalNamed("amount", loanChargeElement, locale);

                    final LocalDate localdueDate = this.fromApiJsonHelper.extractLocalDateNamed("dueDate", loanChargeElement, dateFormat,
                            locale);
                    Date dueDate = null;
                    if (localdueDate != null) {
                        dueDate = localdueDate.toDate();
                    }
                    
                    Boolean isMandatory = this.fromApiJsonHelper.extractBooleanNamed("isMandatory", loanChargeElement);
                    if (isMandatory == null) {
                        isMandatory = false;
                    }
                    if (loanApplicationCharge != null) {
                        loanApplicationCharge.update(loanApplicationReference, charge, dueDate, amountOrPercentage, isMandatory);
                    } else {
                        loanApplicationCharge = LoanApplicationCharge.create(loanApplicationReference, charge, dueDate, amountOrPercentage,
                                isMandatory);
                    }
                    loanApplicationCharges.add(loanApplicationCharge);
                }
            }
        }
        return loanApplicationCharges;
    }

    public Map<String, Object> assembleUpdateForm(final LoanApplicationReference loanApplicationReference, final JsonCommand command) {
        final List<LoanApplicationCharge> loanApplicationCharges = assembleCreateLoanApplicationCharge(loanApplicationReference,
                command.json());
        loanApplicationReference.updateLoanApplicationCharges(loanApplicationCharges);
        final Map<String, Object> actualChanges = loanApplicationReference.update(command);
        final LocalDate expectedFirstRepaymentOnDate = null;
        validateSubmittedOnDate(loanApplicationReference.submittedOnDateParamNameLocalDate(), expectedFirstRepaymentOnDate,
                loanApplicationReference.getLoanProduct(), loanApplicationReference.getClient());
        if (!actualChanges.isEmpty()) {
            if (actualChanges.containsKey(LoanApplicationReferenceApiConstants.clientIdParamName)) {
                final Long clientId = (Long) actualChanges.get(LoanApplicationReferenceApiConstants.clientIdParamName);
                final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
                loanApplicationReference.updateClient(client);
            }
            if (actualChanges.containsKey(LoanApplicationReferenceApiConstants.loanOfficerIdParamName)) {
                final Long loanOfficerId = (Long) actualChanges.get(LoanApplicationReferenceApiConstants.loanOfficerIdParamName);
                final Staff loanOfficer = this.staffRepository.findOneWithNotFoundDetection(loanOfficerId);
                loanApplicationReference.updateLoanOfficer(loanOfficer);
            }
            if (actualChanges.containsKey(LoanApplicationReferenceApiConstants.loanProductIdParamName)) {
                final Long loanProductId = (Long) actualChanges.get(LoanApplicationReferenceApiConstants.loanProductIdParamName);
                final LoanProduct loanProduct = this.loanProductRepository.findOne(loanProductId);
                if (loanProduct == null) { throw new LoanProductNotFoundException(loanProductId); }
                loanApplicationReference.updateLoanProduct(loanProduct);
            }
            if (actualChanges.containsKey(LoanApplicationReferenceApiConstants.loanPurposeIdParamName)) {
                final Long loanPurposeId = (Long) actualChanges.get(LoanApplicationReferenceApiConstants.loanPurposeIdParamName);
                final CodeValue loanPurpose = this.codeValueRepository.findOneWithNotFoundDetection(loanPurposeId);
                loanApplicationReference.updateLoanPurpose(loanPurpose);
            }
        }
        return actualChanges;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> assembleApproveForm(final LoanApplicationReference loanApplicationReference, final JsonCommand command) {

        final List<LoanApplicationCharge> loanApplicationCharges = assembleCreateLoanApplicationCharge(loanApplicationReference,
                command.json());
        loanApplicationReference.updateLoanApplicationCharges(loanApplicationCharges);
        
        Map<String, Object> changes = new LinkedHashMap<>(10);
        final LoanProduct loanProduct = loanApplicationReference.getLoanProduct();
        final Integer statusEnum = LoanApplicationReferenceStatus.APPLICATION_APPROVED.getValue();
        changes.put("statusEnum", statusEnum);
        loanApplicationReference.updateStatusEnum(statusEnum);
        LoanApplicationSanction loanApplicationSanction = loanApplicationReference.getLoanApplicationSanction();
        BigDecimal loanAmountApproved = null;
        if (loanApplicationSanction != null && loanApplicationSanction.getId() != null) {
            changes = loanApplicationSanction.update(command);
            loanAmountApproved = loanApplicationSanction.getLoanAmountApproved();
        } else {

            loanAmountApproved = command.bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName);

            final LocalDate expectedDisbursementDate = command
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName);

            final Integer numberOfRepayments = command
                    .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName);

            Integer repaymentPeriodFrequencyEnum = command
                    .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName);

            if (repaymentPeriodFrequencyEnum == null) {
                repaymentPeriodFrequencyEnum = loanProduct.getInterestPeriodFrequencyType().getValue();
            }

            Integer repayEvery = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.repayEveryParamName);
            if (repayEvery == null || repayEvery == 0) {
                repayEvery = loanProduct.getLoanProductRelatedDetail().getRepayEvery();
            }

            Integer termPeriodFrequencyEnum = command
                    .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName);
            if (termPeriodFrequencyEnum == null) {
                repaymentPeriodFrequencyEnum = loanProduct.getInterestPeriodFrequencyType().getValue();
            }

            Integer termFrequency = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.termFrequencyParamName);
            if (termFrequency == null || termFrequency == 0) {
                termFrequency = repayEvery * loanProduct.getNumberOfRepayments();
            }

            final LocalDate approvedOnDate = command
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.approvedOnDateParaName);

            final BigDecimal fixedEmiAmount = command
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName);

            final BigDecimal maxOutstandingLoanBalance = command
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.maxOutstandingLoanBalanceParamName);

            Date repaymentsStartingFromDate = null;
            final LocalDate repaymentsStartingFromLocalDate = command
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName);
            if (repaymentsStartingFromLocalDate != null) {
                repaymentsStartingFromDate = repaymentsStartingFromLocalDate.toDate();
            }

            loanApplicationSanction = LoanApplicationSanction.create(loanApplicationReference, loanAmountApproved, approvedOnDate.toDate(),
                    expectedDisbursementDate.toDate(), repaymentsStartingFromDate, numberOfRepayments, repaymentPeriodFrequencyEnum,
                    repayEvery, termPeriodFrequencyEnum, termFrequency, fixedEmiAmount, maxOutstandingLoanBalance);
        }

        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        if (command.parameterExists(LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName)) {
            final JsonArray array = parentElementObj.get(LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName)
                    .getAsJsonArray();
            if (array != null && array.size() > 0) {
                final List<LoanApplicationSanctionTranche> loanApplicationSanctionTranches = new ArrayList();
                BigDecimal trancheTotalAmount = BigDecimal.ZERO;
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();

                    final BigDecimal trancheAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                            LoanApplicationReferenceApiConstants.trancheAmountParamName, element);
                    trancheTotalAmount.add(trancheAmount);

                    final BigDecimal fixedEmiAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                            LoanApplicationReferenceApiConstants.fixedEmiAmountParamName, element);

                    final LocalDate expectedTrancheDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(
                            LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName, element);

                    final LoanApplicationSanctionTranche loanApplicationSanctionTranche = LoanApplicationSanctionTranche.create(
                            loanApplicationSanction, trancheAmount, fixedEmiAmount, expectedTrancheDisbursementDate.toDate());
                    loanApplicationSanctionTranches.add(loanApplicationSanctionTranche);
                }
                if (loanProduct.isMultiDisburseLoan() && trancheTotalAmount.compareTo(loanAmountApproved) > 0) {
                    /**
                     * Through Error Amount Should be less than or equal to loan
                     * Amount Approved.
                     */

                }
                if (!loanApplicationSanctionTranches.isEmpty()) {
                    loanApplicationSanction.updateLoanApplicationSanctionTranches(loanApplicationSanctionTranches);
                }
            }
        }
        if (loanApplicationSanction != null) {
            loanApplicationReference.updateLoanApplicationSanction(loanApplicationSanction);
        }
        return changes;
    }

    public void validateSubmittedOnDate(final LocalDate submittedOnDate, final LocalDate expectedFirstRepaymentOnDate,
            final LoanProduct loanProduct, final Client client) {

        final LocalDate startDate = loanProduct.getStartDate();
        final LocalDate closeDate = loanProduct.getCloseDate();
        String defaultUserMessage = "";

        if (client != null) {
            final LocalDate activationDate = client.getActivationLocalDate();

            if (activationDate == null) {
                defaultUserMessage = "Client is not activated.";
                throw new LoanApplicationDateException("client.is.not.activated", defaultUserMessage);
            }

            if (submittedOnDate.isBefore(activationDate)) {
                defaultUserMessage = "Submitted on date cannot be before the client activation date.";
                throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.client.activation.date", defaultUserMessage,
                        submittedOnDate.toString(), activationDate.toString());
            }
        }

        if (startDate != null && submittedOnDate.isBefore(startDate)) {
            defaultUserMessage = "submittedOnDate cannot be before the loan product startDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.loan.product.start.date", defaultUserMessage,
                    submittedOnDate.toString(), startDate.toString());
        }

        if (closeDate != null && submittedOnDate.isAfter(closeDate)) {
            defaultUserMessage = "submittedOnDate cannot be after the loan product closeDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.after.the.loan.product.close.date", defaultUserMessage,
                    submittedOnDate.toString(), closeDate.toString());
        }

        if (expectedFirstRepaymentOnDate != null && submittedOnDate.isAfter(expectedFirstRepaymentOnDate)) {
            defaultUserMessage = "submittedOnDate cannot be after the loans  expectedFirstRepaymentOnDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.after.the.loan.expected.first.repayment.date",
                    defaultUserMessage, submittedOnDate.toString(), expectedFirstRepaymentOnDate.toString());
        }
    }

    private String generateNextSequenceValue(final String sequenceName) {
        if (sequenceName != null) {
            final Long count = this.jdbcTemplate.queryForObject("SELECT count(*) from sequences s where s.sequence_name = '" + sequenceName
                    + "'", Long.class);
            if (count == null || count == 0) {
                final String description = "Sequence for BC region " + sequenceName + "";
                this.jdbcTemplate
                        .execute("INSERT INTO `sequences` (`sequence_name`, `increment_by`, `min_value`, `max_value`, `cur_value`, `is_reset_daily`, `sequence_date`, `description`) VALUES ('"
                                + sequenceName + "', 1, 1, 99, 0, 0, null,'" + description + "')");
            }
            return this.jdbcTemplate.queryForObject("SELECT generate_next_sequence_value('" + sequenceName + "')", String.class);
        }
        return this.jdbcTemplate.queryForObject(
                "SELECT LPAD((SELECT generate_next_sequence_value('loan_application_reference_no')),15,'0')", String.class);
    }
}
