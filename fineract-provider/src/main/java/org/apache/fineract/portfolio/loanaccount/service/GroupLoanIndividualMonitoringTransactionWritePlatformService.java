package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface GroupLoanIndividualMonitoringTransactionWritePlatformService {

    CommandProcessingResult repayGLIM(Long loanId, JsonCommand command);
}
