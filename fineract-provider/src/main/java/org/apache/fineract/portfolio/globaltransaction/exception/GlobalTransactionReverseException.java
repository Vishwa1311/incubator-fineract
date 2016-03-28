package org.apache.fineract.portfolio.globaltransaction.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class GlobalTransactionReverseException extends AbstractPlatformDomainRuleException {

    public GlobalTransactionReverseException(final String transactionRefNo) {
        super("error.msg.global.transaction.with.reference.number.already.reversed", "Global transaction with reference " + transactionRefNo
                + " already reversed", transactionRefNo);
    }

}
