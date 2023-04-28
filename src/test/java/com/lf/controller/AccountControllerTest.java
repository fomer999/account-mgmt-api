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
    private static final String SECURITY_CONTEXT = "some-security-context";

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