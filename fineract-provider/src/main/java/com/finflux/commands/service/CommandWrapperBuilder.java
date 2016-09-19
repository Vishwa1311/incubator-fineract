package com.finflux.commands.service;

import org.apache.fineract.commands.domain.CommandWrapper;

import com.finflux.reconcilation.ReconciliationApiConstants;

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

    public CommandWrapperBuilder deleteBankStatement(final Long bankStatementId) {
        this.actionName = "DELETE";
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId;
        return this;
    }

    public CommandWrapperBuilder reconcileBankStatementDetails(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.RECONCILE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId + "/details";
        return this;
    }

    public CommandWrapperBuilder undoReconcileBankStatementDetails(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.UNDO_RECONCILE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId + "/details";
        return this;
    }

    public CommandWrapperBuilder reconcileBankStatement(final Long bankStatementId) {
        this.actionName = ReconciliationApiConstants.RECONCILE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_STATEMENT_RESOURCE_NAME;
        this.entityId = bankStatementId;
        this.href = "/bankstatements/" + bankStatementId + "?command=reconcile";
        return this;
    }

    public CommandWrapperBuilder createBank() {
        this.actionName = ReconciliationApiConstants.CREATE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_RESOURCE_NAME;
        this.entityId = null;
        this.href = "/bank";
        return this;
    }

    public CommandWrapperBuilder updateBank(final Long bankId) {
        this.actionName = ReconciliationApiConstants.UPDATE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_RESOURCE_NAME;
        this.entityId = bankId;
        this.href = "/bank/" + bankId;
        return this;
    }

    public CommandWrapperBuilder deleteBank(final Long bankId) {
        this.actionName = ReconciliationApiConstants.DELETE_ACTION;
        this.entityName = ReconciliationApiConstants.BANK_RESOURCE_NAME;
        this.entityId = bankId;
        this.href = "/bank/" + bankId;
        return this;
    }
}