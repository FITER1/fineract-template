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
package org.apache.fineract.organisation.agency.data;

/**
 * Immutable data object represent agency entity code enumerations.
 */
public class AgencyEntityCodeEnumData {

    private final Long id;
    private final String code;
    private final String value;

    private final boolean friendshipBridge;

    public AgencyEntityCodeEnumData(final Long id, final String code, final String value) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.friendshipBridge = Long.valueOf(1).equals(this.id);

    }

    public Long id() {
        return this.id;
    }

    public String code() {
        return this.code;
    }

    public String value() {
        return this.value;
    }

}
