package org.apache.fineract.portfolio.globaltransaction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlobalTransactionReferenceApiConstants {

    public static final String GLOBAL_TRANSACTION_REFERENCE_RESOURCE_NAME = "globaltransactionreference";

    // command
    public static String COMMAND_UNDO_TRANSACTION = "undo";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    
    public static final String transactionDateParamName = "transactionDate";
    public static final String externalRefNoParamName = "externalRefNo";
    public static final String transactionIdParamName = "transactionId";
    public static final String transactionRefNoParamName = "transactionRefNo";
    public static final String isReversed = "isReversed";
    public static final String metaDataParamName = "metaData";
    public static final String isProcessedParamName = "isProcessed";
    
    //associations
    public static final String savingsAccountTransactions = "savingsAccountTransactions";
    public static final String journalEntries = "journalEntries";
    

    public static final Set<String> GLOBAL_TRANSACTION_REFERENCE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            localeParamName, dateFormatParamName, externalRefNoParamName, transactionRefNoParamName, transactionDateParamName,
            metaDataParamName, isProcessedParamName));
    
    public static final Set<String> GLOBAL_TRANSACTION_REFERENCE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            transactionIdParamName, transactionRefNoParamName, transactionDateParamName, externalRefNoParamName, isReversed));
}