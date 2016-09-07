package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface GroupLoanIndividualMonitoringTransactionWritePlatformService {

    CommandProcessingResult repayGLIM(Long loanId, JsonCommand command, boolean isRecoveryRepayment);

    CommandProcessingResult waiveInterest(Long loanId, JsonCommand command);
    
    CommandProcessingResult waiveCharge(Long loanId, JsonCommand command);

    CommandProcessingResult writeOff(Long loanId, JsonCommand command);
}
