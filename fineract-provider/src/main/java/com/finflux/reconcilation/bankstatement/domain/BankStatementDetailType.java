package com.finflux.reconcilation.bankstatement.domain;

public enum BankStatementDetailType {
	
	INVALID(0, "bankStatementDetailsType.invalid"),
	PORTFOLIO(1, "bankStatementDetailsType.portfolio"), //
    NONPORTFOLIO(2, "bankStatementDetailsType.nonportfolio"), //
    MISCELLANEOUS(3, "bankStatementDetailsType.miscellaneous"); //
	

    private final Integer value;
    private final String code;

    private BankStatementDetailType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static BankStatementDetailType fromInt(final Integer value) {

    	BankStatementDetailType enumeration = BankStatementDetailType.INVALID;
        switch (value) {
            case 1:
                enumeration = BankStatementDetailType.PORTFOLIO;
            break;
            case 2:
                enumeration = BankStatementDetailType.NONPORTFOLIO;
            break;
            case 3:
                enumeration = BankStatementDetailType.MISCELLANEOUS;
            break;
        }
        return enumeration;
    }


}