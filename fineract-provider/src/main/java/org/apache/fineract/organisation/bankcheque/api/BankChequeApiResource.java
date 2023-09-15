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
package org.apache.fineract.organisation.bankcheque.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.bankcheque.data.BatchData;
import org.apache.fineract.organisation.bankcheque.service.ChequeReadPlatformService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/bankcheques")
@Component
@Scope("singleton")
@Tag(name = "Bank Cheques", description = "Cheques are categorized into batches which are later used to disbursed loans to customers")
@RequiredArgsConstructor
@Slf4j
public class BankChequeApiResource {

    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BatchData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ChequeReadPlatformService chequeReadPlatformService;

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a cheque batch", description = "Mandatory Fields: from cheque, to cheque, bank account no, agency ID"
            + "Optional Fields: No optional fields")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.PostChequeBatchRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.PostChequeBatchResponse.class))) })
    public String createBatch(@Parameter(hidden = true) final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createChequeBatch().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{batchId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a cheque batch", description = "Example Requests: /bankcheques/56")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.GetChequeBatchResponse.class))) })
    public String retrieveOne(@PathParam("batchId") @Parameter(description = "batchId") final Long batchId,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(BankChequeApiConstants.BANK_CHECK_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        BatchData batchData = this.chequeReadPlatformService.retrieveBatch(batchId);
        return this.toApiJsonSerializer.serialize(settings, batchData, BankChequeApiConstants.BATCH_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Cheque Batch Template", description = "This is a convenience resource. It can be useful when building maintenance user interface screens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.GetChequeBatchResponse.class))) })
    public String retrieveTemplate(@Context final UriInfo uriInfo,
            @Parameter(description = "officeId") @QueryParam("officeId") final Long officeId,
            @QueryParam("bankAccId") @Parameter(description = "backAccId") final Long backAccId) {
        this.context.authenticatedUser().validateHasReadPermission(BankChequeApiConstants.BANK_CHECK_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        BatchData batchData = this.chequeReadPlatformService.retrieveTemplate(backAccId);
        return this.toApiJsonSerializer.serialize(settings, batchData, BankChequeApiConstants.BATCH_RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{batchId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update a cheque batch", description = "Mandatory Fields: from cheque, to cheque, bank account no, agency ID"
            + "Optional Fields: No optional fields")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.PostChequeBatchRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.PostChequeBatchResponse.class))) })
    public String updateBatch(@PathParam("batchId") @Parameter(description = "batchId") final Long batchId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateChequeBatch(batchId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{batchId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Delete a cheque batch", description = "Cannot delete batch with available cheques, the delete is a 'hard delete' and cannot be recovered from")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.PostChequeBatchRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BankChequeApiSwagger.PostChequeBatchResponse.class))) })
    public String deleteBatch(@PathParam("batchId") @Parameter(description = "batchId") final Long batchId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteChequeBatch(batchId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}