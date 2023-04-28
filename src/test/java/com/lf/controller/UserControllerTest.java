package com.lf.controller;

import com.lf.model.Account;
import com.lf.model.User;
import com.lf.model.UserRole;
import com.lf.model.SecurityContext;
import com.lf.controller.util.ResponseEntityHelper;
import com.lf.service.AccountService;
import com.lf.service.SecurityService;
import com.lf.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private AccountService accountService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserController controller;

    private static final String USER_ID = "d4dfdaf6-02a2-11e7-93ae-92361f002672";
    private static final String ACCOUNT_ID = "d4dfdaf6-02a2-11e7-93ae-92361f002671";
    private static final String PARENT_ACCOUNT_ID = "parentAccountId";
    private static final String LOGGED_IN_USER_USERNAME = "testUsername";
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
    public void listShouldSayOKWhenNoUsersFound() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.list(ACCOUNT_ID)).thenReturn(emptyList());
        ResponseEntity<List<User>> result = controller.listUsers(ACCOUNT_ID, null, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(OK)));
    }

    @Test
    public void listShouldSayForbiddenWhenLoggedInUserIsNotActive() throws Exception {
        when(securityService.isLoggedInUserActive(any())).thenReturn(false);
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.list(ACCOUNT_ID)).thenReturn(emptyList());
        ResponseEntity<List<User>> result = controller.listUsers(ACCOUNT_ID, null, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(FORBIDDEN)));
    }

    @Test
    public void listShouldSayOkAndGiveUsersIfUsersExist() throws Exception {
        User user1 = new User().withId("testUserId1");
        User user2 = new User().withId("testUserId2");

        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.list(ACCOUNT_ID)).thenReturn(asList(user1, user2));
        ResponseEntity<List<User>> result = controller.listUsers(ACCOUNT_ID, null, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(containsInAnyOrder(user1, user2)))));
    }

    @Test
    public void getUserRolesByIdShouldReturnTheListOfUserRoles() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID).withParentAccountId(PARENT_ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.ROLE_RETRIEVE_ROLES);
        User user = new User().withId(USER_ID);
        user.setUserRoles(userRoles);

        when(service.getUserRolesById(USER_ID)).thenReturn(Optional.of(userRoles));
        ResponseEntity<List<UserRole>> result = controller.getUserRoles(ACCOUNT_ID, USER_ID, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(userRoles)))));
    }

    @Test
    public void readShouldSayNotFoundWhenUserNotFound() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.getUserById(USER_ID)).thenReturn(Optional.empty());
        ResponseEntity<Optional<User>> result = controller.getById(ACCOUNT_ID, USER_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void readShouldSayForbiddenWhenAccountIdForLoggedInUserIsNotSameOrParentOfGivenAccount() throws Exception {
        when(securityService.isLoggedInUserActive(any())).thenReturn(false);

        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        User user = new User().withId(USER_ID);
        when(service.getUserById(USER_ID)).thenReturn(Optional.of(user));
        ResponseEntity<User> result = controller.getById(ACCOUNT_ID, USER_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(FORBIDDEN)));
    }

    @Test
    public void readShouldReturnUserWhenAccountIdForLoggedInUserIsSameAsGivenAccount() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID).withParentAccountId(PARENT_ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        User user = new User().withId(USER_ID);
        when(service.getUserById(USER_ID)).thenReturn(Optional.of(user));
        ResponseEntity<User> result = controller.getById(ACCOUNT_ID, USER_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(user)))));
    }

    @Test
    public void readShouldReturnUserWhenAccountIdForLoggedInUserIsSameAsParentOfGivenAccount() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID).withParentAccountId(PARENT_ACCOUNT_ID);
        when(accountService.getAllDescendantsForAccount(PARENT_ACCOUNT_ID)).thenReturn(asList(account));
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        User user = new User().withId(USER_ID);
        when(service.getUserById(USER_ID)).thenReturn(Optional.of(user));
        ResponseEntity<User> result = controller.getById(ACCOUNT_ID, USER_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(user)))));
    }

    @Test
    public void readShouldReturnUserWhenUserExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        User user = new User().withId(USER_ID);
        when(service.getUserById(USER_ID)).thenReturn(Optional.of(user));
        ResponseEntity<User> result = controller.getById(ACCOUNT_ID, USER_ID, false, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(OK),
                ResponseEntityHelper.responseEntityThat(equalTo(user)))));
    }

    @Test
    public void createShouldConflictIfUserExists() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        User user = new User().withId(USER_ID);
        when(service.save(ACCOUNT_ID, user)).thenReturn(Optional.empty());
        ResponseEntity<User> result = controller.createUser(user, ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(CONFLICT)));
    }

    @Test
    public void createShouldReplyWithCreatedAndUserData() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        User user = new User().withId(USER_ID);
        when(service.save(ACCOUNT_ID, user)).thenReturn(Optional.of(user));
        ResponseEntity<User> result = controller.createUser(user, ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(allOf(
                ResponseEntityHelper.responseEntityWithStatus(CREATED),
                ResponseEntityHelper.responseEntityThat(equalTo(user)))));
    }

    @Test
    public void createShouldReplyWithBadRequestIfUsernameIsMalformed() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        String malformedUsername = "notAnEmailAddress";
        User user = new User();
        user.setUsername(malformedUsername);
        when(service.save(ACCOUNT_ID, user)).thenReturn(Optional.of(user));
        ResponseEntity<User> result = controller.createUser(user, ACCOUNT_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(BAD_REQUEST)));
    }

    @Test
    public void deleteShouldRespondWithNotFoundIfUserDoesNotExist() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(service.delete(ACCOUNT_ID,USER_ID)).thenReturn(Optional.empty());
        ResponseEntity<Void> result = controller.deleteUser(ACCOUNT_ID, USER_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void deleteShouldRespondWithOkIfDeleteSuccessful() throws Exception {
        Account account = new Account().withId(ACCOUNT_ID);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        User deletedUser = new User().withId(USER_ID);
        deletedUser.setIsActive(false);
        when(service.delete(ACCOUNT_ID, USER_ID)).thenReturn(Optional.of(deletedUser));
        ResponseEntity<Void> result = controller.deleteUser(ACCOUNT_ID, USER_ID, SECURITY_CONTEXT);
        assertThat(result, is(ResponseEntityHelper.responseEntityWithStatus(OK)));
    }
}