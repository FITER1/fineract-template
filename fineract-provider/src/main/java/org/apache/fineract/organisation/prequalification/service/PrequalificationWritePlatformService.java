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
package org.apache.fineract.organisation.prequalification.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;

public interface PrequalificationWritePlatformService {

    CommandProcessingResult processPrequalification(JsonCommand command);

    CommandProcessingResult processUpdatePrequalification(Long groupId, JsonCommand command);

    Long addCommentsToPrequalification(Long blacklistId, String comment);

    CommandProcessingResult updatePrequalificationGroupMember(Long memberId, JsonCommand command);

    void disableExpiredPrequalifications() throws JobExecutionException;

    CommandProcessingResult requestUpdates(Long entityId, JsonCommand command);

    CommandProcessingResult sendForAnalysis(Long entityId, JsonCommand command);

    CommandProcessingResult sendToAgency(Long entityId, JsonCommand command);

    CommandProcessingResult processAnalysisRequest(Long entityId, JsonCommand command);
}