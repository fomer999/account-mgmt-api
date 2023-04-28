package com.lf.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.lf.model.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CosmosRepository<User, String> {

    Optional<User> findByName(String name);

    @Query(value = "SELECT * FROM c WHERE c.username = @username")
    List<User> findByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM c WHERE c.account_id = @account_id")
    List<User> findByAccountId(@Param("account_id") String account_id);
}