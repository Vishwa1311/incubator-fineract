package org.apache.fineract.portfolio.globaltransaction.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.portfolio.globaltransaction.domain.GlobalTransactionReference;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GlobalTransactionReferenceIdGenerateHelper {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GlobalTransactionReferenceIdGenerateHelper(final RoutingDataSource dataSource) {
        
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public GlobalTransactionReference getGlobalTransactionReference(final LocalDate transactionDate) {
        final String externalRefNo = new RandomPasswordGenerator(16).generateRandom();
        final String transactionRefNo = this.jdbcTemplate.queryForObject("SELECT concat((select CURDATE() + 0 ),(SELECT LPAD((SELECT next_sequence_value('global_transaction_no')),8,'0'))) ",
                String.class);        
        return GlobalTransactionReference.createNew(externalRefNo, transactionDate, transactionRefNo);
    }

}
