package org.apache.fineract.portfolio.globaltransaction.data;

import java.util.Collection;

import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.joda.time.LocalDate;

/**
 * Immutable data object represents a partial data of Global Transaction
 * reference.
 */
public class GlobalTransactionReferenceData {

    private final Long transactionId;
    private final String transactionRefNo;
    private final String externalRefNo;
    private final LocalDate transactionDate;
    private final boolean isReversed;
    private final Long reversalOfTransactionId;

    @SuppressWarnings("unused")
    private final Collection<SavingsAccountTransactionData> accountTransactions;
    @SuppressWarnings("unused")
    private final Collection<JournalEntryData> journalEntries;

    private GlobalTransactionReferenceData(final Long id, String transactionRefNo, String externalRefNo, LocalDate transactionDate,
            final boolean isReversed, final Collection<SavingsAccountTransactionData> savingsAccountTransactions,
            final Collection<JournalEntryData> journalEntries, final Long reversalOfTransactionId) {
        this.transactionId = id;
        this.transactionRefNo = transactionRefNo;
        this.externalRefNo = externalRefNo;
        this.transactionDate = transactionDate;
        this.isReversed = isReversed;
        this.accountTransactions = savingsAccountTransactions;
        this.journalEntries = journalEntries;
        this.reversalOfTransactionId = reversalOfTransactionId;
    }

    public static GlobalTransactionReferenceData instance(final Long id, String transactionRefNo, String externalRefNo, 
            final LocalDate transactionDate, final boolean isReversed, final Long reversalOfTransactionId) {
        final Collection<SavingsAccountTransactionData> savingsAccountTransactions = null;
        final Collection<JournalEntryData> journalEntries = null;
        return new GlobalTransactionReferenceData(id, transactionRefNo, externalRefNo, transactionDate, isReversed,
                savingsAccountTransactions, journalEntries, reversalOfTransactionId);
    }

    public static GlobalTransactionReferenceData withAssociations(final GlobalTransactionReferenceData tranasactionReferenceData,
            final Collection<SavingsAccountTransactionData> savingsAccountTransactions, final Collection<JournalEntryData> journalEntries) {
        return new GlobalTransactionReferenceData(tranasactionReferenceData.transactionId, tranasactionReferenceData.transactionRefNo,
                tranasactionReferenceData.externalRefNo, tranasactionReferenceData.transactionDate,
                tranasactionReferenceData.isReversed, savingsAccountTransactions, journalEntries, tranasactionReferenceData.reversalOfTransactionId);
    }
    
    public Long getId(){
        return this.transactionId;
    }
}