package org.apache.fineract.portfolio.globaltransaction.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when global transaction reference resources are not found.
 */
public class GlobalTransactionReferenceNotFoundException extends AbstractPlatformResourceNotFoundException {

    public GlobalTransactionReferenceNotFoundException(final Long id) {
        super("error.msg.global.transaction.reference.id.invalid", "Global transaction reference with identifier " + id + " does not exist", id);
    }
    
    public GlobalTransactionReferenceNotFoundException(final String transactionRefNo) {
        super("error.msg.global.transaction.with.reference.no.does.not.exist", "Global transaction with reference " + transactionRefNo + " does not exist", transactionRefNo);
    }
}