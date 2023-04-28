package com.lf.service;

import com.lf.model.User;
import com.lf.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> list(String accountId);

    Optional<User> getUserById(String userId);

    Optional<List<UserRole>> getUserRolesById(String id);

    Optional<User> save(String accountId, User user);

    Optional<User> update(String accountId, String id, User newUserData);

    Optional<User> updateWithOverwrite(String accountId, String id, User newUserData);

    Optional<User> delete(String accountId, String id);

    Optional<User> getUserByUsername(String username);
}
