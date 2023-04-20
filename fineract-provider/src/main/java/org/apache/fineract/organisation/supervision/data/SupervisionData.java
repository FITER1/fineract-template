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
package org.apache.fineract.organisation.supervision.data;

import java.util.Collection;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.useradministration.data.AppUserData;

/**
 * Immutable data object for supervision data.
 */
public class SupervisionData {

    private final Long id;

    private final String name;

    private final Long parentId;

    private final String parentName;

    private final Long responsibleUserId;

    // template
    private final Collection<OfficeData> parentOfficesOptions;
    private final Collection<AppUserData> responsibleUserOptions;

    public static SupervisionData instance(Long id, String name, Long parentId, String parentName, Long responsibleUserId) {
        return new SupervisionData(id, name, parentId, parentName, responsibleUserId, null, null);
    }

    public SupervisionData(Long id, String name, Long parentId, String parentName, Long responsibleUserId,
            Collection<OfficeData> parentOfficesOptions, Collection<AppUserData> responsibleUserOptions) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
        this.responsibleUserId = responsibleUserId;
        // template
        this.parentOfficesOptions = parentOfficesOptions;
        this.responsibleUserOptions = responsibleUserOptions;
    }

    public static SupervisionData template(Collection<OfficeData> parentOfficesOptions, Collection<AppUserData> responsibleUserOptions) {
        return new SupervisionData(null, null, null, null, null, parentOfficesOptions, responsibleUserOptions);
    }

}
