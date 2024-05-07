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
package org.apache.fineract.settings.contactinfo.service;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.settings.contactinfo.data.ContactInfoDataValidator;
import org.apache.fineract.settings.contactinfo.domain.ContactInfo;
import org.apache.fineract.settings.contactinfo.domain.ContactInfoRepository;
import org.apache.fineract.settings.contactinfo.exception.ContactInfoNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Slf4j
public class ContactInfoWritePlatformServiceJpaRepositoryImpl implements ContactInfoWritePlatformService {

    private final ContactInfoRepository contactInfoRepository;
    private final ContactInfoDataValidator fromApiJsonDeserializer;

    @Transactional
    @Override
    public CommandProcessingResult createContactInfo(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForCreate(command.json());

        final String mobileNo = command.stringValueOfParameterNamed(ContactInfoDataValidator.mobileNoParamName);
        final String emailAddress = command.stringValueOfParameterNamed(ContactInfoDataValidator.emailParamName);
        final String website = command.stringValueOfParameterNamed(ContactInfoDataValidator.websiteParamName);

        final ContactInfo newContactInfo = ContactInfo.instance(website, emailAddress, mobileNo);

        this.contactInfoRepository.saveAndFlush(newContactInfo);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withClientId(newContactInfo.getId()) //
                .build();
    }

    @Override
    public CommandProcessingResult updateContactInfo(JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdate(command.json());

        final Long contactInfoId = command.entityId();
        final ContactInfo contactInfoForUpdate = this.contactInfoRepository.findById(contactInfoId)
                .orElseThrow(() -> new ContactInfoNotFoundException(contactInfoId));
        final Map<String, Object> changes = contactInfoForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.contactInfoRepository.saveAndFlush(contactInfoForUpdate);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(contactInfoId).with(changes).build();
    }

    @Override
    public CommandProcessingResult deleteContactInfo() {

        this.contactInfoRepository.deleteAll();
        this.contactInfoRepository.flush();
        return new CommandProcessingResultBuilder().build();
    }

}
