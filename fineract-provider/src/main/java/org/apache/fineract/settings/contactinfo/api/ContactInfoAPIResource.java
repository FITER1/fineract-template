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
package org.apache.fineract.settings.contactinfo.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.settings.contactinfo.data.ContactInfoData;
import org.apache.fineract.settings.contactinfo.service.ContactInfoReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/contactinfo")
@Component
@RequiredArgsConstructor
public class ContactInfoAPIResource {

    private static final Set<String> CONTACT_INFO_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "email", "mobileNo", "website"));

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<ContactInfoData> toApiJsonSerializer;
    private final ContactInfoReadPlatformService readPlatformService;
    private final PlatformSecurityContext context;

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        this.context.authenticatedUser().validateHasReadPermission("CONTACTINFO");

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createContactInfo() //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveContactInfo(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission("CONTACTINFO");
        final Collection<ContactInfoData> contactInfo = this.readPlatformService.retrieveContactInfo();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, contactInfo, CONTACT_INFO_DATA_PARAMETERS);
    }

    @PUT
    @Path("{contactInfoId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update Contact Info", description = "Updates a Contact Info")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ContactInfoData.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ContactInfoData.class))) })
    public String updateContactInfo(@PathParam("contactInfoId") @Parameter(description = "contactInfoId") final Long contactInfoId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {
        this.context.authenticatedUser().validateHasReadPermission("CONTACTINFO");

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateContactInfo(contactInfoId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Delete Contact Info", description = "Deletes a Contact Info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ContactInfoData.class))) })
    public String deleteContactInfo() {
        this.context.authenticatedUser().validateHasReadPermission("CONTACTINFO");

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteContactInfo().build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
