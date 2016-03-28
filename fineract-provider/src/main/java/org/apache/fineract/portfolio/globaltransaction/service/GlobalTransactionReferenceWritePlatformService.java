package org.apache.fineract.portfolio.globaltransaction.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface GlobalTransactionReferenceWritePlatformService {

    CommandProcessingResult create(JsonCommand command);

    CommandProcessingResult undoTransaction(Long transactionId);
    
    CommandProcessingResult transactionReversal(Long transactionId);
}