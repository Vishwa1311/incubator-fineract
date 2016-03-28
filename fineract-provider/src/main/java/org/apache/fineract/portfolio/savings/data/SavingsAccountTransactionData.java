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
package org.apache.fineract.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.joda.time.LocalDate;

/**
 * Immutable data object representing a savings account transaction.
 */
@SuppressWarnings("unused")
public class SavingsAccountTransactionData {

    private final Long id;
    private final SavingsAccountTransactionEnumData transactionType;
    private final Long accountId;
    private final String accountNo;
    private final LocalDate date;
    private final CurrencyData currency;
    private final PaymentDetailData paymentDetailData;
    private final BigDecimal amount;
    private final BigDecimal runningBalance;
    private final boolean reversed;
    private final AccountTransferData transfer;
    private final LocalDate submittedOnDate;
    private final String transactionRefNo;
    private final String externalRefNo;
    private final Long reversalOfTransactionId;
    private final EnumOptionData reversalType;

    // templates
    final Collection<PaymentTypeData> paymentTypeOptions;

    public static SavingsAccountTransactionData create(final Long id, final SavingsAccountTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
            final CurrencyData currency, final BigDecimal amount, final BigDecimal runningBalance, final boolean reversed,
            final AccountTransferData transfer, final String transactionRefNo, final String externalRefNo,
            final Long reversalOfTransactionId, final EnumOptionData reversalType) {
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        return new SavingsAccountTransactionData(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, date, currency,
                amount, runningBalance, reversed, transfer, paymentTypeOptions, transactionRefNo, externalRefNo, reversalOfTransactionId,
                reversalType);
    }

    public static SavingsAccountTransactionData create(final Long id, final SavingsAccountTransactionEnumData transactionType,
                                                       final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
                                                       final CurrencyData currency, final BigDecimal amount, final BigDecimal runningBalance, final boolean reversed,
                                                       final AccountTransferData transfer,final LocalDate submittedOnDate, final String transactionRefNo, final String externalRefNo,
                                                       final Long reversalOfTransactionId, final EnumOptionData reversalType) {
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        return new SavingsAccountTransactionData(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, date, currency,
                amount, runningBalance, reversed, transfer, paymentTypeOptions,submittedOnDate, transactionRefNo, externalRefNo, reversalOfTransactionId, reversalType);
    }

    public static SavingsAccountTransactionData template(final Long savingsId, final String savingsAccountNo,
            final LocalDate defaultLocalDate, final CurrencyData currency) {
        final Long id = null;
        final SavingsAccountTransactionEnumData transactionType = null;
        final BigDecimal amount = null;
        final BigDecimal runningBalance = null;
        final boolean reversed = false;
        final PaymentDetailData paymentDetailData = null;
        final Collection<CodeValueData> paymentTypeOptions = null;
        final String transactionRefNo = null;
        final String externalRefNo = null;
        final Long reversalOfTransactionId = null;
        final EnumOptionData reversalType = null;
        return new SavingsAccountTransactionData(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, defaultLocalDate,
                currency, amount, runningBalance, reversed, null, null, transactionRefNo, externalRefNo, reversalOfTransactionId, reversalType);
    }

    public static SavingsAccountTransactionData templateOnTop(final SavingsAccountTransactionData savingsAccountTransactionData,
            final Collection<PaymentTypeData> paymentTypeOptions) {
        return new SavingsAccountTransactionData(savingsAccountTransactionData.id, savingsAccountTransactionData.transactionType,
                savingsAccountTransactionData.paymentDetailData, savingsAccountTransactionData.accountId,
                savingsAccountTransactionData.accountNo, savingsAccountTransactionData.date, savingsAccountTransactionData.currency,
                savingsAccountTransactionData.amount, savingsAccountTransactionData.runningBalance, savingsAccountTransactionData.reversed,
                savingsAccountTransactionData.transfer, paymentTypeOptions, savingsAccountTransactionData.transactionRefNo,
                savingsAccountTransactionData.externalRefNo, savingsAccountTransactionData.reversalOfTransactionId,
                savingsAccountTransactionData.reversalType);
    }

    private SavingsAccountTransactionData(final Long id, final SavingsAccountTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
            final CurrencyData currency, final BigDecimal amount, final BigDecimal runningBalance, final boolean reversed,
            final AccountTransferData transfer, final Collection<PaymentTypeData> paymentTypeOptions, final String transactionRefNo, final String externalRefNo,
            final Long reversalOfTransactionId, final EnumOptionData reversalType) {

        this(id,transactionType,paymentDetailData,savingsId, savingsAccountNo,date,
        currency,amount,runningBalance, reversed,
        transfer, paymentTypeOptions, null, transactionRefNo,
        externalRefNo, reversalOfTransactionId, reversalType);

    }

    private SavingsAccountTransactionData(final Long id, final SavingsAccountTransactionEnumData transactionType,
                                          final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
                                          final CurrencyData currency, final BigDecimal amount, final BigDecimal runningBalance, final boolean reversed,
                                          final AccountTransferData transfer, final Collection<PaymentTypeData> paymentTypeOptions,final LocalDate submittedOnDate, 
                                          final String transactionRefNo, final String externalRefNo,
                                          final Long reversalOfTransactionId, final EnumOptionData reversalType) {
        this.id = id;
        this.transactionType = transactionType;
        this.paymentDetailData = paymentDetailData;
        this.accountId = savingsId;
        this.accountNo = savingsAccountNo;
        this.date = date;
        this.currency = currency;
        this.amount = amount;
        this.runningBalance = runningBalance;
        this.reversed = reversed;
        this.transfer = transfer;
        this.paymentTypeOptions = paymentTypeOptions;
        this.submittedOnDate = submittedOnDate;
        this.transactionRefNo = transactionRefNo;
        this.externalRefNo = externalRefNo;
        this.reversalOfTransactionId = reversalOfTransactionId;
        this.reversalType = reversalType;
    }

    public static SavingsAccountTransactionData withWithDrawalTransactionDetails(
            final SavingsAccountTransactionData savingsAccountTransactionData) {

        final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        final SavingsAccountTransactionEnumData transactionType = SavingsEnumerations
                .transactionType(SavingsAccountTransactionType.WITHDRAWAL.getValue());

        return new SavingsAccountTransactionData(savingsAccountTransactionData.id, transactionType,
                savingsAccountTransactionData.paymentDetailData, savingsAccountTransactionData.accountId,
                savingsAccountTransactionData.accountNo, currentDate, savingsAccountTransactionData.currency,
                savingsAccountTransactionData.runningBalance, savingsAccountTransactionData.runningBalance,
                savingsAccountTransactionData.reversed, savingsAccountTransactionData.transfer,
                savingsAccountTransactionData.paymentTypeOptions, savingsAccountTransactionData.transactionRefNo,
                savingsAccountTransactionData.externalRefNo, savingsAccountTransactionData.reversalOfTransactionId,
                savingsAccountTransactionData.reversalType);
    }
}