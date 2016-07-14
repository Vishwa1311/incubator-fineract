package com.finflux.loanapplicationreference.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoanApplicationReferenceApiConstants {

    public static final String LOANAPPLICATIONREFERENCE_RESOURCE_NAME = "loanapplicationreference";

    public static final String externalIdOneParamName = "externalIdOne";

    public static final String externalIdTwoParamName = "externalIdTwo";

    public static final String clientIdParamName = "clientId";

    public static final String loanOfficerIdParamName = "loanOfficerId";

    public static final String groupIdParamName = "groupId";

    public static final String accountTypeParamName = "accountType";

    public static final String loanProductIdParamName = "loanProductId";

    public static final String loanPurposeIdParamName = "loanPurposeId";

    public static final String loanAmountRequestedParamName = "loanAmountRequested";

    public static final String numberOfRepaymentsParamName = "numberOfRepayments";

    public static final String loanAmountApprovedParamName = "loanAmountApproved";

    public static final String expectedDisbursementDateParaName = "expectedDisbursementDate";

    public static final String repaymentsStartingFromDateParaName = "repaymentsStartingFromDate";

    public static final String approvedOnDateParaName = "approvedOnDate";

    public static final String repaymentPeriodFrequencyEnumParamName = "repaymentPeriodFrequencyEnum";

    public static final String repayEveryParamName = "repayEvery";

    public static final String termPeriodFrequencyEnumParamName = "termPeriodFrequencyEnum";

    public static final String termFrequencyParamName = "termFrequency";

    public static final String fixedEmiAmountParamName = "fixedEmiAmount";

    public static final String maxOutstandingLoanBalanceParamName = "maxOutstandingLoanBalance";

    public static final String noOfTrancheParamName = "noOfTranche";

    public static final String submittedOnDateParamName = "submittedOnDate";

    public static final String loanApplicationSanctionTrancheDatasParamName = "loanApplicationSanctionTrancheDatas";

    public static final String trancheAmountParamName = "trancheAmount";

    public static final String expectedTrancheDisbursementDateParaName = "expectedTrancheDisbursementDate";

    public static final String chargesParamName = "charges";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    public static final Set<String> CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(externalIdOneParamName,
            externalIdTwoParamName, clientIdParamName, loanOfficerIdParamName, groupIdParamName, accountTypeParamName,
            loanProductIdParamName, loanPurposeIdParamName, loanAmountRequestedParamName, numberOfRepaymentsParamName,
            repaymentPeriodFrequencyEnumParamName, repayEveryParamName, termPeriodFrequencyEnumParamName, termFrequencyParamName,
            fixedEmiAmountParamName, maxOutstandingLoanBalanceParamName, noOfTrancheParamName, submittedOnDateParamName, chargesParamName,
            localeParamName, dateFormatParamName));

    public static final Set<String> UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(externalIdOneParamName,
            externalIdTwoParamName, clientIdParamName, loanOfficerIdParamName, groupIdParamName, accountTypeParamName,
            loanProductIdParamName, loanPurposeIdParamName, loanAmountRequestedParamName, numberOfRepaymentsParamName,
            repaymentPeriodFrequencyEnumParamName, repayEveryParamName, termPeriodFrequencyEnumParamName, termFrequencyParamName,
            fixedEmiAmountParamName, maxOutstandingLoanBalanceParamName, noOfTrancheParamName, submittedOnDateParamName, chargesParamName,
            localeParamName, dateFormatParamName));

    public static final Set<String> APPROVE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(loanAmountApprovedParamName,
            approvedOnDateParaName, expectedDisbursementDateParaName, numberOfRepaymentsParamName, repaymentPeriodFrequencyEnumParamName,
            repayEveryParamName, termPeriodFrequencyEnumParamName, termFrequencyParamName, fixedEmiAmountParamName,
            maxOutstandingLoanBalanceParamName, loanApplicationSanctionTrancheDatasParamName, repaymentsStartingFromDateParaName,
            localeParamName, dateFormatParamName));

}