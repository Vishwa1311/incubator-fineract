package org.apache.fineract.portfolio.loanaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "GLIMTRANSACTION", action = "REPAYMENT")
public class GLIMRepaymentTransactionCommandHandler implements NewCommandSourceHandler {

    private final GroupLoanIndividualMonitoringTransactionWritePlatformService groupLoanIndividualMonitoringTransactionWritePlatformService;

    @Autowired
    public GLIMRepaymentTransactionCommandHandler(
            final GroupLoanIndividualMonitoringTransactionWritePlatformService groupLoanIndividualMonitoringTransactionWritePlatformService) {
        this.groupLoanIndividualMonitoringTransactionWritePlatformService = groupLoanIndividualMonitoringTransactionWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.groupLoanIndividualMonitoringTransactionWritePlatformService.repayGLIM(command.getLoanId(), command);
    }

}
