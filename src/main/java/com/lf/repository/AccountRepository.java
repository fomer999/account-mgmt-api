package com.lf.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.lf.model.Account;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends CosmosRepository<Account, String> {

    Optional<Account> findByName(String name);

    @Query(value = "SELECT * FROM c WHERE c.parent_account_id = @parent_account_id")
    List<Account> findByParentAccountId(@Param("parent_account_id") String parent_account_id);
}
