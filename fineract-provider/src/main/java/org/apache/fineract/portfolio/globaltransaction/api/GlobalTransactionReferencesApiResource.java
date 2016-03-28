package org.apache.fineract.portfolio.globaltransaction.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants;
import org.apache.fineract.portfolio.globaltransaction.data.GlobalTransactionReferenceData;
import org.apache.fineract.portfolio.globaltransaction.service.GlobalTransactionRefernceReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Path("/globaltransactionreferences")
@Component
@Scope("singleton")
public class GlobalTransactionReferencesApiResource {

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<GlobalTransactionReferenceData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final GlobalTransactionRefernceReadPlatformService transactionRefernceReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final JournalEntryReadPlatformService journalEntryReadPlatformService;

    @Autowired
    public GlobalTransactionReferencesApiResource(final PlatformSecurityContext context,
            final DefaultToApiJsonSerializer<GlobalTransactionReferenceData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final GlobalTransactionRefernceReadPlatformService transactionRefernceReadPlatformService,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final JournalEntryReadPlatformService journalEntryReadPlatformService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.transactionRefernceReadPlatformService = transactionRefernceReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.journalEntryReadPlatformService = journalEntryReadPlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTransactionByRefNo(@QueryParam("externalRefNo") final String externalRefNo,
            @QueryParam("transactionRefNo") final String transactionRefNo, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(
                GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME);

        GlobalTransactionReferenceData tranasactionReferenceData = null;
        if (!StringUtils.isBlank(externalRefNo)) {
            tranasactionReferenceData = this.transactionRefernceReadPlatformService.retrieveOneByExternalRefNo(externalRefNo);
        } else if (!StringUtils.isBlank(transactionRefNo)) {
            tranasactionReferenceData = this.transactionRefernceReadPlatformService.retrieveOneByTransactionRefNo(transactionRefNo);
        } else {
            // throw exception
            return null;
        }

        final Set<String> mandatoryResponseParameters = new HashSet<>();
        tranasactionReferenceData = populateAssociations(uriInfo, tranasactionReferenceData, mandatoryResponseParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters(),
                mandatoryResponseParameters);
        return this.toApiJsonSerializer.serialize(settings, tranasactionReferenceData,
                GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("transactionId") final Long transactionId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(
                GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME);

        GlobalTransactionReferenceData tranasactionReferenceData = this.transactionRefernceReadPlatformService.retrieveOne(transactionId);

        final Set<String> mandatoryResponseParameters = new HashSet<>();
        tranasactionReferenceData = populateAssociations(uriInfo, tranasactionReferenceData, mandatoryResponseParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters(),
                mandatoryResponseParameters);
        return this.toApiJsonSerializer.serialize(settings, tranasactionReferenceData,
                GlobalTransactionReferenceApiConstants.GLOBAL_TRANSACTION_REFERENCE_RESPONSE_DATA_PARAMETERS);
    }

    private GlobalTransactionReferenceData populateAssociations(final UriInfo uriInfo,
            GlobalTransactionReferenceData tranasactionReferenceData, final Set<String> mandatoryResponseParameters) {
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        if (!associationParameters.isEmpty()) {
            if (associationParameters.contains("all")) {
                associationParameters.addAll(Arrays.asList("accountTransactions", "journalEntries"));
            }

            Collection<SavingsAccountTransactionData> accountTransactionDatas = null;
            Collection<JournalEntryData> journalEntryDatas = null;

            if (associationParameters.contains("accountTransactions")) {
                mandatoryResponseParameters.add("accountTransactions");
                accountTransactionDatas = this.savingsAccountReadPlatformService
                        .retrieveTransactionsByGlobalTransactionRefId(tranasactionReferenceData.getId());
                if (CollectionUtils.isEmpty(accountTransactionDatas)) {
                    accountTransactionDatas = null;
                }
            }

            if (associationParameters.contains("journalEntries")) {
                mandatoryResponseParameters.add("journalEntries");
                journalEntryDatas = this.journalEntryReadPlatformService
                        .retrieveGLJournalEntriesByTransactionRefId(tranasactionReferenceData.getId());
                if (CollectionUtils.isEmpty(journalEntryDatas)) {
                    journalEntryDatas = null;
                }
            }

            tranasactionReferenceData = GlobalTransactionReferenceData.withAssociations(tranasactionReferenceData, accountTransactionDatas,
                    journalEntryDatas);
        }
        return tranasactionReferenceData;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createTransactionReference(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTransactionReference().withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{transactionRefNo}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String handleCommands(@PathParam("transactionRefNo") final String transactionRefNo,
            @QueryParam("command") final String commandParam, final String apiRequestBodyAsJson) {

        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);

        CommandProcessingResult result = null;
        if (is(commandParam, "undo")) {
            final GlobalTransactionReferenceData tranasactionReferenceData = this.transactionRefernceReadPlatformService
                    .retrieveOneByTransactionRefNo(transactionRefNo);
            final CommandWrapper commandRequest = builder.undoGlobalTransactionReference(tranasactionReferenceData.getId()).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "undo" }); }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}