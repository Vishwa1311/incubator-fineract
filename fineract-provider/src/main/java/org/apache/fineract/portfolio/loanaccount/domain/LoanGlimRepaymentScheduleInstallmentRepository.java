package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanGlimRepaymentScheduleInstallmentRepository extends JpaRepository<LoanGlimRepaymentScheduleInstallment, Long>,
        JpaSpecificationExecutor<LoanGlimRepaymentScheduleInstallment> {
    
    
    @Query("from LoanGlimRepaymentScheduleInstallment loanGlimRepayment where loanGlimRepayment.groupLoanIndividualMonitoring.id IN :ids ")
    List<LoanGlimRepaymentScheduleInstallment> getLoanGlimRepaymentScheduleInstallmentByGlimIds(@Param("ids") Collection<Long> ids);

}