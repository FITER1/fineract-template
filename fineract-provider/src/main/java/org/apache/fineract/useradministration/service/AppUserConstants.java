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
package org.apache.fineract.useradministration.service;

public final class AppUserConstants {

    private AppUserConstants() {

    }

    public static final String PASSWORD_NEVER_EXPIRES = "passwordNeverExpires";
    public static final String IS_SELF_SERVICE_USER = "isSelfServiceUser";
    public static final String CLIENTS = "clients";

    // user roles for FB
    public static final String GERENTE_ROLE_START_WITH = "Gerente";
    public static final String COORDINADOR_REGIONAL_ROLE_START_WITH = "Coordinador";
    public static final String REGIONAL_ROLE_START_WITH = "Regional";
    public static final String LIDER_AGENCIA_ROLE_START_WITH = "Líder de agencia";
    public static final String SUPERVISOR_ROLE_START_WITH = "Supervisor";
    public static final String FACILITATOR_ROLE_START_WITH = "Facilitador";

    // TODO: Remove hard coding of system user name and make this a configurable parameter
    public static final String SYSTEM_USER_NAME = "system";
}
