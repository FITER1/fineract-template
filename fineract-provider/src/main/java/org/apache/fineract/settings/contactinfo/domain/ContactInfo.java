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
package org.apache.fineract.settings.contactinfo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableWithUTCDateTimeCustom;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.useradministration.domain.AppUser;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "m_contact_info")
public class ContactInfo extends AbstractPersistableCustom {

    @Column(name = "website", length = 100)
    private String website;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "mobile_no", length = 50)
    private String mobileNo;

    public ContactInfo() {
        // Default constructor logic
    }

    public ContactInfo(String website, String email, String mobileNo) {
        this.website = website;
        this.email = email;
        this.mobileNo = mobileNo;
    }

    public static ContactInfo instance(final String website, final String email, final String mobileNo) {
        return new ContactInfo(website, email, mobileNo);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String emailParamName = "email";
        if (command.isChangeInStringParameterNamed(emailParamName, this.email)) {
            final String newValue = command.stringValueOfParameterNamed(emailParamName);
            actualChanges.put(emailParamName, newValue);
            this.email = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNoParamName = "mobileNo";
        if (command.isChangeInStringParameterNamed(mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
            actualChanges.put(mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String websiteParamName = "website";
        if (command.isChangeInStringParameterNamed(websiteParamName, this.website)) {
            final String newValue = command.stringValueOfParameterNamed(websiteParamName);
            actualChanges.put(websiteParamName, newValue);
            this.website = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

}
