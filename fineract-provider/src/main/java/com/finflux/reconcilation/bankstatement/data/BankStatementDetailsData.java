package com.finflux.reconcilation.bankstatement.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.joda.time.LocalDate;

import com.finflux.reconcilation.bank.data.BankData;

@SuppressWarnings("unused")
public class BankStatementDetailsData {

    private final Long id;
    private Long bankStatementId;
    private String transactionId;
    private Date transactionDate;
    private String description;
    private BigDecimal amount;
    private String mobileNumber;
    private String clientAccountNumber;
    private String loanAccountNumber;
    private final Boolean isReconciled;
    private final String transactionType;
    private final String branchExternalId;
    private final String groupExternalId;
    private String accountingType;
    private String glCode;
    private  Long loanTransactionId;
    //non-portfollio data
    private  LoanTransactionData loanTransactionData;
    private  Collection<LoanTransactionData> loanTransactionOptions;
    private Integer optionsLength = null;
    // portfolio data
    private Long branch;
    private String glAccount;
    private  String branchName;
    private  BankData bankData;
    private Integer bankStatementDetailType;
    
    

    public BankStatementDetailsData(Long id, Long bankStatementId, Date transactionDate, String description,
			BigDecimal amount, String mobileNumber,String loanAccountNumber,String transactionType,
			String groupExternalId, final Long loanTransactionId, Boolean isReconciled, String accountingType) {
		this.id = id;
		this.bankStatementId = bankStatementId;
		this.transactionDate = transactionDate;
		this.description = description;
		this.amount = amount;
		this.mobileNumber = mobileNumber;
		this.loanAccountNumber = loanAccountNumber;
		this.transactionType = transactionType;
		this.groupExternalId = groupExternalId;
		this.loanTransactionId = loanTransactionId;
		this.accountingType = accountingType;
		this.isReconciled = isReconciled;
		this.loanTransactionData = null;
		this.loanTransactionOptions = null;
		this.clientAccountNumber = null;
		this.branchExternalId = null;
		this.branch = null;
		this.glAccount = null;
		this.branchName = null;
		this.bankData = null;
		this.transactionId = null;
		this.glCode = null;
		this.bankStatementDetailType = null;
	}
    
    public static BankStatementDetailsData reconciledData(Long id, Long bankStatementId, Date transactionDate, String description,
			BigDecimal amount, String mobileNumber,String loanAccountNumber,String transactionType,String
			groupExternalId, final Long loanTransactionId, boolean isReconciled, String accountingType) {

        return new BankStatementDetailsData(id, bankStatementId, transactionDate, description,
    			amount, mobileNumber, loanAccountNumber, transactionType, groupExternalId, 
    			loanTransactionId, isReconciled, accountingType);
    }
    
    public BankStatementDetailsData(Long id, Long bankStatementId, Date transactionDate, String description,
			BigDecimal amount, String mobileNumber,String loanAccountNumber, Boolean isReconciled,
			String transactionType,String groupExternalId, final Collection<LoanTransactionData> loanTransactionOptions) {
    	this.id = id;
		this.bankStatementId = bankStatementId;
		this.transactionDate = transactionDate;
		this.description = description;
		this.amount = amount;
		this.mobileNumber = mobileNumber;
		this.loanAccountNumber = loanAccountNumber;
		this.isReconciled = isReconciled;
		this.transactionType = transactionType;
		this.groupExternalId = groupExternalId;
		this.loanTransactionOptions = loanTransactionOptions;
		this.loanTransactionId = null;
		this.clientAccountNumber = null;
		this.loanTransactionData = null;
		this.branchExternalId = null;
		this.branch = null;
		this.glAccount = null;
		this.branchName = null;
		this.bankData = null;
		this.transactionId = null;
		this.accountingType = null;
		this.glCode = null;
		this.bankStatementDetailType = null;	
		
	}
    
    public static BankStatementDetailsData dataForReconcile(Long id, Long bankStatementId, Date transactionDate, String description,
			BigDecimal amount, String mobileNumber,String loanAccountNumber, Boolean isReconciled,
			String transactionType,String groupExternalId, Collection<LoanTransactionData> loanTransactionOptions) {

        return new BankStatementDetailsData(id, bankStatementId, transactionDate, description,
    			amount, mobileNumber, loanAccountNumber, isReconciled, transactionType, groupExternalId, loanTransactionOptions);
    }
    
    
    public BankStatementDetailsData(Long id, Long bankStatementId, String transactionId, Date transactionDate,
			BigDecimal amount, final String branchExternalId,Long branch,String glAccount,
			String branchName,String accountingType, String glCode, boolean isReconciled) {
    	this.id = id;
		this.bankStatementId = bankStatementId;
		this.transactionId = transactionId;
		this.transactionDate = transactionDate;
		this.amount = amount;
		this.branchExternalId = branchExternalId;
		this.branch = branch;
		this.glAccount = glAccount;
		this.branchName = branchName;
		this.accountingType = accountingType;
		this.glCode = glCode;
		this.isReconciled = isReconciled;
		this.loanTransactionId = null;
		this.bankData = null;		
		this.description = null;
		this.mobileNumber = null;
		this.loanAccountNumber = null;
		this.transactionType = null;
		this.groupExternalId = null;
		this.loanTransactionOptions = null;
		this.clientAccountNumber = null;
		this.loanTransactionData = null;
		this.bankStatementDetailType = null;	
		
	}
    
    public static BankStatementDetailsData nonPortfolioData(Long id, Long bankStatementId, String transactionId, Date transactionDate,
			BigDecimal amount, final String branchExternalId,Long branch,String glAccount,
			String branchName,String accountingType, String glCode, boolean isReconciled) {

        return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate,
    			amount, branchExternalId, branch,glAccount,branchName,accountingType, glCode, isReconciled);
    }
    
    

	public BankStatementDetailsData(final Long id, final Long bankStatementId, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final Boolean isReconciled, final Long loanTransaction,
            final Long branch, final String glAccount, final String accountingType, final String glCode, final Boolean isJournalEntry,
            final BankData bankData, final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {
        this.id = id;
        this.bankStatementId = bankStatementId;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.clientAccountNumber = clientAccountNumber;
        this.loanAccountNumber = loanAccountNumber;
        this.isReconciled = isReconciled;
        this.loanTransactionId = loanTransaction;
       /* this.loanTransaction = loanTransaction;
        this.loanTransactionId = 0l;
        this.transactionAmount = null;
        this.paymentDetailData = null;
        this.officeId = null;*/
        this.transactionDate = null;
        /*this.date = null;
        this.type = null;
        this.officeName = null;
        this.externalId = null;
        this.submittedOnDate = null;
        this.loanAccountNo = null;*/
        this.branch = branch;
        this.glAccount = glAccount;
        this.accountingType = accountingType;
        this.glCode = glCode;
        this.bankData = bankData;
        this.branchName = branchName;
        this.transactionType = transactionType;
        this.branchExternalId = branchExternalId;
        this.groupExternalId =groupExternalId;
        this.bankStatementDetailType = null;
        
    }

    public BankStatementDetailsData(final Long id, final Long bankStatementId, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final Boolean isReconciled, final Long loanTransaction,
            final Long loanTransactionId, final Long officeId, final String officeName, final LoanTransactionEnumData type,
            final LocalDate date, final PaymentDetailData paymentDetailData, final BigDecimal transactionAmount, final String externalId,
            final LocalDate submittedOnDate, final String loanAccountNo, final Long branch, final String glAccount,
            final String accountingType, final String glCode, final BankData bankData,
            final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {
        this.id = id;
        this.bankStatementId = bankStatementId;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.clientAccountNumber = clientAccountNumber;
        this.loanAccountNumber = loanAccountNumber;
        this.isReconciled = isReconciled;
        this.branch = branch;
        this.glAccount = glAccount;
        this.accountingType = accountingType;
        this.glCode = glCode;
        this.bankData = bankData;
        this.branchName = branchName;
        this.transactionType = transactionType;
        this.branchExternalId = branchExternalId;
        this.groupExternalId =groupExternalId;
        this.bankStatementDetailType = null;
    }

    public static BankStatementDetailsData instance(final Long id, final Long bankStatementId, final String transactionId,
            final Date transactionDate, final String description, final BigDecimal amount, final String mobileNumber,
            final String clientAccountNumber, final String loanAccountNumber, final Boolean isReconciled,
            final Long loanTransaction, final Long branch, final String glAccount, final String accountingType, final String glCode,
            final Boolean isJournalEntry, final BankData bankData, final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {

        return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, isReconciled, loanTransaction, branch, glAccount,
                accountingType, glCode, isJournalEntry, bankData, branchName, transactionType, branchExternalId, groupExternalId);
    }

    public static BankStatementDetailsData instance(final Long id, final Long bankStatementId, final String transactionId,
            final Date transactionDate, final String description, final BigDecimal amount, final String mobileNumber,
            final String clientAccountNumber, final String loanAccountNumber, final Boolean isReconciled,
            final Long loanTransaction, final Long loanTransactionId, final Long officeId, final String officeName,
            final LoanTransactionEnumData type, final LocalDate date, final PaymentDetailData paymentDetailData,
            final BigDecimal transactionAmount, final String externalId, final LocalDate submittedOnDate, final String loanAccountNo,
            final Long branch, final String glAccount, final String accountingType, final String glCode,
            final BankData bankData, final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {

        return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, isReconciled, loanTransaction, loanTransactionId, officeId,
                officeName, type, date, paymentDetailData, transactionAmount, externalId, submittedOnDate, loanAccountNo, branch,
                glAccount, accountingType, glCode, bankData, branchName, transactionType, branchExternalId, groupExternalId);
    }
    
    public BankStatementDetailsData(Long id, Long bankStatementId, Date transactionDate, String description,
			BigDecimal amount, String mobileNumber,String loanAccountNumber, Boolean isReconciled,
			String transactionType,String groupExternalId, String accountingType) {
    	this.id = id;
		this.bankStatementId = bankStatementId;
		this.transactionDate = transactionDate;
		this.description = description;
		this.amount = amount;
		this.mobileNumber = mobileNumber;
		this.loanAccountNumber = loanAccountNumber;
		this.isReconciled = isReconciled;
		this.transactionType = transactionType;
		this.groupExternalId = groupExternalId;
		this.accountingType = accountingType;
		this.loanTransactionOptions = null;
		this.loanTransactionId = null;
		this.clientAccountNumber = null;
		this.loanTransactionData = null;
		this.branchExternalId = null;
		this.branch = null;
		this.glAccount = null;
		this.branchName = null;
		this.bankData = null;
		this.transactionId = null;
		this.glCode = null;
		this.bankStatementDetailType = null;	
		
	}
    
    public BankStatementDetailsData(Long id, Long bankStatementId, String transactionId, Long loanTransactionId, Integer bankStatementDetailType) {
		this.id = id;
		this.bankStatementId = bankStatementId;
		this.transactionId = transactionId;
		this.loanTransactionId = loanTransactionId;
		this.bankStatementDetailType = bankStatementDetailType;
		this.transactionDate = null;
		this.description = null;
		this.amount = null;
		this.mobileNumber = null;
		this.loanAccountNumber = null;
		this.transactionType = null;
		this.groupExternalId = null;
		this.accountingType = null;
		this.isReconciled = null;
		this.loanTransactionData = null;
		this.loanTransactionOptions = null;
		this.clientAccountNumber = null;
		this.branchExternalId = null;
		this.branch = null;
		this.glAccount = null;
		this.branchName = null;
		this.bankData = null;
		this.glCode = null;
	}

    public Long getId() {
        return id;
    }

    public Long getBankStatementId() {
        return bankStatementId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getClientAccountNumber() {
        return clientAccountNumber;
    }

    public String getLoanAccountNumber() {
        return loanAccountNumber;
    }

    public String getGroupExternalId() {
        return groupExternalId;
    }

    public Boolean getIsReconciled() {
        return isReconciled;
    }

    public Long getBranch() {
        return branch;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public String getAccountingType() {
        return accountingType;
    }

    public String getGlCode() {
        return glCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public BankData getBankData() {
        return bankData;
    }

    public String getTransactionType() {
        return transactionType;
    }
    
    public String getBranchExternalId() {
        return branchExternalId;
    }

	public LoanTransactionData getLoanTransactionData() {
		return this.loanTransactionData;
	}

	public Collection<LoanTransactionData> getLoanTransactionOptions() {
		return this.loanTransactionOptions;
	}
    
    public void setLoanTransactionData(LoanTransactionData loanTransactionData){
    	this.loanTransactionData = loanTransactionData;
    }
    
    public void setLoanTransactionOptions(Collection<LoanTransactionData> loanTransactionOptions){
    	this.loanTransactionOptions = loanTransactionOptions;
    }

	public Long getLoanTransactionId() {
		return this.loanTransactionId;
	}

	public void setLoanTransactionId(Long loanTransactionId) {
		this.loanTransactionId = loanTransactionId;
	}

	public Integer getOptionsLength() {
		return this.optionsLength;
	}

	public void setOptionsLength(Integer optionsLength) {
		this.optionsLength = optionsLength;
	}

	public Integer getBankStatementDetailType() {
		return this.bankStatementDetailType;
	}

	public void setBankStatementDetailType(Integer bankStatementDetailType) {
		this.bankStatementDetailType = bankStatementDetailType;
	}   
	
	
    
}
