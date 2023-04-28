package com.lf.service;

import com.lf.model.User;
import com.lf.model.UserRole;
import com.lf.repository.UserRepository;
import com.lf.util.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository repository;

    public List<User> list(String accountId) {
        log.trace("Entering list()");
        return repository.findByAccountId(accountId);
    }

    public Optional<User> getUserById(String id) {
        log.trace("Entering getUserById() with {}", id);
        return repository.findById(id);
    }

    public Optional<User> getUserByName(String name) {
        log.trace("Entering getAccountByName() with {}", name);
        return repository.findByName(name);
    }

    public Optional<User> getUserByUsername(String username) {
        log.trace("Entering getUserByName() with {}", username);
        List<User> users = repository.findByUsername(username);
        if (!users.isEmpty()) {
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }

    public Optional<List<UserRole>> getUserRolesById(String id) {
        log.trace("Entering getUserRoles() with userId : {}", id);
        Optional<User> getUser = getUserById(id);
        if(!getUser.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(getUser.get().getUserRoles());
    }

    public Optional<User> save(String accountId, User user) {
        log.trace("Entering save() with {}", user);

        //Cosmos does not auto-generate ids - set id value if it is not supplied in payload
        if (ApiUtils.isNullOrEmpty(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }
        // Since username uniqueness cannot be enforced in DB, enforce it here
        if (getUserByUsername(user.getUsername()).isPresent()) {
            log.warn("User with username {} already exists", user.getUsername());
            return Optional.empty();
        }

        user.setIsActive(true);
        if (!ApiUtils.isNullOrEmpty(accountId)) {
            user.setAccountId(accountId);
        }
        user.setCreatedDate(new Date());

        repository.save(user);
        return Optional.of(user);
    }

    public Optional<User> update(String accountId, String id, User newUserData) {

        log.trace("Entering update() with {}", newUserData);

        Optional<User> existingUser = repository.findById(id);
        if (!existingUser.isPresent()) {
            log.warn("User {} not found", id);
            return Optional.empty();
        }

        User userToUpdate = existingUser.get();

        // Since username uniqueness cannot be enforced in DB, enforce it here
        if (!newUserData.getUsername().equalsIgnoreCase(userToUpdate.getUsername())) {
            Optional<User> existingUserWithSameUsername = getUserByUsername(newUserData.getUsername());
            if (existingUserWithSameUsername.isPresent() && existingUserWithSameUsername.get().getId() != userToUpdate.getId()) {
                log.warn("Another User with username {} already exists", userToUpdate.getUsername());
                return Optional.empty();
            }
        }

        // only name, accountId, username, password and isActive fields can be modified for an existing User
        if (!ApiUtils.isNullOrEmpty(accountId)) {
            userToUpdate.setAccountId(accountId);
        }
        if (!ApiUtils.isNullOrEmpty(newUserData.getName())) {
            userToUpdate.setName(newUserData.getName());
        }
        if (!ApiUtils.isNullOrEmpty(newUserData.getUsername())) {
            userToUpdate.setUsername(newUserData.getUsername());
        }
        if (newUserData.getUserRoles() != null && !newUserData.getUserRoles().isEmpty()) {
            userToUpdate.setUserRoles(newUserData.getUserRoles());
        }
        userToUpdate.setIsActive(newUserData.getIsActive());
        repository.save(userToUpdate);
        return Optional.of(userToUpdate);
    }

    public Optional<User> updateWithOverwrite(String accountId, String id, User newUserData) {

        log.trace("Entering update() with {}", newUserData);

        Optional<User> existingUser = repository.findById(id);
        if (!existingUser.isPresent()) {
            log.warn("User {} not found", id);
            return Optional.empty();
        }

        User userToUpdate = existingUser.get();

        // Since username uniqueness cannot be enforced in DB, enforce it here
        if (newUserData.getUsername() != null) {
            if (!newUserData.getUsername().equalsIgnoreCase(userToUpdate.getUsername())) {
                Optional<User> existingUserWithSameUsername = getUserByUsername(newUserData.getUsername());
                if (existingUserWithSameUsername.isPresent() && existingUserWithSameUsername.get().getId() != userToUpdate.getId()) {
                    log.warn("Another User with username {} already exists", newUserData.getUsername());
                    return Optional.empty();
                }
            }
        }

        newUserData.setAccountId(accountId);
        newUserData.setId(id);
        repository.save(newUserData);
        return repository.findById(id);
    }

    public Optional<User> delete(String accountId, String id) {
        // Mark the user as inactive instead of physical deletion
        log.trace("Entering delete() with userId : {} and accountId: {}", id, accountId);

        Optional<User> existingUser = repository.findById(id);
        if (!existingUser.isPresent()) {
            log.warn("User {} not found", id);
            return Optional.empty();
        }

        User userToDelete = existingUser.get();
        userToDelete.setIsActive(false);
        repository.save(userToDelete);
        return Optional.of(userToDelete);
    }
}
