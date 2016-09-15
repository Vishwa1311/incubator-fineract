package com.finflux.reconcilation.bankstatement.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;

import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

public interface BankLoanTransactionsReadPlatformService {

    public Collection<LoanTransactionData> retrieveLoanTransactionsForGroup(final BigDecimal amount,
            final String groupExternalId, String transactionType);

    public Collection<LoanTransactionData> retrieveLoanTransactionsForBankDetails(Long loanTransactionId);
    
    public LoanTransactionData getReconciledLoanTransaction(Long loanTransactionId);
    
    public List<LoanTransactionData> getLoanTransactionOptions(BankStatementDetailsData bankStatementDetailData);
}
