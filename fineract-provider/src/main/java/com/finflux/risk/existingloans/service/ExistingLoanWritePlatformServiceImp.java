package com.finflux.risk.existingloans.service;

import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;
import com.finflux.risk.existingloans.data.ExistingLoanData;
import com.finflux.risk.existingloans.data.ExistingLoanDataValidator;
import com.finflux.risk.existingloans.domain.ExistingLoan;
import com.finflux.risk.existingloans.domain.ExistingLoanRepositoryWrapper;

@Service
public class ExistingLoanWritePlatformServiceImp implements ExistingLoanWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ExistingLoanWritePlatformServiceImp.class);

    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<ExistingLoanData> toApiJsonSerializer;
    private final ExistingLoanAssembler existingLoanAssembler;
    private final ExistingLoanDataValidator existingLoanDataValidator;
    private final ExistingLoanRepositoryWrapper existingLoanRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;

    @Autowired
    public ExistingLoanWritePlatformServiceImp(final PlatformSecurityContext context,
            final ToApiJsonSerializer<ExistingLoanData> toApiJsonSerializer, final ExistingLoanAssembler existingLoanAssembler,
            final ExistingLoanDataValidator existingLoanDataValidator, final ExistingLoanRepositoryWrapper existingLoanRepository,
            final CodeValueRepositoryWrapper codeValueRepository) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.existingLoanAssembler = existingLoanAssembler;
        this.existingLoanDataValidator = existingLoanDataValidator;
        this.existingLoanRepository = existingLoanRepository;
        this.codeValueRepository = codeValueRepository;

    }

    @Override
    public CommandProcessingResult saveExistingLoan(final Long clientId,JsonCommand command) {
        this.context.authenticatedUser();
        this.existingLoanDataValidator.validateForCreate(clientId,command.json());
        final ExistingLoan existingLoan = this.existingLoanAssembler.assembleForSave(clientId,command);
        this.existingLoanRepository.save(existingLoan);
        return new CommandProcessingResultBuilder() //
                .withEntityId(existingLoan.getId()).withCommandId(command.commandId()).build();

    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

    @Transactional
    @Override
    public CommandProcessingResult updateExistingLoan(final Long clientId,final Long existingLoanId, final JsonCommand command) {

        try {
            
            final ExistingLoan existingLoanForUpdate = this.existingLoanRepository.findOneWithNotFoundDetection(existingLoanId);
            
            this.existingLoanDataValidator.validateForUpdate(clientId,command.json());

            
            final Map<String, Object> changes = existingLoanForUpdate.update(command);

            if (changes.containsKey(ExistingLoanApiConstants.sourceCvIdParamName)) {

                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.sourceCvIdParamName);
                CodeValue sourceCvId = null;
                if (newValue != null) {
                    sourceCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.Source_Cv_Option, newValue);
                }
                existingLoanForUpdate.updatesourceCvId(sourceCvId);
            }
            if (changes.containsKey(ExistingLoanApiConstants.bureauCvIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.Bureau_Cv_Option);
                CodeValue bureauCvId = null;
                if (newValue != null) {
                    bureauCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.Bureau_Cv_Option, newValue);
                }
                existingLoanForUpdate.updatebureauCvId(bureauCvId);
            }

            if (changes.containsKey(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName)) {
                CodeValue externalLoanPurposeCvId = null;
                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.ExternalLoan_Purpose_Option);
                if (newValue != null) {
                    externalLoanPurposeCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.ExternalLoan_Purpose_Option, newValue);
                }
                existingLoanForUpdate.updateExternalLoanPurpose(externalLoanPurposeCvId);
            }
            if (changes.containsKey(ExistingLoanApiConstants.loanTypeCvIdParamName)) {
                CodeValue loanType = null;
                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.LoanType_Cv_Option);
                if (newValue != null) {
                    loanType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.LoanType_Cv_Option, newValue);
                }
                existingLoanForUpdate.updateloanType(loanType);
            }

            if (!changes.isEmpty()) {
                this.existingLoanRepository.saveAndFlush(existingLoanForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(existingLoanForUpdate.getClientId()) //
                    .withEntityId(existingLoanForUpdate.getId()) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteExistingLoan(final Long clientId,final Long existingLoanId) {
        final ExistingLoan existingLoan = this.existingLoanRepository.findOneWithNotFoundDetection(existingLoanId);
        this.existingLoanRepository.delete(existingLoan);

        return new CommandProcessingResultBuilder() //
                .withEntityId(existingLoan.getId()) //
                .build();
    }

}
