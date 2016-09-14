package com.finflux.reconcilation.bankstatement.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_bank_statement_details")
public class BankStatementDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "bank_statement_id", nullable = false)
    private BankStatement bankStatement;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "client_account_number")
    private String clientAccountNumber;

    @Column(name = "loan_account_number")
    private String loanAccountNumber;

    @Column(name = "group_external_id")
    private String groupExternalId;

    @Column(name = "is_reconciled")
    private Boolean isReconciled;

    @OneToOne(optional = true)
    @JoinColumn(name = "loan_transaction", nullable = true)
    private LoanTransaction loanTransaction;

    @Column(name = "accounting_type")
    private String accountingType;

    @Column(name = "branch_external_id")
    private String branchExternalId;

    @Column(name = "gl_code")
    private String glCode;

    @Column(name = "transaction_type")
    private String transactionType;
    
    @Column(name = "bank_statement_detail_type", nullable = false)
    private Integer bankStatementDetailType;
    

    public BankStatementDetails(final BankStatement bankStatement, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final String groupExternalId, final Boolean isReconciled,
            final LoanTransaction loanTransaction, final String branchExternalId, final String accountingType, final String glCode,
            final String transactionType, final Integer bankStatementDetailType) {
        this.bankStatement = bankStatement;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.clientAccountNumber = clientAccountNumber;
        this.loanAccountNumber = loanAccountNumber;
        this.groupExternalId = groupExternalId;
        this.isReconciled = isReconciled;
        this.loanTransaction = loanTransaction;
        this.branchExternalId = branchExternalId;
        this.accountingType = accountingType;
        this.glCode = glCode;
        this.transactionType = transactionType;
        this.bankStatementDetailType = bankStatementDetailType;
    }

    public BankStatementDetails() {
        super();
    }

    public static BankStatementDetails instance(final BankStatement bankStatement, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final String groupExternalId, final Boolean isReconciled,
            final LoanTransaction loanTransaction, final String branchExternalId, final String accountingType, final String glCode,
            final String transactionType, final Integer bankStatementDetailType) {

        return new BankStatementDetails(bankStatement, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, groupExternalId, isReconciled, loanTransaction, branchExternalId, accountingType, glCode,
                transactionType, bankStatementDetailType);
    }

    public void setBankStatement(BankStatement bankStatement) {
        this.bankStatement = bankStatement;
    }

    public void setIsReconciled(Boolean isReconciled) {
        this.isReconciled = isReconciled;
    }

    public void setLoanTransaction(LoanTransaction loanTransaction) {
        this.loanTransaction = loanTransaction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getGroupExternalId() {
        return groupExternalId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

	public Integer getBankStatementDetailType() {
		return this.bankStatementDetailType;
	}

	public void setBankStatementDetailType(Integer bankStatementDetailType) {
		this.bankStatementDetailType = bankStatementDetailType;
	}
       

}
