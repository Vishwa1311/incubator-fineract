package com.finflux.sms.service;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;

public interface SmsEventListenerService {

    public void sendMessgeProcessForLoanDisbursal(final Loan loan);

    public void sendMessgeProcessForRepayment(final LoanTransaction loanTransaction);
}