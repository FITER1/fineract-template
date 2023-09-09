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
package org.apache.fineract.infrastructure.security.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SpringSecurityPlatformSecurityContextTest {

    @Mock
    private ConfigurationDomainService configurationDomainService;

    @InjectMocks
    SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext;

    @Test
    public void testPasswordRenewalRequiredForForceResetOnFirstLogon() {
        when(configurationDomainService.isPasswordForcedResetEnable()).thenReturn(false);
        when(configurationDomainService.isPasswordForceResetOnFirstLogon()).thenReturn(true);

        // Mock the necessary conditions for password renewal
        when(configurationDomainService.retrievePasswordLiveTime()).thenReturn(90L); // Password duration in days
        AppUser currentUser = mock(AppUser.class);
        when(currentUser.isFirstTimeLoginRemaining()).thenReturn(true);

        // Act
        boolean result = springSecurityPlatformSecurityContext.doesPasswordHaveToBeRenewed(currentUser);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testPasswordRenewalNotRequired() {
        // Arrange
        when(configurationDomainService.isPasswordForcedResetEnable()).thenReturn(false);
        when(configurationDomainService.isPasswordForceResetOnFirstLogon()).thenReturn(false);

        AppUser currentUser = mock(AppUser.class);
        when(currentUser.isFirstTimeLoginRemaining()).thenReturn(true);

        boolean result = springSecurityPlatformSecurityContext.doesPasswordHaveToBeRenewed(currentUser);

        assertTrue(!result);
    }

}
