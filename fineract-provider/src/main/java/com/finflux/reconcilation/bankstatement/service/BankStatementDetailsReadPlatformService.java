package com.finflux.reconcilation.bankstatement.service;

import java.util.List;


import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

public interface BankStatementDetailsReadPlatformService {
	
	public List<BankStatementDetailsData> changedBankStatementDetailsData(final Long bankStatementId);

    public List<BankStatementDetailsData> retrieveBankStatementDetailsReconciledData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveBankStatementDetailsDataForReconcile(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveBankStatementNonPortfolioData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveBankStatementMiscellaneousData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveAllBankStatementData(final Long bankStatementId);
    
}
