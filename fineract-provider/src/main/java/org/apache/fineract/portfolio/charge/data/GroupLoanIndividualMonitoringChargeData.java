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
package org.apache.fineract.portfolio.charge.data;

import java.math.BigDecimal;

public class GroupLoanIndividualMonitoringChargeData {
	private final Long id;
	private final Long glim;
	private final Long client;
	private final Long charge;
	private final BigDecimal feeAmount;
	private final BigDecimal revisedFeeAmount;

	public GroupLoanIndividualMonitoringChargeData(final Long id,
			final Long glim, final Long client, final Long charge,
			final BigDecimal feeAmount, final BigDecimal revisedFeeAmount) {
		this.id = id;
		this.glim = glim;
		this.client = client;
		this.charge = charge;
		this.feeAmount = feeAmount;
		this.revisedFeeAmount = revisedFeeAmount;
	}

	public static GroupLoanIndividualMonitoringChargeData instance(
			final Long id, final Long glim, final Long client,
			final Long charge, final BigDecimal feeAmount,
			final BigDecimal revisedFeeAmount) {
		return new GroupLoanIndividualMonitoringChargeData(id, glim, client,
				charge, feeAmount, revisedFeeAmount);
	}

	public Long getId() {
		return this.id;
	}

	public Long getGlim() {
		return this.glim;
	}

	public Long getClient() {
		return this.client;
	}

	public Long getCharge() {
		return this.charge;
	}

	public BigDecimal getFeeAmount() {
		return this.feeAmount;
	}

	public BigDecimal getRevisedFeeAmount() {
		return this.revisedFeeAmount;
	}

}
