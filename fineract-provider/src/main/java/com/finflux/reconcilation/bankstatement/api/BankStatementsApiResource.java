package com.finflux.reconcilation.bankstatement.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.fineract.commands.domain.CommandWrapper;

import com.finflux.commands.service.CommandWrapperBuilder;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.data.BankStatementData;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetails;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepository;
import com.finflux.reconcilation.bankstatement.service.BankLoanTransactionsReadPlatformService;
import com.finflux.reconcilation.bankstatement.service.BankStatementReadPlatformService;
import com.finflux.reconcilation.bankstatement.service.BankStatementWritePlatformService;
import com.sun.jersey.multipart.FormDataMultiPart;

@Path("/bankstatements")
@Component
@Scope("singleton")
public class BankStatementsApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "parentEntityType", "parentEntityId", "name",
            "fileName", "size", "type", "description"));
    private final String SystemEntityType = "DOCUMENT";
    private final ToApiJsonSerializer<DocumentData> toDocumentDataApiJsonSerializer;
    private final ToApiJsonSerializer<LoanTransactionData> loanTransactionDataApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BankStatementData> toApiJsonSerializer;
    private final BankStatementWritePlatformService bankStatementWritePlatformService;
    private final DocumentReadPlatformService documentReadPlatformService;
    private final BankStatementReadPlatformService bankStatementReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final BankLoanTransactionsReadPlatformService bankLoanTransactionsReadPlatformService;
    private final BankStatementDetailsRepository bankStatementDetailsRepository;

    @Autowired
    public BankStatementsApiResource(final ApiRequestParameterHelper apiRequestParameterHelper, final PlatformSecurityContext context,
            final ToApiJsonSerializer<BankStatementData> toApiJsonSerializer,
            final BankStatementWritePlatformService bankStatementWritePlatformService,
            final DocumentReadPlatformService documentReadPlatformService,
            final BankStatementReadPlatformService bankStatementReadPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ToApiJsonSerializer<DocumentData> toDocumentDataApiJsonSerializer,
            final BankLoanTransactionsReadPlatformService bankLoanTransactionsReadPlatformService,
            final BankStatementDetailsRepository bankStatementDetailsRepository,
            final ToApiJsonSerializer<LoanTransactionData> loanTransactionDataApiJsonSerializer) {

        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.bankStatementWritePlatformService = bankStatementWritePlatformService;
        this.documentReadPlatformService = documentReadPlatformService;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toDocumentDataApiJsonSerializer = toDocumentDataApiJsonSerializer;
        this.bankLoanTransactionsReadPlatformService = bankLoanTransactionsReadPlatformService;
        this.bankStatementDetailsRepository = bankStatementDetailsRepository;
        this.loanTransactionDataApiJsonSerializer = loanTransactionDataApiJsonSerializer;
    }

    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createBankStatement(FormDataMultiPart formParams) throws InvalidFormatException, IOException {

        final Long bankStatementId = this.bankStatementWritePlatformService.createBankStatement(formParams);

        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(bankStatementId, null));
    }

    @PUT
    @Path("{documentId}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateBankStatement(FormDataMultiPart formParams) {

        Long bankStatementId = this.bankStatementWritePlatformService.updateBankStatement(formParams);

        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(bankStatementId, null));
    }

    @POST
    @Path("{bankStatementId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String makeReconcile(@Context final UriInfo uriInfo, @PathParam("bankStatementId") final Long bankStatementId,
            @QueryParam("command") final String command) {

        this.context.authenticatedUser();

        final CommandWrapper commandRequest = new CommandWrapperBuilder().reconcileBankStatement(bankStatementId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveAllBankStatements(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        final Collection<BankStatementData> bankStatementData = this.bankStatementReadPlatformService.retrieveAllBankStatements();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, bankStatementData,
                ReconciliationApiConstants.BANK_STATEMENT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("/document/{documentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDocument(@PathParam("documentId") final Long documentId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        final DocumentData documentData = this.documentReadPlatformService.retrieveDocument(ReconciliationApiConstants.entityName,
                ReconciliationApiConstants.bankStatementFolder, documentId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toDocumentDataApiJsonSerializer.serialize(settings, documentData, this.RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{bankStatementId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getBankStatement(@PathParam("bankStatementId") final Long bankStatementId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        final BankStatementData bankStatementData = this.bankStatementReadPlatformService.getBankStatement(bankStatementId);

        return this.toApiJsonSerializer.serialize(settings, bankStatementData,
                ReconciliationApiConstants.BANK_STATEMENT_RESPONSE_DATA_PARAMETERS);

    }

    @GET
    @Path("{documentId}/attachment")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response downloadFile(@PathParam("documentId") final Long documentId) {

        this.context.authenticatedUser();

        final FileData fileData = this.documentReadPlatformService.retrieveFileData(ReconciliationApiConstants.entityName,
                ReconciliationApiConstants.bankStatementFolder, documentId);

        final ResponseBuilder response = Response.ok(fileData.file());

        response.header("Content-Disposition", "attachment; filename=\"" + fileData.name() + "\"");

        response.header("Content-Type", fileData.contentType());

        return response.build();
    }

    @DELETE
    @Path("{bankStatementId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteBankStatement(@PathParam("bankStatementId") final Long bankStatementId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteBankStatement(bankStatementId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{bankStatementId}/{bankStatementDetailId}/matchingloantransactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getMatchingLoanTransactions(@PathParam("bankStatementId") final Long bankStatementId,
            @PathParam("bankStatementDetailId") final Long bankStatementDetailId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        BankStatementDetails bankStatementDetails = this.bankStatementDetailsRepository.findOne(bankStatementDetailId);

        Collection<LoanTransactionData> LoanTransactionDataMatchForAmount = this.bankLoanTransactionsReadPlatformService
                .retrieveLoanTransactionsForGroup(bankStatementDetails.getAmount(), bankStatementDetails.getGroupExternalId(),
                        bankStatementDetails.getTransactionType());

        return this.loanTransactionDataApiJsonSerializer.serialize(LoanTransactionDataMatchForAmount);
    }

    @GET
    @Path("{loanId}/{bankStatementDetailId}/loantransactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getLoanTransaction(@PathParam("loanId") final Long loanId,
            @PathParam("bankStatementDetailId") final Long bankStatementDetailId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        Collection<LoanTransactionData> loanTransactionForBankDetails = this.bankLoanTransactionsReadPlatformService
                .retrieveLoanTransactionsForBankDetails(loanId);

        return this.loanTransactionDataApiJsonSerializer.serialize(loanTransactionForBankDetails);
    }

    @POST
    @Path("{bankStatementId}/nonportfolio")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createJournalEntry(@PathParam("bankStatementId") final Long bankStatementId, final String apiRequestBodyAsJson) {
        String result = this.bankStatementWritePlatformService.createJournalEntries(bankStatementId, apiRequestBodyAsJson);
        return result;
    }

}