package com.lf.service;

import com.lf.model.Configuration;
import com.lf.repository.ConfigurationRepository;
import com.lf.util.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    private ConfigurationRepository repository;

    public Optional<List<Configuration>> list(String accountId) {
        log.trace("Entering list()");
        return Optional.of(repository.findByAccountId(accountId));
    }

    public Optional<Configuration> getConfigurationById(String id) {
        log.trace("Entering getConfigurationById() with configurationId : {}", id);
        return repository.findById(id);
    }

    public Optional<Configuration> save(String accountId, Configuration configuration) {
        log.trace("Entering save() with {}", configuration);

        configuration.setConfigurationEnabled(true);

        if (!ApiUtils.isNullOrEmpty(accountId)) {
            configuration.setAccountId(accountId);
        }
        configuration.setCreatedDate(new Date());
        configuration.setModifiedDate(new Date());
        repository.save(configuration);

        return Optional.of(configuration);
    }

    public Optional<Configuration> update(String accountId, String name, Configuration newConfigurationData) {
        log.trace("Entering update() with {}", newConfigurationData);

        Optional<Configuration> existingCurrentConfiguration = repository.findByName(name);
        if (existingCurrentConfiguration.isPresent()) {
            log.warn("Configuration {} not found", name);
            return Optional.empty();
        }

        // add new config entry
        newConfigurationData.setAccountId(accountId);
        newConfigurationData.setCreatedBy(existingCurrentConfiguration.get().getCreatedBy());
        newConfigurationData.setCreatedDate(existingCurrentConfiguration.get().getCreatedDate());
        newConfigurationData.setModifiedDate(new Date());
        newConfigurationData.setName(existingCurrentConfiguration.get().getName()); // ensure that the name field is not overwritten on config update

        repository.save(newConfigurationData);
        return Optional.of(newConfigurationData);
    }

    public Optional<Configuration> delete(String id, Boolean useName) {
        // Mark the Configuration as inactive instead of physical deletion
        log.trace("Entering delete() with configuration : {} using name: {}", id, useName);

        Optional<Configuration> existingConfiguration = (useName) ? repository.findByName(id) : repository.findById(id);
        if (!existingConfiguration.isPresent()) {
            log.warn("Configuration {} not found", id);
            return Optional.empty();
        }

        Configuration configurationToDelete = existingConfiguration.get();
        configurationToDelete.setConfigurationEnabled(false);
        repository.save(configurationToDelete);
        return (useName) ? repository.findByName(id) : repository.findById(id);
    }
}
