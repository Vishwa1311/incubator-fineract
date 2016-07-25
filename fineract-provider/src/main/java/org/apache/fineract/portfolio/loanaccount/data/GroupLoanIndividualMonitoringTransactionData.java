package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Collection;

public class GroupLoanIndividualMonitoringTransactionData {

    private final BigDecimal transactionAmount;
    private final Collection<GroupLoanIndividualMonitoringData> clientMembers;

    public GroupLoanIndividualMonitoringTransactionData(final BigDecimal transactionAmount,
            final Collection<GroupLoanIndividualMonitoringData> groupMemberDetails) {
        this.transactionAmount = transactionAmount;
        this.clientMembers = groupMemberDetails;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public Collection<GroupLoanIndividualMonitoringData> getGroupMemberDetails() {
        return this.clientMembers;
    }
}
