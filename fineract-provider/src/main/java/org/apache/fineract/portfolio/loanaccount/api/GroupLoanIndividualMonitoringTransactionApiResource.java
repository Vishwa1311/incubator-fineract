package org.apache.fineract.portfolio.loanaccount.api;

import java.math.BigDecimal;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
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
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveGlimRepaymentTemplate(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission("loan");
        BigDecimal transactionAmount = BigDecimal.ZERO;
        Collection<GroupLoanIndividualMonitoringData> groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService
                .retrieveAllByLoanId(loanId);
        Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
        MonetaryCurrency currency = loan.getCurrency();
        BigDecimal numberOfRepayment = BigDecimal.valueOf(Long.valueOf(loan.getLoanProductRelatedDetail().getNumberOfRepayments().toString()));
        for (GroupLoanIndividualMonitoringData groupLoanIndividualMonitoringDetail : groupLoanIndividualMonitoringData) {
            transactionAmount = transactionAmount.add(groupLoanIndividualMonitoringDetail.getInstallmentAmount());
            groupLoanIndividualMonitoringDetail.setInterestAmount(Money.of(currency,BigDecimal.valueOf(groupLoanIndividualMonitoringDetail.getInterestAmount().doubleValue()/numberOfRepayment.doubleValue())).getAmount());            
            groupLoanIndividualMonitoringDetail.setChargeAmount(Money.of(currency,BigDecimal.valueOf(groupLoanIndividualMonitoringDetail.getChargeAmount().doubleValue()/numberOfRepayment.doubleValue())).getAmount());
        }
        final GroupLoanIndividualMonitoringTransactionData groupLoanIndividualMonitoringTransactionData = new GroupLoanIndividualMonitoringTransactionData(
                transactionAmount, groupLoanIndividualMonitoringData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, groupLoanIndividualMonitoringTransactionData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String makeGLIMRepayment(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createGlimRepaymentTransaction(loanId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}
