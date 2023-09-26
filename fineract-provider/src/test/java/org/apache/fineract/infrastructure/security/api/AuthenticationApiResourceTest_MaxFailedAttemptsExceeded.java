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
package org.apache.fineract.infrastructure.security.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.apache.fineract.infrastructure.businessdate.domain.BusinessDateType;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.ActionContext;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.constants.AccountLockConfigurationConstants;
import org.apache.fineract.infrastructure.security.exception.UserLockedOutException;
import org.apache.fineract.infrastructure.security.service.PlatformUserDetailsService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class AuthenticationApiResourceTest_MaxFailedAttemptsExceeded {

    @Mock
    private ConfigurationReadPlatformService configurationReadPlatformService;

    @Mock
    private PlatformUserDetailsService platformUserDetailsService;

    @Mock
    private DaoAuthenticationProvider customAuthenticationProvider;

    @Mock
    private AppUserWritePlatformService appUserWritePlatformService;

    @InjectMocks
    private AuthenticationApiResource authTestService;

    @BeforeEach
    public void setUp() {
        ThreadLocalContextUtil.setTenant(new FineractPlatformTenant(1L, "default", "Default", "Asia/Kolkata", null));
        ThreadLocalContextUtil.setActionContext(ActionContext.DEFAULT);
        ThreadLocalContextUtil
                .setBusinessDates(new HashMap<>(Map.of(BusinessDateType.BUSINESS_DATE, LocalDate.now(ZoneId.systemDefault()))));
    }

    @Test
    public void testAuthentication_Before_MaxFailedAttemptsExceeded() {
        // Arrange
        final Long maxFailedLoginAttempts = 3l; // Set this value to your desired limit
        final Long lockoutDuration = 30l; // Set this value to your desired duration

        GlobalConfigurationPropertyData failedLoginAttempts = new GlobalConfigurationPropertyData();
        failedLoginAttempts.setValue(maxFailedLoginAttempts);
        when(configurationReadPlatformService.retrieveGlobalConfiguration(AccountLockConfigurationConstants.FAILED_LOGIN_ATTEMPTS))
                .thenReturn(failedLoginAttempts);

        GlobalConfigurationPropertyData lockDownDuration = new GlobalConfigurationPropertyData();
        lockDownDuration.setValue(lockoutDuration);
        when(configurationReadPlatformService.retrieveGlobalConfiguration(AccountLockConfigurationConstants.ACCOUNT_LOCK_DURATION))
                .thenReturn(lockDownDuration);

        AppUser user = new AppUser() {

            {
                setCanLoginAfter(LocalDateTime.now(DateUtils.getDateTimeZoneOfTenant()));
                setNoOfFailedLoginAttempts(1);
            }
        };
        int initialLoginAttempts = user.getNoOfFailedLoginAttempts();

        when(platformUserDetailsService.loadUserByUsername(anyString())).thenReturn(user);

        when(customAuthenticationProvider.authenticate(Mockito.isA(Authentication.class)))
                .thenThrow(new AuthenticationCredentialsNotFoundException("Wrong credentials for user"));

        // Act and Assert
        assertThrows(AuthenticationException.class,
                () -> authTestService.authenticate("{\"username\":\"mifos\",\"password\":\"pass\"}", false));

        assertEquals(user.getNoOfFailedLoginAttempts(), initialLoginAttempts + 1);
    }

    @Test
    public void testAuthentication_Prior_MaxFailedAttemptsExceeded() {
        // Arrange
        final Long maxFailedLoginAttempts = 3l; // Set this value to your desired limit
        final Long lockoutDuration = 30l; // Set this value to your desired duration

        GlobalConfigurationPropertyData failedLoginAttempts = new GlobalConfigurationPropertyData();
        failedLoginAttempts.setValue(maxFailedLoginAttempts);
        when(configurationReadPlatformService.retrieveGlobalConfiguration(AccountLockConfigurationConstants.FAILED_LOGIN_ATTEMPTS))
                .thenReturn(failedLoginAttempts);

        GlobalConfigurationPropertyData lockDownDuration = new GlobalConfigurationPropertyData();
        lockDownDuration.setValue(lockoutDuration);
        when(configurationReadPlatformService.retrieveGlobalConfiguration(AccountLockConfigurationConstants.ACCOUNT_LOCK_DURATION))
                .thenReturn(lockDownDuration);

        LocalDateTime now = LocalDateTime.now(DateUtils.getDateTimeZoneOfTenant());

        AppUser user = new AppUser() {

            {
                setCanLoginAfter(now);
                setNoOfFailedLoginAttempts(2);
            }
        };
        int initialLoginAttempts = user.getNoOfFailedLoginAttempts();

        when(platformUserDetailsService.loadUserByUsername(anyString())).thenReturn(user);

        when(customAuthenticationProvider.authenticate(Mockito.isA(Authentication.class)))
                .thenThrow(new AuthenticationCredentialsNotFoundException("Wrong credentials for user"));

        // Act and Assert
        assertThrows(AuthenticationException.class,
                () -> authTestService.authenticate("{\"username\":\"mifos\",\"password\":\"pass\"}", false));

        assertEquals(user.getNoOfFailedLoginAttempts(), initialLoginAttempts + 1);

        assertUserCanLoginAfter(user.getCanLoginAfter(), now.plusMinutes(lockoutDuration));
    }

    public static void assertUserCanLoginAfter(LocalDateTime userCanLoginAfter, LocalDateTime expectedCanLoginAfter) {
        assertTrue(userCanLoginAfter.isAfter(expectedCanLoginAfter) || userCanLoginAfter.isEqual(expectedCanLoginAfter),
                "User can log in after the specified lockout duration");
    }

    @Test
    public void testAuthentication_MaxFailedAttemptsExceeded() {
        // Arrange
        final Long maxFailedLoginAttempts = 3l; // Set this value to your desired limit
        final Long lockoutDuration = 30l; // Set this value to your desired duration

        GlobalConfigurationPropertyData failedLoginAttempts = new GlobalConfigurationPropertyData();
        failedLoginAttempts.setValue(maxFailedLoginAttempts);
        when(configurationReadPlatformService.retrieveGlobalConfiguration(AccountLockConfigurationConstants.FAILED_LOGIN_ATTEMPTS))
                .thenReturn(failedLoginAttempts);

        GlobalConfigurationPropertyData lockDownDuration = new GlobalConfigurationPropertyData();
        lockDownDuration.setValue(lockoutDuration);
        when(configurationReadPlatformService.retrieveGlobalConfiguration(AccountLockConfigurationConstants.ACCOUNT_LOCK_DURATION))
                .thenReturn(lockDownDuration);

        when(platformUserDetailsService.loadUserByUsername(anyString())).thenReturn(new AppUser() {

            {
                setCanLoginAfter(LocalDateTime.now(DateUtils.getDateTimeZoneOfTenant()).plusMinutes(lockoutDuration));
                setNoOfFailedLoginAttempts(3);
            }
        });

        // Act and Assert
        assertThrows(UserLockedOutException.class,
                () -> authTestService.authenticate("{\"username\":\"mifos\",\"password\":\"pass\"}", false));
    }

}
