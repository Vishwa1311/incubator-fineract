package org.apache.fineract.portfolio.loanaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupLoanIndividualMonitoringTransactionRepository extends JpaRepository<GroupLoanIndividualMonitoringTransaction, Long>,
        JpaSpecificationExecutor<GroupLoanIndividualMonitoringTransaction> {

}
