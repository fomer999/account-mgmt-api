package com.lf.service;

import com.lf.model.Account;
import com.lf.repository.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AccountServiceTest {

  private static final String ACCOUNT_ID = "testAccountId";
  private static final String ACCOUNT_NAME = "testAccountName";

  @Mock
  private AccountRepository repository;

  @InjectMocks
  private AccountServiceImpl service;

  @Test
  public void listShouldReturnAllAccounts() throws Exception {

    Account account1 = new Account().withId("testId1");
    Account account2 = new Account().withId("testId2");
    when(repository.findAll()).thenReturn(asList(account1, account2));
    Iterable<Account> result = service.list();
    assertThat(result, containsInAnyOrder(account1, account2));
  }

  @Test
  public void getNameShouldReturnEmptyOptionalWhenNoAccountFound() throws Exception {

    when(repository.findByName(ACCOUNT_NAME)).thenReturn(Optional.empty());
    Optional<Account> result = service.getAccountByName(ACCOUNT_NAME);
    assertThat(result, is(Optional.empty()));
  }

  @Test
  public void getNameShouldReturnResultWhenAccountFound() throws Exception {

    Account account = new Account().withId(ACCOUNT_NAME);
    when(repository.findByName(ACCOUNT_NAME)).thenReturn(Optional.of(account));
    Account result = service.getAccountByName(ACCOUNT_NAME).get();
    assertThat(result, is(equalTo(account)));
  }

  @Test
  public void readShouldReturnEmptyOptionalWhenNoAccountFound() throws Exception {

    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
    Optional<Account> result = service.getAccountById(ACCOUNT_ID);
    assertThat(result, is(Optional.empty()));
  }

  @Test
  public void readShouldReturnResultWhenAccountFound() throws Exception {

    Account account = new Account().withId(ACCOUNT_ID);
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
    Account result = service.getAccountById(ACCOUNT_ID).get();
    assertThat(result, is(equalTo(account)));
  }


  @Test
  public void saveShouldReturnNewAccountWhenAccountDoesNotExist() throws Exception {

    Account newAccount = new Account().withId(ACCOUNT_ID);
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
    Account result = service.save(newAccount).get();
    assertThat(result, is(equalTo(newAccount)));
    verify(repository).save(newAccount);
  }

  @Test
  public void updateShouldOverwriteExistingDataAndReturnNewDataWhenAccountExists() throws Exception {
    Account existingAccountData = new Account().withId(ACCOUNT_ID).withDescription("test description");
    Account newAccountData = new Account().withId(ACCOUNT_ID).withDescription("updated test description");
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.of(existingAccountData));
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.of(newAccountData));
    Account result = service.update(ACCOUNT_ID, newAccountData).get();
    assertThat(result.getDescription(), is(equalTo(newAccountData.getDescription())));
  }

  @Test
  public void updateShouldReturnEmptyOptionalWhenAccountNotFound() throws Exception {

    Account newAccountData = new Account().withId(ACCOUNT_ID).withDescription("updated test description");
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
    Optional<Account> result = service.update(ACCOUNT_ID, newAccountData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newAccountData);
  }

  @Test
  public void deleteShouldReturnEmptyOptionalWhenAccountNotFound() throws Exception {

    Account newAccountData = new Account().withId(ACCOUNT_ID).withDescription("updated test description");
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
    Optional<Account> result = service.delete(ACCOUNT_ID);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newAccountData);
  }

  @Test
  public void deleteShouldReturnDeletedAccountWhenAccountFound() throws Exception {

    Account newAccountData = new Account().withId(ACCOUNT_ID).withDescription("updated test description");
    when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.of(newAccountData));
    Account result = service.delete(ACCOUNT_ID).get();
    assertFalse(result.getIsActive());
  }
}