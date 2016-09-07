/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.organisation.monetary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CurrencyReadPlatformServiceImpl implements CurrencyReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyMapper currencyRowMapper;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public CurrencyReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
    		final ConfigurationDomainService configurationDomainService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currencyRowMapper = new CurrencyMapper();
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public Collection<CurrencyData> retrieveAllowedCurrencies() {

        this.context.authenticatedUser();

        final String sql = "select " + this.currencyRowMapper.schema() + " from m_organisation_currency c order by c.name";

        return this.jdbcTemplate.query(sql, this.currencyRowMapper, new Object[] {});
    }
    
    @Override
    public Collection<CurrencyData> retrieveAllPlatformCurrencies() {

        final String sql = "select " + this.currencyRowMapper.schema() + " from m_currency c order by c.name";

        return this.jdbcTemplate.query(sql, this.currencyRowMapper, new Object[] {});
    }

    @Override
    public CurrencyData retrieveCurrency(final String code) {

        final String sql = "select " + this.currencyRowMapper.schema() + " from m_currency c  where c.code = ? order by c.name";

        return this.jdbcTemplate.queryForObject(sql, this.currencyRowMapper, new Object[] { code });
    }
    
	@Override
	public String getDefaultCurrencyCode() {
		String currencyCode = null;
		if (configurationDomainService.isDefaultCurrencyEnabled()) {
			currencyCode = configurationDomainService.retreiveDefaultCurrency();
		} else {
			CurrencyData currencyData = this.retrieveFirstCurrency();
			currencyCode = currencyData.code();
		}
		return currencyCode;
	}

	public CurrencyData retrieveFirstCurrency() {
		this.context.authenticatedUser();

		final String sql = "select " + this.currencyRowMapper.schema()
				+ " from m_organisation_currency c order by c.name limit 1";

		return this.jdbcTemplate.queryForObject(sql, this.currencyRowMapper, new Object[] {});
	}
    

    private static final class CurrencyMapper implements RowMapper<CurrencyData> {

        @Override
        public CurrencyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String code = rs.getString("code");
            final String name = rs.getString("name");
            final int decimalPlaces = JdbcSupport.getInteger(rs, "decimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final String displaySymbol = rs.getString("displaySymbol");
            final String nameCode = rs.getString("nameCode");

            return new CurrencyData(code, name, decimalPlaces, inMultiplesOf, displaySymbol, nameCode);
        }

        public String schema() {
            return " c.code as code, c.name as name, c.decimal_places as decimalPlaces,c.currency_multiplesof as inMultiplesOf, c.display_symbol as displaySymbol, c.internationalized_name_code as nameCode ";
        }
    }
}