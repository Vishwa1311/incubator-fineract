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

public enum GlimChargeCalculationType {
	
	INVALID(1, "glimChargeCalculationType.invalid"),
	ROUND(1, "glimChargeCalculationType.round"), 
    ROUND_WITH_MAX_CHARGE(2, "glimChargeCalculationType.roundWithMaxCharge"), 
    ROUND_WITHOUT_MAX_CHARGE(3, "glimChargeCalculationType.roundWithoutMaxCharge");
	
	private final Integer value;
    private final String code;

    private GlimChargeCalculationType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public static GlimChargeCalculationType fromInt(final Integer chargeCalculationType) {
    	GlimChargeCalculationType glimChargeCalculationType = GlimChargeCalculationType.INVALID;
        if (chargeCalculationType != null) {
            switch (chargeCalculationType) {
            	case 0:
            		glimChargeCalculationType = INVALID;
                break;
                case 1:
                	glimChargeCalculationType = ROUND;
                	break;
                case 2:
                	glimChargeCalculationType = ROUND_WITH_MAX_CHARGE; 
                	break;
                default:
                	glimChargeCalculationType = ROUND_WITHOUT_MAX_CHARGE;
                break;
            }
        }
        return glimChargeCalculationType;
    }    

    public static Object[] validValues() {
        return new Object[] { GlimChargeCalculationType.ROUND.getValue(), GlimChargeCalculationType.ROUND_WITH_MAX_CHARGE.getValue(), GlimChargeCalculationType.ROUND_WITHOUT_MAX_CHARGE.getValue()};
    }
}
