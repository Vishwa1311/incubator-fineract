package org.apache.fineract.portfolio.globaltransaction.domain;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TransactionRefNoGenerator {

    private final String sequence;

    public TransactionRefNoGenerator(String sequence) {
        this.sequence = sequence;
    }

    public String generate() {
        final DateTimeFormatter df = DateTimeFormat.forPattern("yyyyMMdd");
        final String date = DateUtils.getLocalDateOfTenant().toString(df);
        final StringBuilder transactionRefNo = new StringBuilder(this.sequence.length() + 8);
        transactionRefNo.append(date);
        transactionRefNo.append(this.sequence);
        return transactionRefNo.toString();
    }
}