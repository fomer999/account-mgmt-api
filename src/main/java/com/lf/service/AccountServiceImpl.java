package com.lf.service;

import com.lf.model.Account;
import com.lf.repository.AccountRepository;
import com.lf.util.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository repository;

    public Iterable<Account> list() {
        log.trace("Entering list()");
        return repository.findAll();
    }

    public Optional<Account> getAccountById(String id) {
        log.trace("Entering getAccountById() with {}", id);
        return repository.findById(id);
    }

    public Optional<Account> getAccountByName(String name) {
        log.trace("Entering getAccountByName() with {}", name);
        return repository.findByName(name);
    }

    public List<Account> getAllDescendantsForAccount(String id) {
        log.trace("Entering getAllDescendantsForAccount() with {}", id);
        List<Account> allDescendantAccounts = new ArrayList<Account>();

        List<Account> immediateDescendants = repository.findByParentAccountId(id);
        for (Account account : immediateDescendants) { // add this account and all its descendants to the return list
            allDescendantAccounts.add(account);
            allDescendantAccounts.addAll(repository.findByParentAccountId(account.getId()));
        }
        return allDescendantAccounts;
    }

    public List<String> getAllChildrenAccountIds(List<String> accountIds) {
        List<String> allAccountsAtLevel = new ArrayList<>();
        // Add the first immediate descendants
        allAccountsAtLevel.addAll(accountIds);

        // Push all subsequent descendants at each level in the tree into the stack
        Stack<List<Account>> stack = new Stack<>();
        for(String accountId : accountIds) {
            stack.push(repository.findByParentAccountId(accountId));
            do {
                allAccountsAtLevel.addAll(stack.pop().stream()
                        .map(e -> e.getId())
                        .collect(Collectors.toList())
                );
            } while(!stack.isEmpty());
        }
        return allAccountsAtLevel;
    }

    public Optional<Account> save(Account account) {
        log.info("Entering save() with {}", account);

        //Cosmos does not auto-generate ids - set id value if it is not supplied in payload
        if (ApiUtils.isNullOrEmpty(account.getId())) {
            account.setId(UUID.randomUUID().toString());
        }

        account.setIsActive(true);
        account.setCreatedDate(new Date());
        repository.save(account);
        return Optional.of(account);
    }

    public Optional<Account> update(String id, Account newAccountData) {
        log.trace("Entering update() with {}", newAccountData);

        Optional<Account> existingAccount = repository.findById(id);
        if (!existingAccount.isPresent()) {
            log.warn("Account {} not found", id);
            return Optional.empty();
        }
        Account oldAccountData = existingAccount.get();

        // ensure non-editable fields are not modified by the update
        newAccountData.setId(id);
        newAccountData.setCreatedBy(oldAccountData.getCreatedBy());
        newAccountData.setCreatedDate(oldAccountData.getCreatedDate());

        repository.save(newAccountData);
        return repository.findById(id);
    }

    public Optional<Account> delete(String id) {
        log.trace("Entering delete() with {}", id);

        Optional<Account> account = repository.findById(id);
        if (!account.isPresent()) {
            log.warn("Account {} not found", id);
            return Optional.empty();
        }
        Account accountToDelete = account.get();
        accountToDelete.setIsActive(false);
        repository.save(accountToDelete);
        return Optional.of(accountToDelete);
    }
}
