package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class GroupLoanIndividualMonitoringTransactionNotFoundException extends AbstractPlatformResourceNotFoundException {

    public GroupLoanIndividualMonitoringTransactionNotFoundException(Long id) {
        super("error.msg.glim.transaction.id.invalid",
                "Group Loan individual transaction record with identifier " + id + " does not exist", id);
    }

}
