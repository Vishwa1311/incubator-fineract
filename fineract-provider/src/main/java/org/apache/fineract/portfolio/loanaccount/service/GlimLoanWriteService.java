package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;

public interface GlimLoanWriteService {

    void generateGlimLoanRepaymentSchedule(final Loan loan);
}
