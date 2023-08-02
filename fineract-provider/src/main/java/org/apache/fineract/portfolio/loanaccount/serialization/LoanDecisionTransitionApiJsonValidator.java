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
package org.apache.fineract.portfolio.loanaccount.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.LoanApprovalMatrixConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class LoanDecisionTransitionApiJsonValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanDecisionTransitionApiJsonValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

    public void validateApplicationReview(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Set<String> disbursementParameters = new HashSet<>(
                Arrays.asList(LoanApiConstants.loanId, LoanApiConstants.loanReviewOnDateParameterName, LoanApiConstants.noteParameterName,
                        LoanApiConstants.localeParameterName, LoanApiConstants.dateFormatParameterName));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanDecisionEngine");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate loanReviewOnDate = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.loanReviewOnDateParameterName,
                element);
        baseDataValidator.reset().parameter(LoanApiConstants.loanReviewOnDateParameterName).value(loanReviewOnDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed(LoanApiConstants.noteParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.noteParameterName).value(note).notExceedingLengthOf(1000).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateDueDiligence(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList(LoanApiConstants.loanId,
                LoanApiConstants.loanReviewOnDateParameterName, LoanApiConstants.noteParameterName, LoanApiConstants.localeParameterName,
                LoanApiConstants.dateFormatParameterName, LoanApiConstants.dueDiligenceOnDateParameterName,
                LoanApiConstants.surveyNameParameterName, LoanApiConstants.startDateParameterName, LoanApiConstants.endDateParameterName,
                LoanApiConstants.surveyLocationParameterName, LoanApiConstants.programParameterName, LoanApiConstants.countryParameterName,
                LoanApiConstants.cohortParameterName));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanDecisionEngine");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate dueDiligenceOn = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.dueDiligenceOnDateParameterName,
                element);
        baseDataValidator.reset().parameter(LoanApiConstants.loanReviewOnDateParameterName).value(dueDiligenceOn).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed(LoanApiConstants.noteParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.noteParameterName).value(note).notExceedingLengthOf(1000).notNull();

        final String surveyName = this.fromApiJsonHelper.extractStringNamed(LoanApiConstants.surveyNameParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.surveyNameParameterName).value(surveyName).notExceedingLengthOf(200).notNull();

        final LocalDate startDate = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.startDateParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.startDateParameterName).value(startDate).notNull();

        final LocalDate endDate = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.endDateParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.endDateParameterName).value(endDate).notNull();

        final Long surveyLocation = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.surveyLocationParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.surveyLocationParameterName).value(surveyLocation).notNull()
                .integerGreaterThanZero();

        final Long cohort = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.cohortParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.cohortParameterName).value(cohort).notNull().integerGreaterThanZero();

        final Long program = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.programParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.programParameterName).value(program).notNull().integerGreaterThanZero();

        final Long country = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.countryParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.countryParameterName).value(country).notNull().integerGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateCollateralReview(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList(LoanApiConstants.loanId,
                LoanApiConstants.collateralReviewOnDateParameterName, LoanApiConstants.noteParameterName,
                LoanApiConstants.localeParameterName, LoanApiConstants.dateFormatParameterName));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanDecisionEngine");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate collateralReviewOn = this.fromApiJsonHelper
                .extractLocalDateNamed(LoanApiConstants.collateralReviewOnDateParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.collateralReviewOnDateParameterName).value(collateralReviewOn).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed(LoanApiConstants.noteParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.noteParameterName).value(note).notExceedingLengthOf(1000).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateApprovalMatrix(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList(LoanApiConstants.loanId,
                LoanApiConstants.collateralReviewOnDateParameterName, LoanApiConstants.noteParameterName,
                LoanApiConstants.localeParameterName, LoanApiConstants.dateFormatParameterName));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanApprovalMatrix");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String currency = this.fromApiJsonHelper.extractStringNamed(LoanApprovalMatrixConstants.currencyParameterName, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.currencyParameterName).value(currency).notExceedingLengthOf(10)
                .notNull();

        final BigDecimal levelOneUnsecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelOneUnsecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneUnsecuredFirstCycleMaxAmount)
                .value(levelOneUnsecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelOneUnsecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneUnsecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneUnsecuredFirstCycleMinTerm)
                .value(levelOneUnsecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelOneUnsecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneUnsecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneUnsecuredFirstCycleMaxTerm)
                .value(levelOneUnsecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelOneUnsecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelOneUnsecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneUnsecuredSecondCycleMaxAmount)
                .value(levelOneUnsecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelOneUnsecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneUnsecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneUnsecuredSecondCycleMinTerm)
                .value(levelOneUnsecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelOneUnsecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneUnsecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneUnsecuredSecondCycleMaxTerm)
                .value(levelOneUnsecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelOneSecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelOneSecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneSecuredFirstCycleMaxAmount)
                .value(levelOneSecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelOneSecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneSecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneSecuredFirstCycleMinTerm)
                .value(levelOneSecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelOneSecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneSecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneSecuredFirstCycleMaxTerm)
                .value(levelOneSecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelOneSecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelOneSecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneSecuredSecondCycleMaxAmount)
                .value(levelOneSecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelOneSecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneSecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneSecuredSecondCycleMinTerm)
                .value(levelOneSecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelOneSecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelOneSecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelOneSecuredSecondCycleMaxTerm)
                .value(levelOneSecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelTwoUnsecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoUnsecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoUnsecuredFirstCycleMaxAmount)
                .value(levelTwoUnsecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelTwoUnsecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoUnsecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoUnsecuredFirstCycleMinTerm)
                .value(levelTwoUnsecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelTwoUnsecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoUnsecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoUnsecuredFirstCycleMaxTerm)
                .value(levelTwoUnsecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelTwoUnsecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoUnsecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoUnsecuredSecondCycleMaxAmount)
                .value(levelTwoUnsecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelTwoUnsecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoUnsecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoUnsecuredSecondCycleMinTerm)
                .value(levelTwoUnsecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelTwoUnsecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoUnsecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoUnsecuredSecondCycleMaxTerm)
                .value(levelTwoUnsecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelTwoSecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoSecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoSecuredFirstCycleMaxAmount)
                .value(levelTwoSecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelTwoSecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoSecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoSecuredFirstCycleMinTerm)
                .value(levelTwoSecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelTwoSecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoSecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoSecuredFirstCycleMaxTerm)
                .value(levelTwoSecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelTwoSecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoSecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoSecuredSecondCycleMaxAmount)
                .value(levelTwoSecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelTwoSecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoSecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoSecuredSecondCycleMinTerm)
                .value(levelTwoSecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelTwoSecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelTwoSecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelTwoSecuredSecondCycleMaxTerm)
                .value(levelTwoSecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelThreeUnsecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeUnsecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeUnsecuredFirstCycleMaxAmount)
                .value(levelThreeUnsecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelThreeUnsecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeUnsecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeUnsecuredFirstCycleMinTerm)
                .value(levelThreeUnsecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelThreeUnsecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeUnsecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeUnsecuredFirstCycleMaxTerm)
                .value(levelThreeUnsecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelThreeUnsecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeUnsecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeUnsecuredSecondCycleMaxAmount)
                .value(levelThreeUnsecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelThreeUnsecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeUnsecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeUnsecuredSecondCycleMinTerm)
                .value(levelThreeUnsecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelThreeUnsecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeUnsecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeUnsecuredSecondCycleMaxTerm)
                .value(levelThreeUnsecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelThreeSecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeSecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeSecuredFirstCycleMaxAmount)
                .value(levelThreeSecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelThreeSecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeSecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeSecuredFirstCycleMinTerm)
                .value(levelThreeSecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelThreeSecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeSecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeSecuredFirstCycleMaxTerm)
                .value(levelThreeSecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelThreeSecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeSecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeSecuredSecondCycleMaxAmount)
                .value(levelThreeSecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelThreeSecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeSecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeSecuredSecondCycleMinTerm)
                .value(levelThreeSecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelThreeSecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelThreeSecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelThreeSecuredSecondCycleMaxTerm)
                .value(levelThreeSecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFourUnsecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFourUnsecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourUnsecuredFirstCycleMaxAmount)
                .value(levelFourUnsecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFourUnsecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourUnsecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourUnsecuredFirstCycleMinTerm)
                .value(levelFourUnsecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFourUnsecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourUnsecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourUnsecuredFirstCycleMaxTerm)
                .value(levelFourUnsecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFourUnsecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFourUnsecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourUnsecuredSecondCycleMaxAmount)
                .value(levelFourUnsecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFourUnsecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourUnsecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourUnsecuredSecondCycleMinTerm)
                .value(levelFourUnsecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFourUnsecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourUnsecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourUnsecuredSecondCycleMaxTerm)
                .value(levelFourUnsecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFourSecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFourSecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourSecuredFirstCycleMaxAmount)
                .value(levelFourSecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFourSecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourSecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourSecuredFirstCycleMinTerm)
                .value(levelFourSecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFourSecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourSecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourSecuredFirstCycleMaxTerm)
                .value(levelFourSecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFourSecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFourSecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourSecuredSecondCycleMaxAmount)
                .value(levelFourSecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFourSecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourSecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourSecuredSecondCycleMinTerm)
                .value(levelFourSecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFourSecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFourSecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFourSecuredSecondCycleMaxTerm)
                .value(levelFourSecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFiveUnsecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveUnsecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveUnsecuredFirstCycleMaxAmount)
                .value(levelFiveUnsecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFiveUnsecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveUnsecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveUnsecuredFirstCycleMinTerm)
                .value(levelFiveUnsecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFiveUnsecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveUnsecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveUnsecuredFirstCycleMaxTerm)
                .value(levelFiveUnsecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFiveUnsecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveUnsecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveUnsecuredSecondCycleMaxAmount)
                .value(levelFiveUnsecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFiveUnsecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveUnsecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveUnsecuredSecondCycleMinTerm)
                .value(levelFiveUnsecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFiveUnsecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveUnsecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveUnsecuredSecondCycleMaxTerm)
                .value(levelFiveUnsecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFiveSecuredFirstCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveSecuredFirstCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveSecuredFirstCycleMaxAmount)
                .value(levelFiveSecuredFirstCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFiveSecuredFirstCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveSecuredFirstCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveSecuredFirstCycleMinTerm)
                .value(levelFiveSecuredFirstCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFiveSecuredFirstCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveSecuredFirstCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveSecuredFirstCycleMaxTerm)
                .value(levelFiveSecuredFirstCycleMaxTerm).notNull().integerGreaterThanZero();

        final BigDecimal levelFiveSecuredSecondCycleMaxAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveSecuredSecondCycleMaxAmount, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveSecuredSecondCycleMaxAmount)
                .value(levelFiveSecuredSecondCycleMaxAmount).positiveAmount().notNull();

        final Integer levelFiveSecuredSecondCycleMinTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveSecuredSecondCycleMinTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveSecuredSecondCycleMinTerm)
                .value(levelFiveSecuredSecondCycleMinTerm).notNull().integerGreaterThanZero();

        final Integer levelFiveSecuredSecondCycleMaxTerm = this.fromApiJsonHelper
                .extractIntegerWithLocaleNamed(LoanApprovalMatrixConstants.levelFiveSecuredSecondCycleMaxTerm, element);
        baseDataValidator.reset().parameter(LoanApprovalMatrixConstants.levelFiveSecuredSecondCycleMaxTerm)
                .value(levelFiveSecuredSecondCycleMaxTerm).notNull().integerGreaterThanZero();

        // Lower levels amounts should not be greater than upper levels

        validateAmountsUnsecuredFirstCycle(levelOneUnsecuredFirstCycleMaxAmount, levelTwoUnsecuredFirstCycleMaxAmount,
                levelThreeUnsecuredFirstCycleMaxAmount, levelFourUnsecuredFirstCycleMaxAmount, levelFiveUnsecuredFirstCycleMaxAmount);

        validateAmountsUnsecuredSecondCycle(levelOneUnsecuredSecondCycleMaxAmount, levelTwoUnsecuredSecondCycleMaxAmount,
                levelThreeUnsecuredSecondCycleMaxAmount, levelFourUnsecuredSecondCycleMaxAmount, levelFiveUnsecuredSecondCycleMaxAmount);

        validateAmountsSecuredFirstCycle(levelOneSecuredFirstCycleMaxAmount, levelTwoSecuredFirstCycleMaxAmount,
                levelThreeSecuredFirstCycleMaxAmount, levelFourSecuredFirstCycleMaxAmount, levelFiveSecuredFirstCycleMaxAmount);

        validateAmountsSecuredSecondCycle(levelOneSecuredSecondCycleMaxAmount, levelTwoSecuredSecondCycleMaxAmount,
                levelThreeSecuredSecondCycleMaxAmount, levelFourSecuredSecondCycleMaxAmount, levelFiveSecuredSecondCycleMaxAmount);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private static void validateAmountsUnsecuredFirstCycle(BigDecimal levelOneUnsecuredFirstCycleMaxAmount,
            BigDecimal levelTwoUnsecuredFirstCycleMaxAmount, BigDecimal levelThreeUnsecuredFirstCycleMaxAmount,
            BigDecimal levelFourUnsecuredFirstCycleMaxAmount, BigDecimal levelFiveUnsecuredFirstCycleMaxAmount) {
        if (levelOneUnsecuredFirstCycleMaxAmount.compareTo(levelTwoUnsecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.one.should.not.be.greater.than.level.two.max.amount",
                    String.format("Loan maximum amount for level one [%s] should not be greater than for level two [%s] ",
                            levelOneUnsecuredFirstCycleMaxAmount, levelTwoUnsecuredFirstCycleMaxAmount));

        }
        if (levelTwoUnsecuredFirstCycleMaxAmount.compareTo(levelThreeUnsecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.two.should.not.be.greater.than.level.three.max.amount",
                    String.format("Loan maximum amount for level two [%s] should not be greater than for level three [%s] ",
                            levelTwoUnsecuredFirstCycleMaxAmount, levelThreeUnsecuredFirstCycleMaxAmount));

        }

        if (levelThreeUnsecuredFirstCycleMaxAmount.compareTo(levelFourUnsecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.three.should.not.be.greater.than.level.four.max.amount",
                    String.format("Loan maximum amount for level three [%s] should not be greater than for level four [%s] ",
                            levelThreeUnsecuredFirstCycleMaxAmount, levelFourUnsecuredFirstCycleMaxAmount));

        }
        if (levelFourUnsecuredFirstCycleMaxAmount.compareTo(levelFiveUnsecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.four.should.not.be.greater.than.level.five.max.amount",
                    String.format("Loan maximum amount for level four [%s] should not be greater than for level five [%s] ",
                            levelFourUnsecuredFirstCycleMaxAmount, levelFiveUnsecuredFirstCycleMaxAmount));

        }
    }

    private static void validateAmountsUnsecuredSecondCycle(BigDecimal levelOneUnsecuredSecondCycleMaxAmount,
            BigDecimal levelTwoUnsecuredSecondCycleMaxAmount, BigDecimal levelThreeUnsecuredSecondCycleMaxAmount,
            BigDecimal levelFourUnsecuredSecondCycleMaxAmount, BigDecimal levelFiveUnsecuredSecondCycleMaxAmount) {
        if (levelOneUnsecuredSecondCycleMaxAmount.compareTo(levelTwoUnsecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.one.should.not.be.greater.than.level.two.max.amount",
                    String.format("Loan maximum amount for level one [%s] should not be greater than for level two [%s] ",
                            levelOneUnsecuredSecondCycleMaxAmount, levelTwoUnsecuredSecondCycleMaxAmount));

        }
        if (levelTwoUnsecuredSecondCycleMaxAmount.compareTo(levelThreeUnsecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.two.should.not.be.greater.than.level.three.max.amount",
                    String.format("Loan maximum amount for level two [%s] should not be greater than for level three [%s] ",
                            levelTwoUnsecuredSecondCycleMaxAmount, levelThreeUnsecuredSecondCycleMaxAmount));

        }

        if (levelThreeUnsecuredSecondCycleMaxAmount.compareTo(levelFourUnsecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.three.should.not.be.greater.than.level.four.max.amount",
                    String.format("Loan maximum amount for level three [%s] should not be greater than for level four [%s] ",
                            levelThreeUnsecuredSecondCycleMaxAmount, levelFourUnsecuredSecondCycleMaxAmount));

        }
        if (levelFourUnsecuredSecondCycleMaxAmount.compareTo(levelFiveUnsecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.four.should.not.be.greater.than.level.five.max.amount",
                    String.format("Loan maximum amount for level four [%s] should not be greater than for level five [%s] ",
                            levelFourUnsecuredSecondCycleMaxAmount, levelFiveUnsecuredSecondCycleMaxAmount));

        }
    }

    private static void validateAmountsSecuredFirstCycle(BigDecimal levelOneSecuredFirstCycleMaxAmount,
            BigDecimal levelTwoSecuredFirstCycleMaxAmount, BigDecimal levelThreeSecuredFirstCycleMaxAmount,
            BigDecimal levelFourSecuredFirstCycleMaxAmount, BigDecimal levelFiveSecuredFirstCycleMaxAmount) {
        if (levelOneSecuredFirstCycleMaxAmount.compareTo(levelTwoSecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.one.should.not.be.greater.than.level.two.max.amount",
                    String.format("Loan maximum amount for level one [%s] should not be greater than for level two [%s] ",
                            levelOneSecuredFirstCycleMaxAmount, levelTwoSecuredFirstCycleMaxAmount));

        }
        if (levelTwoSecuredFirstCycleMaxAmount.compareTo(levelThreeSecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.two.should.not.be.greater.than.level.three.max.amount",
                    String.format("Loan maximum amount for level two [%s] should not be greater than for level three [%s] ",
                            levelTwoSecuredFirstCycleMaxAmount, levelThreeSecuredFirstCycleMaxAmount));

        }

        if (levelThreeSecuredFirstCycleMaxAmount.compareTo(levelFourSecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.three.should.not.be.greater.than.level.four.max.amount",
                    String.format("Loan maximum amount for level three [%s] should not be greater than for level four [%s] ",
                            levelThreeSecuredFirstCycleMaxAmount, levelFourSecuredFirstCycleMaxAmount));

        }
        if (levelFourSecuredFirstCycleMaxAmount.compareTo(levelFiveSecuredFirstCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.four.should.not.be.greater.than.level.five.max.amount",
                    String.format("Loan maximum amount for level four [%s] should not be greater than for level five [%s] ",
                            levelFourSecuredFirstCycleMaxAmount, levelFiveSecuredFirstCycleMaxAmount));

        }
    }

    private static void validateAmountsSecuredSecondCycle(BigDecimal levelOneSecuredSecondCycleMaxAmount,
            BigDecimal levelTwoSecuredSecondCycleMaxAmount, BigDecimal levelThreeSecuredSecondCycleMaxAmount,
            BigDecimal levelFourSecuredSecondCycleMaxAmount, BigDecimal levelFiveSecuredSecondCycleMaxAmount) {
        if (levelOneSecuredSecondCycleMaxAmount.compareTo(levelTwoSecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.one.should.not.be.greater.than.level.two.max.amount",
                    String.format("Loan maximum amount for level one [%s] should not be greater than for level two [%s] ",
                            levelOneSecuredSecondCycleMaxAmount, levelTwoSecuredSecondCycleMaxAmount));

        }
        if (levelTwoSecuredSecondCycleMaxAmount.compareTo(levelThreeSecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.two.should.not.be.greater.than.level.three.max.amount",
                    String.format("Loan maximum amount for level two [%s] should not be greater than for level three [%s] ",
                            levelTwoSecuredSecondCycleMaxAmount, levelThreeSecuredSecondCycleMaxAmount));

        }

        if (levelThreeSecuredSecondCycleMaxAmount.compareTo(levelFourSecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.three.should.not.be.greater.than.level.four.max.amount",
                    String.format("Loan maximum amount for level three [%s] should not be greater than for level four [%s] ",
                            levelThreeSecuredSecondCycleMaxAmount, levelFourSecuredSecondCycleMaxAmount));

        }
        if (levelFourSecuredSecondCycleMaxAmount.compareTo(levelFiveSecuredSecondCycleMaxAmount) > 0) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.max.amount.for.level.four.should.not.be.greater.than.level.five.max.amount",
                    String.format("Loan maximum amount for level four [%s] should not be greater than for level five [%s] ",
                            levelFourSecuredSecondCycleMaxAmount, levelFiveSecuredSecondCycleMaxAmount));

        }
    }
}
