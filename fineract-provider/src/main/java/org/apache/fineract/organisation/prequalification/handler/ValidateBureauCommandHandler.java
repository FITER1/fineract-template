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
package org.apache.fineract.organisation.prequalification.handler;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.DataIntegrityErrorHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.organisation.prequalification.service.BureauValidationWritePlatformService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CommandType(entity = "BUREAU", action = "VALIDATE")
public class ValidateBureauCommandHandler implements NewCommandSourceHandler {

    private final DataIntegrityErrorHandler dataIntegrityErrorHandler;
    private final BureauValidationWritePlatformService bureauValidationWritePlatformService;

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        try {
            return this.bureauValidationWritePlatformService.validatePrequalificationWithBureau(command.entityId(), command);
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            dataIntegrityErrorHandler.handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve,
                    "prequalification.bureau.check.failed", "Bureau validation failed");
            return CommandProcessingResult.empty();
        }
    }
}