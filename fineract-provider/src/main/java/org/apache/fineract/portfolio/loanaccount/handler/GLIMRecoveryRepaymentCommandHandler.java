package org.apache.fineract.portfolio.loanaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "GLIMTRANSACTION", action = "RECOVERYPAYMENT")
public class GLIMRecoveryRepaymentCommandHandler  implements NewCommandSourceHandler {
	
	private final GroupLoanIndividualMonitoringTransactionWritePlatformService glimTransactionWritePlatformService;

    @Autowired
    public GLIMRecoveryRepaymentCommandHandler(
            final GroupLoanIndividualMonitoringTransactionWritePlatformService groupLoanIndividualMonitoringTransactionWritePlatformService) {
        this.glimTransactionWritePlatformService = groupLoanIndividualMonitoringTransactionWritePlatformService;
    }
	
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
    	boolean isRecoveryRepayment = true;
        return this.glimTransactionWritePlatformService.repayGLIM(command.getLoanId(), command, isRecoveryRepayment);
    }

}
