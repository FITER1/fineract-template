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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformPasswordEncoder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserPreviousPassword;
import org.apache.fineract.useradministration.domain.AppUserPreviousPasswordRepository;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AppUserWritePlatformServiceJpaRepositoryImplTest {

    @Mock
    private PlatformSecurityContext context;

    @Mock
    private PlatformPasswordEncoder platformPasswordEncoder;

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private ConfigurationReadPlatformService configurationReadPlatformService;
    @Mock
    private AppUserPreviousPasswordRepository appUserPreviewPasswordRepository;

    @InjectMocks
    private AppUserWritePlatformServiceJpaRepositoryImpl appUserService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCurrentPasswordToSaveAsPreview() {
        // Arrange
        AppUser user = mock(AppUser.class);
        JsonCommand command = mock(JsonCommand.class);

        when(user.getId()).thenReturn(1L);
        when(user.getPassword()).thenReturn("password");
        Mockito.mockStatic(DateUtils.class);
        when(DateUtils.getLocalDateOfTenant()).thenReturn(LocalDate.of(2023, 9, 10));
        when(user.getEncodedPassword(command, platformPasswordEncoder)).thenReturn("password");

        // Mock configuration
        GlobalConfigurationPropertyData config = new GlobalConfigurationPropertyData();
        config.setEnabled(true);
        config.setValue(5L); // Number of previous passwords
        when(configurationReadPlatformService.retrieveGlobalConfiguration("Restrict-re-use-of-password")).thenReturn(config);

        // Mock password history
        List<AppUserPreviousPassword> passwordHistory = new ArrayList<>();
        AppUserPreviousPassword previousPassword = new AppUserPreviousPassword(user);
        passwordHistory.add(previousPassword);
        when(appUserPreviewPasswordRepository.findByUserId(eq(1L), any())).thenReturn(passwordHistory);
        AppUserPreviousPassword result = appUserService.getCurrentPasswordToSaveAsPreview(user, command);
    }

}
