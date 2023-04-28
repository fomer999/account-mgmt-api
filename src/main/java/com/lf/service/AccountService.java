package com.lf.service;

import com.lf.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Iterable<Account> list();

    Optional<Account> getAccountById(String id);

    Optional<Account> getAccountByName(String name);

    List<Account> getAllDescendantsForAccount(String id);

    Optional<Account> save(Account account);

    Optional<Account> update(String id, Account newAccountData);

    Optional<Account> delete(String id);

    List<String> getAllChildrenAccountIds(List<String> accountIds);
}
