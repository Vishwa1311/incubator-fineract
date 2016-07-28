package com.finflux.familydetail.service;

import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.data.FamilyDetailDataValidator;
import com.finflux.familydetail.domain.FamilyDetail;
import com.finflux.familydetail.domain.FamilyDetailsRepository;

@Service
public class FamilyDetailWritePlatformServiceImp implements FamilyDetailWritePlatromService {

    private final FamilyDetailsRepository familyDetailsRepository;
    private final PlatformSecurityContext context;
    private final FamilyDetailDataValidator fromApiJsonDeserializer;
    private ClientRepositoryWrapper clientRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;

    @Autowired
    public FamilyDetailWritePlatformServiceImp(FamilyDetailsRepository familyDetailsRepository, final PlatformSecurityContext context,
            FamilyDetailDataValidator fromApiJsonDeserializer, ClientRepositoryWrapper clientRepository,
            CodeValueRepositoryWrapper codeValueRepository) {
        super();
        this.familyDetailsRepository = familyDetailsRepository;
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.clientRepository = clientRepository;
        this.codeValueRepository = codeValueRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createFamilyDeatails(final Long clientId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final String firstname = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.firstnameParamName);
            final String middlename = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.middlenameParamName);
            final String lastname = command.stringValueOfParameterNamed(FamilyDetailsApiConstants.lastnameParamName);

            final LocalDate dateOfBirth = command.localDateValueOfParameterNamed(FamilyDetailsApiConstants.dobParamName);

            final Integer age = command.integerValueOfParameterNamed(FamilyDetailsApiConstants.ageParamName);

            CodeValue gender = null;
            final Long genderId = command.longValueOfParameterNamed(FamilyDetailsApiConstants.genderIdParamName);
            if (genderId != null) {
                gender = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(FamilyDetailsApiConstants.genderParamName,
                        genderId);
            }

            CodeValue occupationDetails = null;
            final Long occupationDetailsId = command.longValueOfParameterNamed(FamilyDetailsApiConstants.occupationDetailsIdParamName);
            if (occupationDetailsId != null) {
                occupationDetails = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                        FamilyDetailsApiConstants.occupationalDetailsParamName, occupationDetailsId);
            }

            CodeValue relationship = null;
            final Long relationshipId = command.longValueOfParameterNamed(FamilyDetailsApiConstants.relationshipIdParamName);
            if (relationshipId != null) {
                relationship = this.codeValueRepository
                        .findOneByCodeNameAndIdWithNotFoundDetection(FamilyDetailsApiConstants.relationshipParamName, relationshipId);
            }

            CodeValue education = null;
            final Long educationId = command.longValueOfParameterNamed(FamilyDetailsApiConstants.educationIdParamName);
            if (educationId != null) {
                education = this.codeValueRepository
                        .findOneByCodeNameAndIdWithNotFoundDetection(FamilyDetailsApiConstants.educationParamName, educationId);
            }

            CodeValue salutaion = null;
            final Long salutaionId = command.longValueOfParameterNamed(FamilyDetailsApiConstants.salutationIdParamName);
            if (salutaionId != null) {
                salutaion = this.codeValueRepository
                        .findOneByCodeNameAndIdWithNotFoundDetection(FamilyDetailsApiConstants.salutationParamName, salutaionId);
            }

            final FamilyDetail familyDetail = FamilyDetail.create(client, salutaion, firstname, middlename, lastname, relationship, gender,
                    dateOfBirth, age, occupationDetails, education);

            this.familyDetailsRepository.saveAndFlush(familyDetail);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(client.getId()) //
                    .withEntityId(familyDetail.getId()) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    public CommandProcessingResult updateFamilyDeatails(final Long clientId, Long id, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForUpdate(command.json());
            final FamilyDetail familyDetail = this.familyDetailsRepository.findByIdAndClientId(id, clientId);
            final Map<String, Object> changes = familyDetail.update(command);
            if (!CollectionUtils.isEmpty(changes)) {
                this.familyDetailsRepository.save(familyDetail);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(familyDetail.getId()) //
                    .withClientId(command.getClientId()) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    public CommandProcessingResult deleteFamilyDeatails(final Long familyDetailId, final Long clientId) {
        try {
            final FamilyDetail familyDetailDelete = this.familyDetailsRepository.findOne(familyDetailId);
            this.familyDetailsRepository.delete(familyDetailDelete);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(familyDetailId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

}
