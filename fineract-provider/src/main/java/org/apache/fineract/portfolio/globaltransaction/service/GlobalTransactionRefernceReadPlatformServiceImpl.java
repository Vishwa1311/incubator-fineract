package org.apache.fineract.portfolio.globaltransaction.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.globaltransaction.data.GlobalTransactionReferenceData;
import org.apache.fineract.portfolio.globaltransaction.exception.GlobalTransactionReferenceNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GlobalTransactionRefernceReadPlatformServiceImpl implements GlobalTransactionRefernceReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final GlobalTranasactionReferenceMapper tranasactionReferenceMapper;

    @Autowired
    public GlobalTransactionRefernceReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tranasactionReferenceMapper = new GlobalTranasactionReferenceMapper();
    }

    @Override
    public GlobalTransactionReferenceData retrieveOne(final Long transactionId) {

        try {
            // this.context.authenticatedUser();
            final String sql = "select " + this.tranasactionReferenceMapper.schema() + " where gtr.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.tranasactionReferenceMapper, new Object[] { transactionId });
        } catch (final EmptyResultDataAccessException e) {
            throw new GlobalTransactionReferenceNotFoundException(transactionId);
        }
    }

    @Override
    public GlobalTransactionReferenceData retrieveOneByExternalRefNo(final String externalRefNo) {
        try {
            // this.context.authenticatedUser();
            final String sql = "select " + this.tranasactionReferenceMapper.schema() + " where gtr.external_ref_no = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.tranasactionReferenceMapper, new Object[] { externalRefNo });
        } catch (final EmptyResultDataAccessException e) {
            throw new GlobalTransactionReferenceNotFoundException(externalRefNo);
        }
    }
    
    @Override
    public GlobalTransactionReferenceData retrieveOneByTransactionRefNo(final String transactionRefNo) {
        try {
            // this.context.authenticatedUser();
            final String sql = "select " + this.tranasactionReferenceMapper.schema() + " where gtr.transaction_ref_no = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.tranasactionReferenceMapper, new Object[] { transactionRefNo });
        } catch (final EmptyResultDataAccessException e) {
            throw new GlobalTransactionReferenceNotFoundException(transactionRefNo);
        }
    }

    private static final class GlobalTranasactionReferenceMapper implements RowMapper<GlobalTransactionReferenceData> {

        private final String schemaSql;

        public GlobalTranasactionReferenceMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(100);
            sqlBuilder.append("gtr.id as id, gtr.transaction_ref_no as transactionRefNo, gtr.external_ref_no as externalRefNo, "
                    + "gtr.transaction_date as transactionDate, gtr.is_reversed as isReversed, gtr.reversal_of_id as reversalOfId "
                    + "from m_global_transaction_reference gtr ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public GlobalTransactionReferenceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String transactionRefNo = rs.getString("transactionRefNo");
            final String externalRefNo = rs.getString("externalRefNo");
            final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            final boolean isReversed = rs.getBoolean("isReversed");
            final Long reversalOfId = JdbcSupport.getLong(rs, "reversalOfId");

            return GlobalTransactionReferenceData.instance(id, transactionRefNo, externalRefNo, transactionDate, isReversed, reversalOfId);
        }
    }

}