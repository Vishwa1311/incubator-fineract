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
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.globaltransaction.domain.GlobalTransactionReference;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionDTO;
import org.apache.fineract.portfolio.savings.exception.DepositAccountTransactionNotAllowedException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavingsAccountDomainServiceJpa implements SavingsAccountDomainService {

    private final PlatformSecurityContext context;
    private final SavingsAccountRepositoryWrapper savingsAccountRepository;
    private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository;

    @Autowired
    public SavingsAccountDomainServiceJpa(final SavingsAccountRepositoryWrapper savingsAccountRepository,
            final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final ConfigurationDomainService configurationDomainService, final PlatformSecurityContext context,
            final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.configurationDomainService = configurationDomainService;
        this.context = context;
        this.depositAccountOnHoldTransactionRepository = depositAccountOnHoldTransactionRepository;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleWithdrawal(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final SavingsTransactionBooleanValues transactionBooleanValues, final GlobalTransactionReference transactionReference) {

        AppUser user = getAppUserIfPresent();
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

        if (transactionBooleanValues.isRegularTransaction() && !account.allowWithdrawal()) { throw new DepositAccountTransactionNotAllowedException(
                account.getId(), "withdraw", account.depositAccountType()); }
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user);
        final SavingsAccountTransaction withdrawal = account.withdraw(transactionDTO, transactionBooleanValues.isApplyWithdrawFee(), transactionReference);

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, transactionBooleanValues.isInterestTransfer(), isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth, transactionReference);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, transactionBooleanValues.isInterestTransfer(),
                    isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth);
        }
        List<DepositAccountOnHoldTransaction> depositAccountOnHoldTransactions = null;
        if(account.getOnHoldFunds().compareTo(BigDecimal.ZERO) == 1){
            depositAccountOnHoldTransactions = this.depositAccountOnHoldTransactionRepository.findBySavingsAccountAndReversedFalseOrderByCreatedDateAsc(account);
        }
        account.validateAccountBalanceDoesNotBecomeNegative(transactionAmount, transactionBooleanValues.isExceptionForBalanceCheck(), depositAccountOnHoldTransactions);
        saveTransactionToGenerateTransactionId(withdrawal);
        this.savingsAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, transactionBooleanValues.isAccountTransfer());

        return withdrawal;
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction, final GlobalTransactionReference transactionReference) {

        AppUser user = getAppUserIfPresent();
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

        if (isRegularTransaction && !account.allowDeposit()) { throw new DepositAccountTransactionNotAllowedException(account.getId(),
                "deposit", account.depositAccountType()); }

        boolean isInterestTransfer = false;
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user);
        final SavingsAccountTransaction deposit = account.deposit(transactionDTO, transactionReference);

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth, 
                    transactionReference);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth);
        }

        saveTransactionToGenerateTransactionId(deposit);

        this.savingsAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);

        return deposit;
    }

    private Long saveTransactionToGenerateTransactionId(final SavingsAccountTransaction transaction) {
        this.savingsAccountTransactionRepository.save(transaction);
        return transaction.getId();
    }

    private void updateExistingTransactionsDetails(SavingsAccount account, Set<Long> existingTransactionIds,
            Set<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(account.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
    }

    private void postJournalEntries(final SavingsAccount savingsAccount, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds, boolean isAccountTransfer) {

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);

        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }

    @Transactional
    @Override
    public void postJournalEntries(final SavingsAccount account, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final boolean isAccountTransfer = false;
        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
    }

    @Override
    public SavingsAccountTransaction handleDepositReversal(final SavingsAccount account, final GlobalTransactionReference transactionReference,
            final SavingsAccountTransaction transactionToBeReversed) {

        boolean isInterestTransfer = false;
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService
                        .retrieveFinancialYearBeginningMonth();
        
        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);

        final SavingsAccountTransaction depositReversal = account.depositReversal(transactionToBeReversed, transactionReference, transactionDate);

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth, transactionReference);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth);
        }

        saveTransactionToGenerateTransactionId(depositReversal);

        // mark the original transaction as complete reversal
        transactionToBeReversed.markAsCompleteReversal();
        saveTransactionToGenerateTransactionId(transactionToBeReversed);

        this.savingsAccountRepository.save(account);

        // postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return depositReversal;
    }

    @Override
    public SavingsAccountTransaction handleWithdrawalReversal(final SavingsAccount account, final GlobalTransactionReference transactionReference,
            final SavingsAccountTransaction transactionToBeReversed) {
        
        boolean isInterestTransfer = false;
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService
                        .retrieveFinancialYearBeginningMonth();
        
        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);

        final SavingsAccountTransaction withdrawalReversal = account.withdrawalReversal(transactionToBeReversed, transactionReference, transactionDate);

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth, transactionReference);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth);
        }

        saveTransactionToGenerateTransactionId(withdrawalReversal);

        // mark the original transaction as complete reversal
        transactionToBeReversed.markAsCompleteReversal();
        saveTransactionToGenerateTransactionId(transactionToBeReversed);

        this.savingsAccountRepository.save(account);

        // postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return withdrawalReversal;
    }

    @Override
    public SavingsAccountTransaction handleChargeReversal(final SavingsAccount account, final GlobalTransactionReference transactionReference,
            final SavingsAccountTransaction transactionToBeReversed) {
        
        boolean isInterestTransfer = false;
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService
                        .retrieveFinancialYearBeginningMonth();
        
        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);

        final SavingsAccountTransaction chargeReversal = account.chargeReversal(transactionToBeReversed, transactionReference, transactionDate);

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth, transactionReference);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth);
        }

        saveTransactionToGenerateTransactionId(chargeReversal);

        // mark the original transaction as complete reversal
        transactionToBeReversed.markAsCompleteReversal();
        saveTransactionToGenerateTransactionId(transactionToBeReversed);

        this.savingsAccountRepository.save(account);

        // postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return chargeReversal;
    }
}