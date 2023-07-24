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
package org.apache.fineract.organisation.prequalification.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.prequalification.domain.PreQualificationGroupRepository;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrequalificationDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final PreQualificationGroupRepository preQualificationGroupRepository;

    @Autowired
    public PrequalificationDataValidator(final FromJsonHelper fromApiJsonHelper,
            final PreQualificationGroupRepository preQualificationGroupRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.preQualificationGroupRepository = preQualificationGroupRepository;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PrequalificationCollectionConstants.NEW_GROUP_PREQUALIFICATION_REQUEST_DATA_PARAMETERS);

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PrequalificatoinApiConstants.PREQUALIFICATION_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long centerId = this.fromApiJsonHelper.extractLongNamed(PrequalificatoinApiConstants.centerIdParamName, element);
        baseDataValidator.reset().parameter(PrequalificatoinApiConstants.centerIdParamName).value(centerId).notNull().longGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(PrequalificatoinApiConstants.groupIdParamName, element)) {
            final Long groupId = this.fromApiJsonHelper.extractLongNamed(PrequalificatoinApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(PrequalificatoinApiConstants.groupIdParamName).value(groupId).notNull()
                    .longGreaterThanZero();
        } else {
            final String groupName = this.fromApiJsonHelper.extractStringNamed(PrequalificatoinApiConstants.groupNameParamName, element);
            baseDataValidator.reset().parameter(PrequalificatoinApiConstants.groupNameParamName).value(groupName).notBlank();
        }

        final JsonArray members = this.fromApiJsonHelper.extractJsonArrayNamed(PrequalificatoinApiConstants.membersParamName, element);
        baseDataValidator.reset().parameter(PrequalificatoinApiConstants.membersParamName).value(members).ignoreIfNull()
                .jsonArrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PrequalificationCollectionConstants.NEW_GROUP_PREQUALIFICATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.amountParamName, element)) {
            final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.amountParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.amountParamName).value(amount).notNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.dueAsOfDateParamName, element)) {
            final LocalDate dueDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.dueAsOfDateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.dueAsOfDateParamName).value(dueDate).notNull();
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

}
