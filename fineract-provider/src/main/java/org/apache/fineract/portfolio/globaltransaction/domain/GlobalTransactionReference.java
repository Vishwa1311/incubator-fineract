package org.apache.fineract.portfolio.globaltransaction.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.SavingsReversalType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.springframework.util.CollectionUtils;

@Entity
@Table(name = "m_global_transaction_reference", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "external_ref_no", "transaction_date" }, name = "gtr_external_ref_no_UNIQUE"),
        @UniqueConstraint(columnNames = { "transaction_ref_no", "transaction_date" }, name = "gtr_transaction_ref_no_UNIQUE") })
public class GlobalTransactionReference extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "external_ref_no", length = 18, nullable = false)
    private String extranalRefNo;

    @Column(name = "transaction_ref_no", length = 16, nullable = false)
    private String transactionRefNo;

    @Column(name = "is_reversed", nullable = false)
    private boolean isReversed;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(name = "is_processed", nullable = true)
    private boolean isProcessed;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transactionReference", orphanRemoval = true)
    private final List<SavingsAccountTransaction> savingsTransactions = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transactionReference", orphanRemoval = true)
    private final List<LoanTransaction> loanTransactions = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transactionReference", orphanRemoval = true)
    private final List<JournalEntry> journalEntryTransactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversal_of_id")
    private GlobalTransactionReference reversalOf;

    @Column(name = "reversal_type", nullable = false)
    private Integer reversalType;

    protected GlobalTransactionReference() {

    }

    private GlobalTransactionReference(final String extranalRefNo, final LocalDate transactionDate, final String transactionRefNo, final GlobalTransactionReference reversalOf, final Integer reversalType) {
        this.extranalRefNo = extranalRefNo;
        this.transactionDate = transactionDate.toDate();
        this.transactionRefNo = transactionRefNo;
        this.isProcessed = false;
        this.reversalOf = reversalOf;
        this.reversalType = reversalType;
    }

    public static GlobalTransactionReference createNew(final String extranalRefNo, final LocalDate transactionDate,
            final String transactionRefNo) {
        final GlobalTransactionReference reversalOf = null;
        final Integer reversalType = SavingsReversalType.NOT_REVERSED.getValue();
        return new GlobalTransactionReference(extranalRefNo, transactionDate, transactionRefNo, reversalOf, reversalType);
    }

    public static GlobalTransactionReference reverse(final String externalRefNo, final String transactionRefNo,
            final GlobalTransactionReference originalTransactionReference, final Integer reversalType) {

        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
        GlobalTransactionReference newTransactionReference = new GlobalTransactionReference(externalRefNo, transactionDate,
                transactionRefNo, originalTransactionReference, reversalType);

        return newTransactionReference;
    }

    public void undoSavingsCharges(final Long savingsId) {
        final List<SavingsAccountTransaction> nonReversedChargeTransactions = this.retrieveNonReversedSavingsChargeTransactions(savingsId);
        if (!CollectionUtils.isEmpty(nonReversedChargeTransactions)) {
            for (SavingsAccountTransaction transaction : nonReversedChargeTransactions) {
                transaction.undoTransaction();
            }
        }
    }

    private List<SavingsAccountTransaction> retrieveNonReversedSavingsChargeTransactions(final Long savingsId) {
        final List<SavingsAccountTransaction> nonReversedTransactions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(savingsTransactions)) {
            for (SavingsAccountTransaction transaction : this.savingsTransactions) {
                if (transaction.isChargeTransactionAndNotReversed() && transaction.isTransactionOfAccount(savingsId)) {
                    nonReversedTransactions.add(transaction);
                }
            }
        }
        return nonReversedTransactions;
    }

    public List<SavingsAccountTransaction> retrieveNonReversedSavingsTransactions() {
        final List<SavingsAccountTransaction> nonReversedTransactions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(savingsTransactions)) {
            for (SavingsAccountTransaction transaction : this.savingsTransactions) {
                if (transaction.isNotReversed() && !transaction.isChargeTransaction()) {
                    nonReversedTransactions.add(transaction);
                }
            }
        }
        return nonReversedTransactions;
    }

    public List<JournalEntry> retrieveNonReversedJournalEntries() {
        final List<JournalEntry> nonReversedTransactions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(journalEntryTransactions)) {
            for (JournalEntry transaction : this.journalEntryTransactions) {
                if (!transaction.isReversed()) {
                    nonReversedTransactions.add(transaction);
                }
            }
        }
        return nonReversedTransactions;
    }

    public void reverse() {
        this.isReversed = true;
    }

    public String transactionRefNo() {
        return this.transactionRefNo;
    }

    public boolean isReversed() {
        return this.isReversed;
    }

    public boolean isBusinessReversal() {
        return this.reversalOf != null;
    }

    public void markAsCompleteReversal() {
        this.reversalType = SavingsReversalType.COMPLETE.getValue();
    }

    public boolean isCompleteBusinessReversal() {
        return SavingsReversalType.fromInt(this.reversalType).isCompleteReversal();
    }

    public List<SavingsAccountTransaction> getSavingAccountTransactions() {
        final List<SavingsAccountTransaction> nonReversedTransactions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(savingsTransactions)) {
            for (SavingsAccountTransaction transaction : this.savingsTransactions) {
                if (transaction.isNotReversed()) {
                    nonReversedTransactions.add(transaction);
                }
            }
        }
        return nonReversedTransactions;
    }

    public void markAsNotReversed() {
        this.reversalType = SavingsReversalType.NOT_REVERSED.getValue();
    }

    public void undoBusinessReversal() {
        if (this.reversalOf != null) {
            for (final SavingsAccountTransaction savingsAccountTransaction : this.reversalOf.getSavingAccountTransactions()) {
                savingsAccountTransaction.markAsNotReversed();
            }
            this.reversalOf.markAsNotReversed();
        }
    }

    public String getExtranalRefNo() {
        return this.extranalRefNo;
    }

    public List<LoanTransaction> getLoanTransactions() {
        return this.loanTransactions;
    }
}
