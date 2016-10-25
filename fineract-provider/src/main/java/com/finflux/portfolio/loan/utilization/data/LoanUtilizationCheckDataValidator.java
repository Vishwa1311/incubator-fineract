package com.finflux.portfolio.loan.utilization.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loan.utilization.api.LoanUtilizationCheckApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class LoanUtilizationCheckDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanUtilizationCheckDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanUtilizationCheckApiConstants.CREATE_LOAN_UTILIZATION_CHECK_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanUtilizationCheckApiConstants.LOAN_UTILIZATION_CHECK_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Boolean isAuditeScheduledOn = this.fromApiJsonHelper.extractBooleanNamed(
                LoanUtilizationCheckApiConstants.isAuditeScheduledOnParamName, element);

        final Long toBeAuditedById = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName,
                element);
        final LocalDate auditeScheduledOn = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanUtilizationCheckApiConstants.auditeScheduledOnParamName, element);

        final Long auditDoneById = this.fromApiJsonHelper
                .extractLongNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName, element);
        final LocalDate auditDoneOn = this.fromApiJsonHelper.extractLocalDateNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName,
                element);

        if (isAuditeScheduledOn != null && isAuditeScheduledOn) {
            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName).value(toBeAuditedById).notNull();
            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName).value(auditeScheduledOn)
                    .notNull();

            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditDoneByIdParamName).value(auditDoneById)
                    .ignoreIfNull();
            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditDoneOnParamName).value(auditDoneOn).ignoreIfNull();
        } else {
            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName).value(toBeAuditedById)
                    .ignoreIfNull();
            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName).value(auditeScheduledOn)
                    .ignoreIfNull();

            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditDoneByIdParamName).value(auditDoneById).notNull();
            baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditDoneOnParamName).value(auditDoneOn).notNull();
        }

        final JsonArray loanUtilizationCheckDetailsArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                LoanUtilizationCheckApiConstants.loanUtilizationCheckDetailsParamName, element);

        if (loanUtilizationCheckDetailsArray != null && loanUtilizationCheckDetailsArray.size() > 0) {
            for (int i = 0; i < loanUtilizationCheckDetailsArray.size(); i++) {
                final JsonObject loanUtilizationElement = loanUtilizationCheckDetailsArray.get(i).getAsJsonObject();
                validateForLoanUtilizationCheckDetails(locale, loanUtilizationElement, baseDataValidator);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateForLoanUtilizationCheckDetails(final Locale locale, final JsonObject element,
            final DataValidatorBuilder baseDataValidator) {

        final Long loanId = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.loanIdParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.loanIdParamName).value(loanId).notNull();

        final JsonArray utilizationDetailsArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                LoanUtilizationCheckApiConstants.utilizationDetailsParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.utilizationDetailsParamName).value(utilizationDetailsArray)
                .jsonArrayNotEmpty();

        if (utilizationDetailsArray != null && utilizationDetailsArray.size() > 0) {
            for (int i = 0; i < utilizationDetailsArray.size(); i++) {
                final JsonObject utilizationDetail = utilizationDetailsArray.get(i).getAsJsonObject();
                validateForUtilizationDetails(locale, utilizationDetail, baseDataValidator);
            }
        }
    }

    private void validateForUtilizationDetails(final Locale locale, final JsonObject element, final DataValidatorBuilder baseDataValidator) {
        final Long loanPurposeId = this.fromApiJsonHelper
                .extractLongNamed(LoanUtilizationCheckApiConstants.loanPurposeIdParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.loanPurposeIdParamName).value(loanPurposeId).ignoreIfNull();

        final Boolean isSameAsOriginalPurpose = this.fromApiJsonHelper.extractBooleanNamed(
                LoanUtilizationCheckApiConstants.isSameAsOriginalPurposeParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.isSameAsOriginalPurposeParamName)
                .value(isSameAsOriginalPurpose).ignoreIfNull();

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed(LoanUtilizationCheckApiConstants.amountParamName, element,
                locale);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.amountParamName).value(amount).ignoreIfNull();

        final String comment = this.fromApiJsonHelper.extractStringNamed(LoanUtilizationCheckApiConstants.commentParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.commentParamName).value(comment).ignoreIfNull();
    }

    public void validateForUpdate(String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanUtilizationCheckApiConstants.UPDATE_LOAN_UTILIZATION_CHECK_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanUtilizationCheckApiConstants.LOAN_UTILIZATION_CHECK_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long toBeAuditedById = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName,
                element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName).value(toBeAuditedById)
                .ignoreIfNull();

        final LocalDate auditeScheduledOn = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanUtilizationCheckApiConstants.auditeScheduledOnParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName).value(auditeScheduledOn)
                .ignoreIfNull();

        final Long auditDoneById = this.fromApiJsonHelper
                .extractLongNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName, element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditDoneByIdParamName).value(auditDoneById).ignoreIfNull();

        final LocalDate auditDoneOn = this.fromApiJsonHelper.extractLocalDateNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName,
                element);
        baseDataValidator.reset().parameter(LoanUtilizationCheckApiConstants.auditDoneOnParamName).value(auditDoneOn).ignoreIfNull();

        final JsonArray loanUtilizationCheckDetailsArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                LoanUtilizationCheckApiConstants.loanUtilizationCheckDetailsParamName, element);

        if (loanUtilizationCheckDetailsArray != null && loanUtilizationCheckDetailsArray.size() > 0) {
            for (int i = 0; i < loanUtilizationCheckDetailsArray.size(); i++) {
                final JsonObject loanUtilizationElement = loanUtilizationCheckDetailsArray.get(i).getAsJsonObject();
                validateForLoanUtilizationCheckDetails(locale, loanUtilizationElement, baseDataValidator);
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}