package com.finflux.commands.service;

import org.apache.fineract.commands.domain.CommandWrapper;

import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;

public class CommandWrapperBuilder {

    private Long officeId;
    private Long groupId;
    private Long clientId;
    private Long loanId;
    private Long savingsId;
    private String actionName;
    private String entityName;
    private Long entityId;
    private Long subentityId;
    private String href;
    private String json = "{}";
    private String transactionId;
    private Long productId;
    private Long templateId;

    public CommandWrapper build() {
        return new CommandWrapper(this.officeId, this.groupId, this.clientId, this.loanId, this.savingsId, this.actionName,
                this.entityName, this.entityId, this.subentityId, this.href, this.json, this.transactionId, this.productId, this.templateId);
    }

    public CommandWrapperBuilder withLoanId(final Long withLoanId) {
        this.loanId = withLoanId;
        return this;
    }

    public CommandWrapperBuilder withSavingsId(final Long withSavingsId) {
        this.savingsId = withSavingsId;
        return this;
    }

    public CommandWrapperBuilder withClientId(final Long withClientId) {
        this.clientId = withClientId;
        return this;
    }

    public CommandWrapperBuilder withGroupId(final Long withGroupId) {
        this.groupId = withGroupId;
        return this;
    }

    public CommandWrapperBuilder withEntityName(final String withEntityName) {
        this.entityName = withEntityName;
        return this;
    }

    public CommandWrapperBuilder withSubEntityId(final Long withSubEntityId) {
        this.subentityId = withSubEntityId;
        return this;
    }

    public CommandWrapperBuilder withJson(final String withJson) {
        this.json = withJson;
        return this;
    }

    public CommandWrapperBuilder withNoJsonBody() {
        this.json = null;
        return this;
    }


    public CommandWrapperBuilder updateSecondaryAuthenticationService(final Long authenticationServiceId) {
		this.actionName = "UPDATE";
		this.entityName = "AUTHENTICATIONSERVICE";
		this.entityId = authenticationServiceId;
		this.href = "/external/authentications/services/" + authenticationServiceId;
		return this;
	}
    
	public CommandWrapperBuilder createTransactionAuthenticationService() {
		this.actionName = TransactionAuthenticationApiConstants.CREATE_ACTION;
		this.entityName = TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_SERVICE;
		this.entityId = null;
		this.href = "/transaction/authentications";
		return this;
	}
    
	public CommandWrapperBuilder updateTransactionAuthenticationService(final Long transactionAuthenticationServiceId) {
		this.actionName = TransactionAuthenticationApiConstants.UPDATE_ACTION;
		this.entityName = TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_SERVICE;
		this.entityId = transactionAuthenticationServiceId;
		this.href = "/transaction/authentications/" + transactionAuthenticationServiceId;
		return this;
	}
	
	public CommandWrapperBuilder deleteTransactionAuthenticationService(final Long transactionAuthenticationServiceId) {
		this.actionName = TransactionAuthenticationApiConstants.DELETE_ACTION;
		this.entityName = TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_SERVICE;
		this.entityId = transactionAuthenticationServiceId;
		this.href = "/transaction/authentications/" + transactionAuthenticationServiceId;
		return this;
	}
	
}