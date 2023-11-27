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
package org.apache.fineract.organisation.prequalification.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.documentmanagement.api.FileUploadValidator;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.agency.service.AgencyReadPlatformServiceImpl;
import org.apache.fineract.organisation.prequalification.data.MemberPrequalificationData;
import org.apache.fineract.organisation.prequalification.service.PrequalificationReadPlatformService;
import org.apache.fineract.organisation.prequalification.service.PrequalificationWritePlatformService;
import org.apache.fineract.portfolio.group.service.CenterReadPlatformServiceImpl;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.useradministration.service.AppUserReadPlatformService;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/individual/prequalification")
@Component
@Scope("singleton")
@Tag(name = "Prequalification", description = "Prequalify clients that need loans against a blacklist")
public class IndividualPrequalificationApiResource {

    private static final Set<String> PRE_QUALIFICATION_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("id", "productId", "productCode", "year", "typification", "dpi", "nit", "description", "agencyId", "balance",
                    "disbursementAmount", "status", "addedBy", "createdAt"));

    private final String resourceNameForPermissions = "PREQUALIFICATIONS";

    private final PlatformSecurityContext context;
    private final PrequalificationReadPlatformService prequalificationReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final CenterReadPlatformServiceImpl centerReadPlatformService;
    private final DefaultToApiJsonSerializer<MemberPrequalificationData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FileUploadValidator fileUploadValidator;
    private final DocumentWritePlatformService documentWritePlatformService;
    private final PrequalificationWritePlatformService prequalificationWritePlatformService;
    private final AgencyReadPlatformServiceImpl agencyReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final AppUserReadPlatformService appUserReadPlatformService;

    @Autowired
    public IndividualPrequalificationApiResource(final PlatformSecurityContext context,
            final CodeValueReadPlatformService codeValueReadPlatformService, final AgencyReadPlatformServiceImpl agencyReadPlatformService,
            final PrequalificationWritePlatformService prequalificationWritePlatformService,
            final CenterReadPlatformServiceImpl centerReadPlatformService,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final AppUserReadPlatformService appUserReadPlatformService,
            final DefaultToApiJsonSerializer<MemberPrequalificationData> toApiJsonSerializer,
            final PrequalificationReadPlatformService prequalificationReadPlatformService, final FileUploadValidator fileUploadValidator,
            final DocumentWritePlatformService documentWritePlatformService, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.prequalificationReadPlatformService = prequalificationReadPlatformService;
        this.fileUploadValidator = fileUploadValidator;
        this.documentWritePlatformService = documentWritePlatformService;
        this.prequalificationWritePlatformService = prequalificationWritePlatformService;
        this.agencyReadPlatformService = agencyReadPlatformService;
        this.centerReadPlatformService = centerReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.appUserReadPlatformService = appUserReadPlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "List all prequalifications", description = "Example Requests:\n" + "prequalification\n")
    public String retrieveAllBlacklistItems(@Context final UriInfo uriInfo,
            @QueryParam("offset") @Parameter(description = "offset") final Integer offset,
            @QueryParam("limit") @Parameter(description = "limit") final Integer limit,
            @QueryParam("orderBy") @Parameter(description = "orderBy") final String orderBy,
            @QueryParam("status") @Parameter(description = "status") final String status,
            @QueryParam("type") @Parameter(description = "type") final String type,
            @QueryParam("searchText") @Parameter(description = "searchText") final String searchText,
            @QueryParam("sortOrder") @Parameter(description = "sortOrder") final String sortOrder,
            @QueryParam("groupingType") @Parameter(description = "groupingType") final String groupingType) {

        this.context.authenticatedUser().validateHasViewPermission(this.resourceNameForPermissions);

        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        String clientName = queryParameters.getFirst("clientName");
        SearchParameters searchParameters = SearchParameters.forPrequalification(clientName, status, offset, limit, orderBy, sortOrder,
                type, searchText, groupingType);
        final Page<MemberPrequalificationData> memberData = this.prequalificationReadPlatformService.retrieveAllMembers(searchParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(queryParameters);
        return this.toApiJsonSerializer.serialize(settings, memberData, PRE_QUALIFICATION_DATA_PARAMETERS);

    }

    @GET
    @Path("/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Prequalification Details")
    public String getBlacklistDetails(@Context final UriInfo uriInfo,
            @PathParam("clientId") @Parameter(description = "clientId") final Long clientId) {

        this.context.authenticatedUser().validateHasViewPermission(this.resourceNameForPermissions);

        MemberPrequalificationData memberData = this.prequalificationReadPlatformService.retrieveOneMember(clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, memberData, PRE_QUALIFICATION_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createPrequalification(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        try {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().createPrequalification().withJson(apiRequestBodyAsJson)
                    .build();

            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            return this.toApiJsonSerializer.serialize(result);
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @POST
    @Path("/{groupId}/comment")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDocument(@PathParam("groupId") @Parameter(description = "groupId") final Long groupId,
            @HeaderParam("Content-Length") @Parameter(description = "Content-Length") final Long fileSize,
            @FormDataParam("file") final InputStream inputStream, @FormDataParam("file") final FormDataContentDisposition fileDetails,
            @FormDataParam("file") final FormDataBodyPart bodyPart, @FormDataParam("name") final String name,
            @FormDataParam("description") final String description, @FormDataParam("comment") final String comment) {

        if (inputStream != null) {
            fileUploadValidator.validate(fileSize, inputStream, fileDetails, bodyPart);
            final DocumentCommand documentCommand = new DocumentCommand(null, null, "prequalifications", groupId, name,
                    fileDetails.getFileName(), fileSize, bodyPart.getMediaType().toString(), description, null);
            final Long documentId = this.documentWritePlatformService.createDocument(documentCommand, inputStream);
        }

        this.prequalificationWritePlatformService.addCommentsToPrequalification(groupId, comment);
        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(groupId, null));
    }
}