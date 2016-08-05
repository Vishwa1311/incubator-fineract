package org.apache.fineract.portfolio.loanaccount.api;

import java.math.BigDecimal;
import java.util.Collection;

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
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringReadPlatformService;
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
    private final GroupLoanIndividualMonitoringReadPlatformService groupLoanIndividualMonitoringReadPlatformService;
    private final LoanRepositoryWrapper loanRepositoryWrapper;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringTransactionData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final GroupLoanIndividualMonitoringReadPlatformService groupLoanIndividualMonitoringReadPlatformService,
            final LoanRepositoryWrapper loanRepositoryWrapper) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.groupLoanIndividualMonitoringReadPlatformService = groupLoanIndividualMonitoringReadPlatformService;
        this.loanRepositoryWrapper =loanRepositoryWrapper;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveGlimRepaymentTemplate(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission("loan");
        BigDecimal transactionAmount = BigDecimal.ZERO;
        Collection<GroupLoanIndividualMonitoringData> groupLoanIndividualMonitoringData = null;
        if (is(commandParam, "repayment")) {
            groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService.retrieveAllByLoanId(loanId);
            Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            BigDecimal numberOfRepayment = BigDecimal.valueOf(Long.valueOf(loan.getLoanProductRelatedDetail().getNumberOfRepayments()
                    .toString()));
            for (GroupLoanIndividualMonitoringData groupLoanIndividualMonitoringDetail : groupLoanIndividualMonitoringData) {
                transactionAmount = transactionAmount.add(groupLoanIndividualMonitoringDetail.getInstallmentAmount());
                if (groupLoanIndividualMonitoringDetail.getInterestAmount() != null) {
                    groupLoanIndividualMonitoringDetail.setInterestAmount(BigDecimal.valueOf(
                            groupLoanIndividualMonitoringDetail.getInterestAmount().doubleValue() / numberOfRepayment.doubleValue())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                if (groupLoanIndividualMonitoringDetail.getChargeAmount() != null) {
                    groupLoanIndividualMonitoringDetail.setChargeAmount(BigDecimal.valueOf(
                            groupLoanIndividualMonitoringDetail.getChargeAmount().doubleValue() / numberOfRepayment.doubleValue())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        } else if (is(commandParam, "waiveinterest")) {
            groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService.retrieveWaiveInterestTemplate(loanId);
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
        }

        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam); }

        return this.toApiJsonSerializer.serialize(result);
    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
