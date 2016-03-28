package org.apache.fineract.infrastructure.sequencegenerator.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.sequencegenerator.data.RecyclableSequenceData;
import org.apache.fineract.infrastructure.sequencegenerator.exception.RecyclableSequenceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class RecyclableSequenceReadPlatformServiceImpl implements RecyclableSequenceReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final RecyclableSequenceMapper sequenceMapper;

    @Autowired
    public RecyclableSequenceReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sequenceMapper = new RecyclableSequenceMapper();
    }
    
    @Override
    public RecyclableSequenceData getNextSequence(final String sequenceType) {
        try {
            // this.context.authenticatedUser();
            final String sql = "select " + this.sequenceMapper.schema() + " where  smr.name = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.sequenceMapper, new Object[] { sequenceType });
        } catch (final EmptyResultDataAccessException e) {
            throw new RecyclableSequenceNotFoundException(sequenceType);
        }
    }

    private static final class RecyclableSequenceMapper implements RowMapper<RecyclableSequenceData> {

        private final String schemaSql;

        public RecyclableSequenceMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(100);
            sqlBuilder.append("sequence_value as sequenceValue, length(max_seq_num) seqLength, " +
            		"padding_char as paddingChar from sequence_mst_recyclable smr ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public RecyclableSequenceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long sequenceValue = rs.getLong("sequenceValue");
            final int seqLength = rs.getInt("seqLength");
            final String paddingChar = rs.getString("paddingChar");
            return RecyclableSequenceData.instance(sequenceValue, paddingChar, seqLength);
        }
    }

}