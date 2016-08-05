package org.apache.fineract.portfolio.loanaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "GLIMTRANSACTION", action = "WAIVEINTEREST")
public class GlimWaiveInterestCommandHandler implements NewCommandSourceHandler {

    private final GroupLoanIndividualMonitoringTransactionWritePlatformService glimTransactionWritePlatformService;
    
    @Autowired
    public GlimWaiveInterestCommandHandler(
            final GroupLoanIndividualMonitoringTransactionWritePlatformService glimTransactionWritePlatformService) {
        this.glimTransactionWritePlatformService = glimTransactionWritePlatformService;
    }
    
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.glimTransactionWritePlatformService.waiveInterest(command.getLoanId(), command);
    }
    
    

}
