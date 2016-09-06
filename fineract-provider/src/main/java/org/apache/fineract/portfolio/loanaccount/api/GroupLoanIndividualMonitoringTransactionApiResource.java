package org.apache.fineract.portfolio.loanaccount.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.charge.service.GroupLoanIndividualMonitoringChargeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.exception.ClientAlreadyWriteOffException;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanChargeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/grouploanindividualmonitoring/{loanId}/transactions")
@Component
@Scope("singleton")
public class GroupLoanIndividualMonitoringTransactionApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringTransactionData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final GroupLoanIndividualMonitoringReadPlatformService glimReadPlatformService;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final GroupLoanIndividualMonitoringChargeReadPlatformService glimChargeReadPlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final GroupLoanIndividualMonitoringTransactionAssembler glimTransactionAssembler;
    private final GroupLoanIndividualMonitoringRepository glimRepository;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringTransactionData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final GroupLoanIndividualMonitoringReadPlatformService glimReadPlatformService,
            final LoanRepositoryWrapper loanRepositoryWrapper, final LoanChargeReadPlatformService loanChargeReadPlatformService,
            final GroupLoanIndividualMonitoringChargeReadPlatformService glimChargeReadPlatformService, final LoanReadPlatformService loanReadPlatformService,
            final GroupLoanIndividualMonitoringTransactionAssembler glimTransactionAssembler,
            final GroupLoanIndividualMonitoringRepository glimRepository) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.glimReadPlatformService = glimReadPlatformService;
        this.loanRepositoryWrapper =loanRepositoryWrapper;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.glimChargeReadPlatformService = glimChargeReadPlatformService;
        this.loanReadPlatformService =loanReadPlatformService;
        this.glimTransactionAssembler = glimTransactionAssembler;
        this.glimRepository = glimRepository;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveGlimRepaymentTemplate(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
            @QueryParam("transactionDate") final Date transactionDate, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission("loan");
        BigDecimal transactionAmount = BigDecimal.ZERO;
        List<GroupLoanIndividualMonitoringData> groupLoanIndividualMonitoringData = null;
        LoanTransactionData loanTransactionData = null;
        if (is(commandParam, "repayment")) {
            loanTransactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(loanId);
            groupLoanIndividualMonitoringData = (List<GroupLoanIndividualMonitoringData>) this.glimReadPlatformService
                    .retrieveAllActiveGlimByLoanId(loanId);
            Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            loan.updateDefautGlimMembers(this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true));
            groupLoanIndividualMonitoringData = this.glimTransactionAssembler.handleGLIMRepaymentTemplate(
                    groupLoanIndividualMonitoringData, loanTransactionData, loan, transactionDate);
            for (GroupLoanIndividualMonitoringData glimData : groupLoanIndividualMonitoringData) {
                transactionAmount = transactionAmount.add(glimData.getInstallmentAmount());
            }
        } else if (is(commandParam, "waiveinterest")) {
            groupLoanIndividualMonitoringData = (List<GroupLoanIndividualMonitoringData>) this.glimReadPlatformService
                    .retrieveWaiveInterestTemplate(loanId);

        } else if (is(commandParam, "waivecharge")) {
            groupLoanIndividualMonitoringData = (List<GroupLoanIndividualMonitoringData>) this.glimReadPlatformService
                    .retrieveWaiveChargeDetails(loanId);
            for (GroupLoanIndividualMonitoringData glimDetail : groupLoanIndividualMonitoringData) {
            	BigDecimal chargeAmount = GlimUtility.subtract(glimDetail.getChargeAmount(), glimDetail.getPaidChargeAmount());
            	glimDetail.setRemainingTransactionAmount(chargeAmount);
                glimDetail.setTransactionAmount(chargeAmount);
                transactionAmount = GlimUtility.add(transactionAmount,chargeAmount);
                glimDetail.setIsClientSelected(true);
            }
        } else if (is(commandParam, "writeoff") || is(commandParam, "prepay")) {
            groupLoanIndividualMonitoringData = (List<GroupLoanIndividualMonitoringData>) this.glimReadPlatformService
                    .retrieveAllActiveGlimByLoanId(loanId);
            for (GroupLoanIndividualMonitoringData glimDetail : groupLoanIndividualMonitoringData) {
                BigDecimal glimTransactionAmount = GlimUtility.subtract(glimDetail.getTotalPaybleAmount(), GlimUtility.add(glimDetail.getPaidAmount(),
                		glimDetail.getWaivedChargeAmount(), glimDetail.getWaivedInterestAmount()));
                glimDetail.setRemainingTransactionAmount(glimTransactionAmount);
                glimDetail.setTransactionAmount(glimTransactionAmount);
                transactionAmount = transactionAmount.add(glimTransactionAmount);
            }
        }else {
            throw new UnrecognizedQueryParamException("command", commandParam);
        }
        final GroupLoanIndividualMonitoringTransactionData groupLoanIndividualMonitoringTransactionData = new GroupLoanIndividualMonitoringTransactionData(
                transactionAmount, groupLoanIndividualMonitoringData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, groupLoanIndividualMonitoringTransactionData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String makeGLIMTransactions(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam, 
            final String apiRequestBodyAsJson) {
        
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        
        CommandProcessingResult result = null;
        if (is(commandParam, "repayment")) {
            final CommandWrapper commandRequest = builder.createGlimRepaymentTransaction(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "waiveinterest")) {
            final CommandWrapper commandRequest = builder.waiveInterestTransactionForGlim(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if (is(commandParam, "waivecharge")) {
            final CommandWrapper commandRequest = builder.waiveChargeTransactionForGlim(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "writeoff")) {
            final CommandWrapper commandRequest = builder.writeOffTransactionForGlim(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam);
        }

        return this.toApiJsonSerializer.serialize(result);
    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
    
    public static BigDecimal zeroIfNull(BigDecimal amount){
    	return (amount==null)?BigDecimal.ZERO:amount;
    }
}
