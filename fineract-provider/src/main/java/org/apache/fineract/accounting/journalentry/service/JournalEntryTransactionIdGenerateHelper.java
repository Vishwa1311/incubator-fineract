package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JournalEntryTransactionIdGenerateHelper {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JournalEntryTransactionIdGenerateHelper(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getTransactionId() {
        return this.jdbcTemplate.queryForObject("SELECT CONCAT('J', (SELECT next_sequence_value('journal_entry_sequence_no')))",
                String.class);
    }
}
