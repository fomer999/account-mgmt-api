package com.lf.controller;

import com.lf.model.Account;
import com.lf.model.Configuration;
import com.lf.controller.util.ResponseEntityHelper;
import com.lf.model.SecurityContext;
import com.lf.service.AccountService;
import com.lf.service.ConfigurationService;
import com.lf.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConfigurationControllerTest {

    @Mock
    private ConfigurationService service;

    @Mock
    private AccountService accountService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ConfigurationController controller;

    private static final String PARENT_ACCOUNT_ID = "parentAccountId";
    private static final String CONFIGURATION_ID = "d4dfdaf6-02a2-11e7-93ae-92361f002672";
    private static final String LOGGED_IN_USER_USERNAME = "testUsername";
    private static final String ACCOUNT_ID = "d4dfdaf6-02a2-11e7-93ae-92361f002671";
    private static final String UPDATED_NAME = "updatedName";
    private static final List<String> CONFIG_ID_LIST = Arrays.asList("testId", "testId1");
    private static final List<String> ACCOUNT_ID_LIST = Arrays.asList("testId", "testId1");
    private static final List<String> CONFIG_NAME_LIST = Arrays.asList("testConfigName", "testConfigName1");
    private static final List<String> ACCOUNT_NAME_LIST = Arrays.asList("testAccountName", "testAccountName1");
    private static final String SECURITY_CONTEXT = "rO0ABXNyACJjb20uZGx2ci5vYXV0aC5EbHZyU2VjdXJpdHlDb250ZXh0AAAAAAAAAAED" +
            "AAVMAAlhY2NvdW50SWR0ABJMamF2YS9sYW5nL1N0cmluZztbAAdjb250ZXh0dAACW0JMAAhoYXNSb2xlc3QAEExqYXZhL3V0aWwvTGlzdDt" +
            "MAARuYW1lcQB+AAFMAAZ1c2VySWRxAH4AAXhwdXIAAltCrPMX+AYIVOACAAB4cAAAADBNbVV3TldVMU1tWXRaR05sWmkwME1UTmlMVGcwTj" +
            "JFdE5ERXhaakF4Tmpaa05UQTB1cQB+AAUAAAYUck8wQUJYTnlBQk5xWVhaaExuVjBhV3d1UVhKeVlYbE1hWE4wZUlIU0habkhZWjBEQUFGS" +
            "kFBUnphWHBsZUhBQUFBQXNkd1FBQUFBc2RBQVRVazlNUlY5RFVrVkJWRVZmUVVORFQxVk9WSFFBRTFKUFRFVmZWVkJFUVZSRlgwRkRRMDlW" +
            "VGxSMEFCTlNUMHhGWDBSRlRFVlVSVjlCUTBOUFZVNVVkQUFWVWs5TVJWOVNSVlJTU1VWV1JWOUJRME5QVlU1VWRBQVFVazlNUlY5RFVrVkJ" +
            "WRVZmVlZORlVuUUFFRkpQVEVWZlZWQkVRVlJGWDFWVFJWSjBBQkJTVDB4RlgwUkZURVZVUlY5VlUwVlNkQUFTVWs5TVJWOVNSVlJTU1VWV1" +
            "JWOVZVMFZTZEFBWlVrOU1SVjlEVWtWQlZFVmZRMDlPUmtsSFZWSkJWRWxQVG5RQUdWSlBURVZmVlZCRVFWUkZYME5QVGtaSlIxVlNRVlJKV" +
            "DA1MEFCbFNUMHhGWDBSRlRFVlVSVjlEVDA1R1NVZFZVa0ZVU1U5T2RBQWJVazlNUlY5U1JWUlNTVVZXUlY5RFQwNUdTVWRWVWtGVVNVOU9k" +
            "QUFtVWs5TVJWOURVa1ZCVkVWZlEwOU9Sa2xIVlZKQlZFbFBUbDlUU1VSRlEwRlNYMFpKVEVWMEFDWlNUMHhGWDBSRlRFVlVSVjlEVDA1R1N" +
            "VZFZVa0ZVU1U5T1gxTkpSRVZEUVZKZlJrbE1SWFFBS0ZKUFRFVmZVa1ZVVWtsRlZrVmZRMDlPUmtsSFZWSkJWRWxQVGw5VFNVUkZRMEZTWD" +
            "BaSlRFVjBBQnhTVDB4RlgxSkZWRkpKUlZaRlgxTk9VMTlVVDFCSlExOU9RVTFGZEFBYVVrOU1SVjlEVWtWQlZFVmZVMDVUWDFSUFVFbERYM" +
            "DVCVFVWMEFCcFNUMHhGWDBSRlRFVlVSVjlUVGxOZlZFOVFTVU5mVGtGTlJYUUFHbEpQVEVWZlZWQkVRVlJGWDFOT1UxOVVUMUJKUTE5T1FV" +
            "MUZkQUFhVWs5TVJWOVRSVTVFWDFOT1UxOU9UMVJKUmtsRFFWUkpUMDUwQUJOU1QweEZYMUpGVkZKSlJWWkZYMUpQVEVWVGRBQWRVazlNUlY" +
            "5U1JWUlNTVVZXUlY5UVVsOU9UMVJKUmtsRFFWUkpUMDUwQUJ0U1QweEZYME5TUlVGVVJWOVFVbDlPVDFSSlJrbERRVlJKVDA1MEFCdFNUMH" +
            "hGWDFWUVJFRlVSVjlRVWw5T1QxUkpSa2xEUVZSSlQwNTBBQnRTVDB4RlgwUkZURVZVUlY5UVVsOU9UMVJKUmtsRFFWUkpUMDUwQUJ0U1Qwe" +
            "EZYME5TUlVGVVJWOVRSVkpXUlZKZlJFVkdRVlZNVkZOMEFCdFNUMHhGWDFWUVJFRlVSVjlUUlZKV1JWSmZSRVZHUVZWTVZGTjBBQnRTVDB4" +
            "RlgwUkZURVZVUlY5VFJWSldSVkpmUkVWR1FWVk1WRk4wQUIxU1QweEZYMUpGVkZKSlJWWkZYMU5GVWxaRlVsOUVSVVpCVlV4VVUzUUFJMUp" +
            "QVEVWZlVrVlVVa2xGVmtWZlNVNVVSVkpKVFY5RFQwNUdTVWRWVWtGVVNVOU9kQUFoVWs5TVJWOURVa1ZCVkVWZlNVNVVSVkpKVFY5RFQwNU" +
            "dTVWRWVWtGVVNVOU9kQUFoVWs5TVJWOVZVRVJCVkVWZlNVNVVSVkpKVFY5RFQwNUdTVWRWVWtGVVNVOU9kQUFoVWs5TVJWOUVSVXhGVkVWZ" +
            "lNVNVVSVkpKVFY5RFQwNUdTVWRWVWtGVVNVOU9kQUFVVWs5TVJWOVZVRVJCVkVWZlYwOVNTMFpNVDFkMEFCUlNUMHhGWDBSRlRFVlVSVjlY" +
            "VDFKTFJreFBWM1FBRkZKUFRFVmZVMGxIVGtGTVgxZFBVa3RHVEU5WGNRQitBQ0J4QUg0QUlYRUFmZ0FpZEFBVVVrOU1SVjlEVWtWQlZFVmZ" +
            "WMDlTUzBaTVQxZHhBSDRBSTNFQWZnQWtjUUIrQUNWMEFCSlNUMHhGWDFKRlFVUmZTVTVUU1VkSVZGTjR1cQB+AAUAAAAwTjJOall6UmtaRF" +
            "l0WlRWak5DMDBPVFUyTFdJNU9USXRaREF5TURJNFpXSXpOelUwdXEAfgAFAAAAEFlXeHdhR0VnZFhObGNnPT14";

    @Before
    public void setupSecurityContext() {
        when(securityService.userHasRoles(any())).thenReturn(true);
        when(securityService.isLoggedInUserActive(any())).thenReturn(true);
        when(securityService.getAccountIdForLoggedInUser(any())).thenReturn(ACCOUNT_ID);
        when(securityService.getUserNameForUserId(any())).thenReturn(LOGGED_IN_USER_USERNAME);
        when(securityService.validate(any())).thenReturn(Optional.of(new SecurityContext(SECURITY_CONTEXT)));
    }

    @Test
    public void listShouldSayOKWhenNoConfigurationsFound() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.list(ACCOUNT_ID)).thenReturn(Optional.of(emptyList()));
        ResponseEntity<List<Configuration>> result = controller.listConfigurations(ACCOUNT_ID, null, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(OK)));
    }

    @Test
    public void listShouldSayForbiddenWhenLoggedInUserIsNotActive() throws Exception {
        when(securityService.isLoggedInUserActive(any())).thenReturn(false);

        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.list(ACCOUNT_ID)).thenReturn(Optional.of(emptyList()));
        ResponseEntity<List<Configuration>> result = controller.listConfigurations(ACCOUNT_ID, null, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(FORBIDDEN)));
    }

    @Test
    public void listShouldSayOkAndGiveConfigurationsIfConfigurationsExist() throws Exception {
        Configuration configuration1 = new Configuration().withId("testConfigurationId1");
        Configuration configuration2 = new Configuration().withId("testConfigurationId2");

        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.list(ACCOUNT_ID)).thenReturn(Optional.of(asList(configuration1, configuration2)));
        ResponseEntity<List<Configuration>> result = controller.listConfigurations(ACCOUNT_ID,null, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(containsInAnyOrder(configuration1, configuration2)))));
    }

    @Test
    public void readShouldSayNotFoundWhenConfigurationNotFound() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.getConfigurationById(CONFIGURATION_ID)).thenReturn(Optional.empty());
        ResponseEntity<Configuration> result = controller.getById(ACCOUNT_ID, CONFIGURATION_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void readShouldSayForbiddenWhenAccountIdForLoggedInUserIsNotSameOrParentOfGivenAccount() throws Exception {

        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
        when(service.getConfigurationById(CONFIGURATION_ID)).thenReturn(Optional.of(configuration));
        ResponseEntity<Configuration> result = controller.getById(ACCOUNT_ID, CONFIGURATION_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(FORBIDDEN)));
    }

    @Test
    public void readShouldReturnConfigurationWhenAccountIdForLoggedInUserIsSameAsGivenAccount() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID).withParentAccountId(PARENT_ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
        when(service.getConfigurationById(CONFIGURATION_ID)).thenReturn(Optional.of(configuration));
        ResponseEntity<Configuration> result = controller.getById(ACCOUNT_ID, CONFIGURATION_ID, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(configuration)))));
    }

    @Test
    public void readShouldReturnConfigurationWhenAccountIdForLoggedInUserIsSameAsParentOfGivenAccount() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID).withParentAccountId(PARENT_ACCOUNT_ID);
        Account parentAccount = new Account().withId(PARENT_ACCOUNT_ID).withParentAccountId(PARENT_ACCOUNT_ID);
        when(accountService.getAllDescendantsForAccount(PARENT_ACCOUNT_ID)).thenReturn(asList(account));
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
        when(service.getConfigurationById(CONFIGURATION_ID)).thenReturn(Optional.of(configuration));
        ResponseEntity<Configuration> result = controller.getById(ACCOUNT_ID, CONFIGURATION_ID,  SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(configuration)))));
    }


    @Test
    public void readShouldReturnConfigurationWhenConfigurationExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
        when(service.getConfigurationById(CONFIGURATION_ID)).thenReturn(Optional.of(configuration));
        ResponseEntity<Configuration> result = controller.getById(ACCOUNT_ID, CONFIGURATION_ID, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(configuration)))));
    }

    @Test
    public void createShouldConflictIfConfigurationExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
        when(service.save(ACCOUNT_ID, configuration)).thenReturn(Optional.empty());
        ResponseEntity<Configuration> result = controller.createConfiguration(configuration, ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(BAD_REQUEST)));
    }

    @Test
    public void createShouldReplyWithCreatedAndConfigurationData() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
        when(service.save(ACCOUNT_ID, configuration)).thenReturn(Optional.of(configuration));
        ResponseEntity<Configuration> result = controller.createConfiguration(configuration, ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(CREATED),
                ResponseEntityHelper.responseEntityThat(equalTo(configuration)))));
    }

    @Test
    public void updateShouldReplyWithNotFoundIfConfigurationDoesNotExist() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration updatedConfiguration = new Configuration().withId(CONFIGURATION_ID).withName(UPDATED_NAME);
        when(service.update(ACCOUNT_ID, CONFIGURATION_ID, updatedConfiguration)).thenReturn(Optional.empty());
        ResponseEntity<Configuration> result = controller.updateConfiguration(updatedConfiguration, ACCOUNT_ID, CONFIGURATION_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void updateShouldReplyWithUpdatedDataAndOkIfConfigurationExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration updatedConfiguration = new Configuration().withId(CONFIGURATION_ID).withName(UPDATED_NAME);
        when(service.update(ACCOUNT_ID, CONFIGURATION_ID, updatedConfiguration)).thenReturn(Optional.of(updatedConfiguration));
        ResponseEntity<Configuration> result = controller.updateConfiguration(updatedConfiguration, ACCOUNT_ID, CONFIGURATION_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(updatedConfiguration)))));
    }

    @Test
    public void deleteShouldRespondWithNotFoundIfConfigurationDoesNotExist() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.delete(CONFIGURATION_ID, false)).thenReturn(Optional.empty());
        ResponseEntity result = controller.deleteConfiguration(ACCOUNT_ID, CONFIGURATION_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void deleteShouldRespondWithOkIfDeleteSuccessful() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Configuration deletedConfiguration = new Configuration().withId(CONFIGURATION_ID);
        when(service.delete(CONFIGURATION_ID, false)).thenReturn(Optional.of(deletedConfiguration));
        ResponseEntity result = controller.deleteConfiguration(ACCOUNT_ID, CONFIGURATION_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(OK)));
    }
}