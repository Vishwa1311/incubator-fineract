package org.apache.fineract.portfolio.loanaccount.data;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.joda.time.LocalDate;

public class LoanForeClosureDetailDTO {

    private final Loan loan;
    private final LocalDate transactionDate;
    private final String note;
    private final boolean isAccountTransfer;
    private final boolean isRecoveryRepayment;
    private final PaymentDetail paymentDetail;
    private final String txnExternalId;

    public LoanForeClosureDetailDTO(final Loan loan, final LocalDate transactionDate, final String note) {
        this.loan = loan;
        this.transactionDate = transactionDate;
        this.note = note;
        this.isAccountTransfer = false;
        this.isRecoveryRepayment = false;
        this.paymentDetail = null;
        this.txnExternalId = null;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public String getNote() {
        return this.note;
    }

    public boolean isAccountTransfer() {
        return this.isAccountTransfer;
    }

    public boolean isRecoveryRepayment() {
        return this.isRecoveryRepayment;
    }

    public PaymentDetail getPaymentDetail() {
        return this.paymentDetail;
    }

    public String getTxnExternalId() {
        return this.txnExternalId;
    }

}
