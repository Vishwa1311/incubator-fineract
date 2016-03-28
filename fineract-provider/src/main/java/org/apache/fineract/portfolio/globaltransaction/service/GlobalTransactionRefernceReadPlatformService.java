package org.apache.fineract.portfolio.globaltransaction.service;

import org.apache.fineract.portfolio.globaltransaction.data.GlobalTransactionReferenceData;

public interface GlobalTransactionRefernceReadPlatformService {

    GlobalTransactionReferenceData retrieveOne(Long transactionId);
    
    GlobalTransactionReferenceData retrieveOneByExternalRefNo(String externalRefNo);

    GlobalTransactionReferenceData retrieveOneByTransactionRefNo(String transactionRefNo);
}
