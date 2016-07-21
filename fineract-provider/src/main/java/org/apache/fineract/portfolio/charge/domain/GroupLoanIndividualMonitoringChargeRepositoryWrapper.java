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
package org.apache.fineract.portfolio.charge.domain;

import java.util.Collection;

import org.apache.fineract.portfolio.charge.exception.GroupLoanIndividualMonitoringChargeNotFoundException;
import org.apache.fineract.portfolio.collaterals.domain.Collaterals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringChargeRepositoryWrapper {
	private final GroupLoanIndividualMonitoringChargeRepository groupLoanIndividualMonitoringChargeRepository;

	@Autowired
	public GroupLoanIndividualMonitoringChargeRepositoryWrapper(
			GroupLoanIndividualMonitoringChargeRepository groupLoanIndividualMonitoringChargeRepository) {
		this.groupLoanIndividualMonitoringChargeRepository = groupLoanIndividualMonitoringChargeRepository;
	}
	public void save(final GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge) {
        this.groupLoanIndividualMonitoringChargeRepository.save(groupLoanIndividualMonitoringCharge);
    }

    public void save(final Collection<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharges) {
        this.groupLoanIndividualMonitoringChargeRepository.save(groupLoanIndividualMonitoringCharges);
    }

    public void delete(final GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge) {
        this.groupLoanIndividualMonitoringChargeRepository.delete(groupLoanIndividualMonitoringCharge);
    }

	public GroupLoanIndividualMonitoringCharge findOneWithNotFoundDetection(
			final Long id) {
		final GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge = this.groupLoanIndividualMonitoringChargeRepository
				.findOne(id);
		if (groupLoanIndividualMonitoringCharge == null) {
			throw new GroupLoanIndividualMonitoringChargeNotFoundException(id);
		}
		return groupLoanIndividualMonitoringCharge;
	}
}
