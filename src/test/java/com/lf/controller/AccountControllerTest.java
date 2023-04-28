package com.lf.controller;

import com.lf.model.Account;
import com.lf.controller.util.ResponseEntityHelper;
import com.lf.model.SecurityContext;
import com.lf.service.AccountService;
import com.lf.service.SecurityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AccountControllerTest {

    @Mock
    private AccountService service;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AccountController controller;

    private static final String BAD_ACCOUNT_ID = "not_a_uuid";
    private static final String ACCOUNT_NAME = "test-account";
    private static final String PARENT_ACCOUNT_ID = "parentAccountId";
    private static final String LOGGED_IN_USER_USERNAME = "testUsername";
    private static final String ACCOUNT_ID = "d4dfdaf6-02a2-11e7-93ae-92361f002671";
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
    public void getByIdShouldSayBadRequestWhenAccountIdIsInvalid() throws Exception {
        //when(service.getAccountById(ACCOUNT_ID)).thenReturn(Optional.empty());
        ResponseEntity<Account> result = controller.getById(BAD_ACCOUNT_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(BAD_REQUEST)));
    }

    @Test
    public void getByIdShouldSayNotFoundWhenAccountNotFound() throws Exception {
        when(service.getAccountById(ACCOUNT_ID)).thenReturn(Optional.empty());
        ResponseEntity<Account> result = controller.getById(ACCOUNT_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void readShouldReturnAccountWhenAccountIdForLoggedInUserIsSameAsGivenAccount() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(service.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        ResponseEntity<Account> result = controller.getById(ACCOUNT_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(account)))));
    }

    @Test
    public void readShouldReturnAccountWhenAccountIdForLoggedInUserIsSameAsParentOfGivenAccount() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        Account parentAccount = new Account().withId(PARENT_ACCOUNT_ID);
        when(service.getAllDescendantsForAccount(PARENT_ACCOUNT_ID)).thenReturn(asList(account));
        when(service.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        ResponseEntity<Account> result = controller.getById(ACCOUNT_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(account)))));
    }

    @Test
    public void readShouldReturnAccountWhenAccountExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(service.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        ResponseEntity<Account> result = controller.getById(ACCOUNT_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(account)))));
    }

    @Test
    public void readShouldReturnAccountWhenAccountNameExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_NAME);
        when(service.getAccountByName(ACCOUNT_NAME)).thenReturn(Optional.of(account));
        ResponseEntity<Account> result = controller.getById(ACCOUNT_NAME, true, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(account)))));
    }

    @Test
    public void createShouldConflictIfAccountExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(service.save(account)).thenReturn(Optional.empty());
        ResponseEntity<Account> result = controller.createAccount(account, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(BAD_REQUEST)));
    }

    @Test
    public void createShouldReplyWithCreatedAndAccountData() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);

        when(service.save(account)).thenReturn(Optional.of(account));
        ResponseEntity<Account> result = controller.createAccount(account, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(CREATED),
                ResponseEntityHelper.responseEntityThat(equalTo(account)))));
    }

    @Test
    public void updateShouldReplyWithUpdatedDataAndOkIfAccountExists() throws Exception {
        Account updatedAccount = new Account().withId(ACCOUNT_ID).withDescription("updated customer");

        when(service.update(ACCOUNT_ID, updatedAccount)).thenReturn(Optional.of(updatedAccount));
        ResponseEntity<Account> result = controller.updateAccount(updatedAccount, ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(updatedAccount)))));
    }

    @Test
    public void deleteShouldRespondWithNotFoundIfAccountDoesNotExist() throws Exception {
        when(service.delete(ACCOUNT_ID)).thenReturn(Optional.empty());
        ResponseEntity<Void> result = controller.deleteAccount(ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void deleteShouldRespondWithOkIfDeleteSuccessful() throws Exception {
        Account deletedAccount = new Account().withId(ACCOUNT_ID);
        deletedAccount.setIsActive(false);
        when(service.delete(ACCOUNT_ID)).thenReturn(Optional.of(deletedAccount));
        ResponseEntity<Void> result = controller.deleteAccount(ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(OK)));
    }
}