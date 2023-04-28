package com.lf.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.lf.model.Configuration;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends CosmosRepository<Configuration, String> {

    Optional<Configuration> findByName(String name);

    @Query(value = "SELECT * FROM c WHERE c.account_id = @account_id")
    List<Configuration> findByAccountId(@Param("account_id") String account_id);
}
