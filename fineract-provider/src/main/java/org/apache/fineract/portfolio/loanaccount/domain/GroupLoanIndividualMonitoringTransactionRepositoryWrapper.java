package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Collection;

import org.apache.fineract.portfolio.loanaccount.exception.GroupLoanIndividualMonitoringTransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringTransactionRepositoryWrapper {

    public final GroupLoanIndividualMonitoringTransactionRepository groupLoanIndividualMonitoringTransactionRepository;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionRepositoryWrapper(
            final GroupLoanIndividualMonitoringTransactionRepository groupLoanIndividualMonitoringTransactionRepository) {
        this.groupLoanIndividualMonitoringTransactionRepository = groupLoanIndividualMonitoringTransactionRepository;
    }

    public void saveAsList(final Collection<GroupLoanIndividualMonitoringTransaction> groupLoanIndividualMonitoringTransaction) {
        this.groupLoanIndividualMonitoringTransactionRepository.save(groupLoanIndividualMonitoringTransaction);
    }

    public GroupLoanIndividualMonitoringTransaction findOneWithNotFoundDetection(final Long id) {
        final GroupLoanIndividualMonitoringTransaction entity = this.groupLoanIndividualMonitoringTransactionRepository.findOne(id);
        if (entity == null) { throw new GroupLoanIndividualMonitoringTransactionNotFoundException(id); }
        return entity;
    }
}
