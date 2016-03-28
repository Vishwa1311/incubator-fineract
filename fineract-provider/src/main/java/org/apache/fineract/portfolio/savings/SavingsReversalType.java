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
package org.apache.fineract.portfolio.savings;

import java.util.HashMap;
import java.util.Map;

public enum SavingsReversalType {

    NOT_REVERSED(0, "savingsReversalType.notreversed"), //
    COMPLETE(1, "savingsReversalType.complete"), //
    PARTIAL(2, "savingsReversalType.partial");

    private final Integer value;
    private final String code;

    private SavingsReversalType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    private static final Map<Integer, SavingsReversalType> intToEnumMap = new HashMap<>();
    static {
        for (final SavingsReversalType type : SavingsReversalType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static SavingsReversalType fromInt(final Integer transactionType) {

        if (transactionType == null) { return SavingsReversalType.NOT_REVERSED; }

        SavingsReversalType savingsReversalType = intToEnumMap.get(transactionType);

        if (savingsReversalType == null) {
            savingsReversalType = SavingsReversalType.NOT_REVERSED;
        }

        return savingsReversalType;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public boolean isCompleteReversal(){
        return this.value.equals(SavingsReversalType.COMPLETE.getValue());
    }

}
