package com.lf.service;

import com.lf.model.Configuration;

import java.util.List;
import java.util.Optional;


public interface ConfigurationService {

    Optional<List<Configuration>> list(String accountId);

    Optional<Configuration> getConfigurationById(String id);

    Optional<Configuration> save(String accountId, Configuration configuration);

    Optional<Configuration> update(String accountId, String id, Configuration newConfigurationData);

    Optional<Configuration> delete(String id, Boolean useName);
}
