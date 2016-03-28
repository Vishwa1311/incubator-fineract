package org.apache.fineract.portfolio.globaltransaction.data;

import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_REQUEST_DATA_PARAMETERS;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.externalRefNoParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.metaDataParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.transactionDateParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.transactionRefNoParamName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class GlobalTransactionReferenceDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public GlobalTransactionReferenceDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, GLOBAL_TRANSACTION_REFERENCE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String externalRefNo = this.fromApiJsonHelper.extractStringNamed(externalRefNoParamName, element);
        baseDataValidator.reset().parameter(externalRefNoParamName).value(externalRefNo).notBlank().notExceedingLengthOf(16);
        
        if(this.fromApiJsonHelper.parameterExists(transactionRefNoParamName, element)){
            final String transactionRefNo = this.fromApiJsonHelper.extractStringNamed(transactionRefNoParamName, element);
            baseDataValidator.reset().parameter(transactionRefNoParamName).value(transactionRefNo).notBlank().notExceedingLengthOf(16);
        }
        
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(transactionDateParamName, element);
        baseDataValidator.reset().parameter(transactionDateParamName).value(transactionDate).notNull();
        
        final String metaData = this.fromApiJsonHelper.extractStringNamed(metaDataParamName, element);
        baseDataValidator.reset().parameter(metaDataParamName).value(metaData).notExceedingLengthOf(2048);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}