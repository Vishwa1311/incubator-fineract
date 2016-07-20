package org.apache.fineract.portfolio.charge.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.charge.data.GroupLoanIndividualMonitoringChargeData;
import org.apache.fineract.portfolio.charge.exception.GroupLoanIndividualMonitoringChargeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringChargeReadPlatformServiceImpl
		implements GroupLoanIndividualMonitoringChargeReadPlatformService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public GroupLoanIndividualMonitoringChargeReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	private static final class GroupLoanIndividualMonitoringChargeMapper
			implements RowMapper<GroupLoanIndividualMonitoringChargeData> {

		private final String sqlQuery = " glimcharge.id as id, name, glimcharge.glim_id as glim,"
				+ " glimcharge.client_id as client, glimcharge.charge_id as charge,"
				+ " glimcharge.fee_amount feeAmount, glimcharge.revised_fee_amount as revisedFeeAmount "
				+ " from m_loan_glim_charges glimcharge ";

		public String schema() {
			return this.sqlQuery;
		}

		@Override
		public GroupLoanIndividualMonitoringChargeData mapRow(
				final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final Long glim = rs.getLong("glim");
			final Long client = rs.getLong("client");
			final Long charge = rs.getLong("charge");
			final BigDecimal feeAmount = JdbcSupport
					.getBigDecimalDefaultToNullIfZero(rs, "feeAmount");
			final BigDecimal revisedFeeAmount = JdbcSupport
					.getBigDecimalDefaultToNullIfZero(rs, "revisedFeeAmount");

			return GroupLoanIndividualMonitoringChargeData.instance(id, glim,
					client, charge, feeAmount, revisedFeeAmount);
		}

	}

	@Override
	public GroupLoanIndividualMonitoringChargeData retrieveGLIMCharge(Long id) {

		try {
			final GroupLoanIndividualMonitoringChargeMapper rm = new GroupLoanIndividualMonitoringChargeMapper();
			String sql = "select " + rm.schema() + " where glimcharge.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm,
					new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			throw new GroupLoanIndividualMonitoringChargeNotFoundException(id);
		}

	}

	@Override
	public Collection<GroupLoanIndividualMonitoringChargeData> retrieveGLIMChargeByGlimId(
			Long glimId) {
		final GroupLoanIndividualMonitoringChargeMapper rm = new GroupLoanIndividualMonitoringChargeMapper();
		String sql = "select " + rm.schema() + " where glimcharge.glim_id = ? ";

		return this.jdbcTemplate.query(sql, rm, new Object[] { glimId });
	}

}
