package org.apache.fineract.integrationtests.common.organisation;

import static org.junit.Assert.assertEquals;

import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CampaignsTest {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private CampaignsHelper campaignsHelper;

    private static final String NON_TRIGGERED_REPORT_NAME = "Prospective Clients";
    private static final String TRIGGERED_REPORT_NAME = "Client Activated";

    private static final Integer DIRECT_TRIGGER_TYPE = 1;
    private static final Integer SCHEDULED_TRIGGER_TYPE = 2;
    private static final Integer TRIGGERED_TRIGGER_TYPE = 3;

    private static final String ACTIVATE_COMMAND = "activate";
    private static final String CLOSE_COMMAND = "close";
    private static final String REACTIVATE_COMMAND = "reactivate";

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.campaignsHelper = new CampaignsHelper(this.requestSpec, this.responseSpec);
    }

    @Test
    public void testSupportedActionsForCampaignWithTriggerTypeAsDirect() {
        // creating new campaign
        Integer campaignId = this.campaignsHelper.createCampaign(NON_TRIGGERED_REPORT_NAME, DIRECT_TRIGGER_TYPE);
        this.campaignsHelper.verifyCampaignCreatedOnServer(this.requestSpec, this.responseSpec, campaignId);

        // updating campaign
        Integer updatedCampaignId = this.campaignsHelper.updateCampaign(requestSpec, responseSpec, campaignId, NON_TRIGGERED_REPORT_NAME,
                DIRECT_TRIGGER_TYPE);
        assertEquals(campaignId, updatedCampaignId);

        // activating campaign
        Integer activatedCampaignId = this.campaignsHelper
                .performActionsOnCampaign(requestSpec, responseSpec, campaignId, ACTIVATE_COMMAND);
        assertEquals(activatedCampaignId, campaignId);

        // closing campaign
        Integer closedCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId, CLOSE_COMMAND);
        assertEquals(closedCampaignId, campaignId);

        // reactivating campaign
        Integer reactivateCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId,
                REACTIVATE_COMMAND);
        assertEquals(reactivateCampaignId, campaignId);

        // closing campaign again for deletion
        closedCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId, CLOSE_COMMAND);
        assertEquals(closedCampaignId, campaignId);

        // deleting campaign
        Integer deletedCampaignId = this.campaignsHelper.deleteCampaign(requestSpec, responseSpec, campaignId);
        assertEquals(deletedCampaignId, campaignId);
    }

    @Test
    public void testSupportedActionsForCampaignWithTriggerTypeAsScheduled() {
        // creating new campaign
        Integer campaignId = this.campaignsHelper.createCampaign(NON_TRIGGERED_REPORT_NAME, SCHEDULED_TRIGGER_TYPE);
        this.campaignsHelper.verifyCampaignCreatedOnServer(this.requestSpec, this.responseSpec, campaignId);

        // updating campaign
        Integer updatedCampaignId = this.campaignsHelper.updateCampaign(requestSpec, responseSpec, campaignId, NON_TRIGGERED_REPORT_NAME,
                SCHEDULED_TRIGGER_TYPE);
        assertEquals(campaignId, updatedCampaignId);

        // activating campaign
        Integer activatedCampaignId = this.campaignsHelper
                .performActionsOnCampaign(requestSpec, responseSpec, campaignId, ACTIVATE_COMMAND);
        assertEquals(activatedCampaignId, campaignId);

        // closing campaign
        Integer closedCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId, CLOSE_COMMAND);
        assertEquals(closedCampaignId, campaignId);

        // reactivating campaign
        Integer reactivateCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId,
                REACTIVATE_COMMAND);
        assertEquals(reactivateCampaignId, campaignId);

        // closing campaign again for deletion
        closedCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId, CLOSE_COMMAND);
        assertEquals(closedCampaignId, campaignId);

        // deleting campaign
        Integer deletedCampaignId = this.campaignsHelper.deleteCampaign(requestSpec, responseSpec, campaignId);
        assertEquals(deletedCampaignId, campaignId);
    }

    @Test
    public void testSupportedActionsForCampaignWithTriggerTypeAsTriggered() {
        // creating new campaign
        Integer campaignId = this.campaignsHelper.createCampaign(TRIGGERED_REPORT_NAME, TRIGGERED_TRIGGER_TYPE);
        this.campaignsHelper.verifyCampaignCreatedOnServer(this.requestSpec, this.responseSpec, campaignId);

        // updating campaign
        Integer updatedCampaignId = this.campaignsHelper.updateCampaign(requestSpec, responseSpec, campaignId, TRIGGERED_REPORT_NAME,
                TRIGGERED_TRIGGER_TYPE);
        assertEquals(campaignId, updatedCampaignId);

        // activating campaign
        Integer activatedCampaignId = this.campaignsHelper
                .performActionsOnCampaign(requestSpec, responseSpec, campaignId, ACTIVATE_COMMAND);
        assertEquals(activatedCampaignId, campaignId);

        // closing campaign
        Integer closedCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId, CLOSE_COMMAND);
        assertEquals(closedCampaignId, campaignId);

        // reactivating campaign
        Integer reactivateCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId,
                REACTIVATE_COMMAND);
        assertEquals(reactivateCampaignId, campaignId);

        // closing campaign again for deletion
        closedCampaignId = this.campaignsHelper.performActionsOnCampaign(requestSpec, responseSpec, campaignId, CLOSE_COMMAND);
        assertEquals(closedCampaignId, campaignId);

        // deleting campaign
        Integer deletedCampaignId = this.campaignsHelper.deleteCampaign(requestSpec, responseSpec, campaignId);
        assertEquals(deletedCampaignId, campaignId);
    }
}
