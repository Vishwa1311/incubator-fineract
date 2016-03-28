package org.apache.fineract.infrastructure.sequencegenerator;

/**
 * An enumeration of different Sequence Generator Types
 */
public enum SequenceGeneratorType {
    INVALID(0, "sequenceGeneratorType.invalid"), //
    TRANSACTIONS(1, "sequenceGeneratorType.transactions"), //
    LEADID(2, "sequenceGeneratorType.leadid"), //
    CUSTOMERID(3, "sequenceGeneratorType.customerid"), //
    HOUSEHOLDID(4, "sequenceGeneratorType.householdid"), //
    LOANACCOUNTNO(5, "sequenceGeneratorType.loanaccountno"), //
    APPREFID(6, "sequenceGeneratorType.refid"),//
    INSURANCEPOLICYNO(7, "sequenceGeneratorType.insurancepolicyno");

    private final Integer value;
    private final String code;

    private SequenceGeneratorType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static SequenceGeneratorType fromInt(final Integer transactionType) {

        if (transactionType == null) { return SequenceGeneratorType.INVALID; }

        SequenceGeneratorType generatorType = SequenceGeneratorType.INVALID;
        switch (transactionType) {
            case 1:
                generatorType = SequenceGeneratorType.TRANSACTIONS;
            break;
            case 2:
                generatorType = SequenceGeneratorType.LEADID;
            break;
            case 3:
                generatorType = SequenceGeneratorType.CUSTOMERID;
            break;
            case 4:
                generatorType = SequenceGeneratorType.HOUSEHOLDID;
            break;
            case 5:
                generatorType = SequenceGeneratorType.LOANACCOUNTNO;
            break;
            case 6:
                generatorType = SequenceGeneratorType.APPREFID;
            break;
            case 7:
            	generatorType = SequenceGeneratorType.INSURANCEPOLICYNO;
        }
        return generatorType;
    }

    public boolean isTransactionType() {
        return this.value.equals(SequenceGeneratorType.TRANSACTIONS.getValue());
    }

    public boolean isClientType() {
        return this.value.equals(SequenceGeneratorType.LEADID.getValue());
    }

    public boolean isInvalid() {
        return this.value.equals(SequenceGeneratorType.INVALID.value);
    }
}