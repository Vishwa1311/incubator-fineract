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
package org.apache.fineract.portfolio.village.api;

import java.util.Collection;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.service.CenterReadPlatformService;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.portfolio.village.service.VillageReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/villages")
@Component
@Scope("singleton")
public class VillageApiResource {

    private final PlatformSecurityContext context;
    private final VillageReadPlatformService villageReadPlatformService;
    private final CenterReadPlatformService centerReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<VillageData> villageDataApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
    private final ToApiJsonSerializer<Object> toApiJsonSerializer;
    
    @Autowired
    public VillageApiResource(PlatformSecurityContext context, VillageReadPlatformService villageReadPlatformService,
            ApiRequestParameterHelper apiRequestParameterHelper, ToApiJsonSerializer<VillageData> villageDataApiJsonSerializer, 
            PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService, ToApiJsonSerializer<Object> toApiJsonSerializer, 
            CenterReadPlatformService centerReadPlatformService) {

        this.context = context;
        this.villageReadPlatformService = villageReadPlatformService;
        this.centerReadPlatformService = centerReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.villageDataApiJsonSerializer = villageDataApiJsonSerializer;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId) {
       
        this.context.authenticatedUser().validateHasReadPermission(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        
        final VillageData villageTemplate = this.villageReadPlatformService.retrieveTemplate(officeId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.villageDataApiJsonSerializer.serialize(settings, villageTemplate, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .createVillage() //
                    .withJson(apiRequestBodyAsJson) //
                    .build();
        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch, @QueryParam("officeId") final Long officeId,
                    @QueryParam("externalId") final String externalId, @QueryParam("name") final String name, @QueryParam("paged") final Boolean paged, 
                    @QueryParam("offset") Integer offset, @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy, 
                    @QueryParam("sortOrder") final String sortOrder)  {
       
        this.context.authenticatedUser().validateHasReadPermission(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final PaginationParameters parameters = PaginationParameters.instance(paged, offset, limit, orderBy, sortOrder);
        final SearchParameters searchParameters = SearchParameters.forVillages(sqlSearch, officeId, externalId, name, offset, limit, orderBy, sortOrder);
        
        if (parameters.isPaged()) {
            final Page<VillageData> villages = this.villageReadPlatformService.retrievePagedAll(searchParameters, parameters);
            
           return this.toApiJsonSerializer.serialize(settings, villages, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);         
        }
        
        final Collection<VillageData> villages = this.villageReadPlatformService.retrieveAll(searchParameters, parameters);
        return this.toApiJsonSerializer.serialize(settings, villages, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);
    }
    
    @GET
    @Path("{villageId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@Context final UriInfo uriInfo, @PathParam("villageId") final Long villageId) {

        this.context.authenticatedUser().validateHasReadPermission(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        
        VillageData village = villageReadPlatformService.retrieveOne(villageId);

        // associations:
        Collection<CenterData> centers = null;
        
        if (!associationParameters.isEmpty()) {
            if (associationParameters.contains("setOfCenters")) {
                centers = this.centerReadPlatformService.retrieveAssociatedCenters(villageId);
            }
        }
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        village = VillageData.withAssociations(village, centers);
       // village.getAssociations(centers);
        
        return this.villageDataApiJsonSerializer.serialize(settings, village, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);
    }
    
    @PUT
    @Path("{villageId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("villageId") final Long villageId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateVillage(villageId) //
                .withJson(apiRequestBodyAsJson) //
                .build();
        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @DELETE
    @Path("{ villageId }")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("villageId") final Long villageId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                        .deleteVillage(villageId) //
                        .build();
        
        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.toApiJsonSerializer.serialize(result);
    }
    
    
    @POST
    @Path("{villageId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String activate(@PathParam("villageId") final Long villageId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson, @Context final UriInfo uriInfo) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        if (is(commandParam, "activate")) {
            final CommandWrapper commandRequest = builder.activateVillage(villageId).build();
            result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "activate" });

    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}