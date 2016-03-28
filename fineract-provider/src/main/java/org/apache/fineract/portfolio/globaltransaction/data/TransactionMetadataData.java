package org.apache.fineract.portfolio.globaltransaction.data;


/**
 * Immutable data object represents a partial data of Transaction Meta Data
 * 
 */
public class TransactionMetadataData {

    private final String dataKey;
    private final String dataValue;

    private TransactionMetadataData(String dataKey, String dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    public static TransactionMetadataData instance(String dataKey, String dataValue) {
        return new TransactionMetadataData(dataKey, dataValue);
    }

	/**
	 * @return the dataValue
	 */
	public String getDataValue() {
		return dataValue;
	}

	/**
	 * @return the dataKey
	 */
	public String getDataKey() {
		return dataKey;
	}

}