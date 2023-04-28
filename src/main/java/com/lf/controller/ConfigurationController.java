package com.lf.controller;

import com.lf.model.Configuration;
import com.lf.model.SecurityContext;
import com.lf.model.UserRole;
import com.lf.util.PatternMatcher;
import com.lf.util.ApiUtils;
import com.lf.service.AccountService;
import com.lf.service.ConfigurationService;
import com.lf.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/lf/accounts")
public class ConfigurationController {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(value = "/{account_id}/configurations", method = RequestMethod.GET)
    @ApiOperation(value = "Get all configurations for given account id", response = Configuration.class, responseContainer = "List", produces = "application/json")
    public ResponseEntity listConfigurations(
            @PathVariable("account_id") final String accountId,
            @RequestParam(value = "is-active", required = false) final Boolean isActive,
            @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug( "Received request to list all configurations");

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_CONFIGURATION.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check account id is a valid uuid
        if (!accountId.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid account-id {}", accountId);
            return new ResponseEntity<>("Invalid account-id: " + accountId, BAD_REQUEST);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        if (!accountService.getAccountById(accountId).isPresent()) {
            log.warn("account with id {} does not exist", accountId);
            return new ResponseEntity<>("account with id: " + accountId + " does not exist", NOT_FOUND);
        }

        Optional<List<Configuration>> configurationList = configurationService.list(accountId);
        if(!configurationList.isPresent()) {
            log.warn("configuration list is not present", accountId);
            return new ResponseEntity<>("configuration id list is not present for account-id:" + accountId, INTERNAL_SERVER_ERROR);
        }

        List<Configuration> configurations = (isActive != null) ? configurationList.get().stream()
                .filter(config -> config.getConfigurationEnabled().equals(isActive.booleanValue()))
                .collect(Collectors.toList()) : configurationList.get();
        return new ResponseEntity<>(configurations, OK);
    }

    @RequestMapping(value = "/{account_id}/configurations/{configuration_id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get configuration with given id for the given account id", response = Configuration.class, produces = "application/json")
    public ResponseEntity getById(
            @PathVariable("account_id") final String accountId,
            @PathVariable("configuration_id") final String id,
            @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to retrieve configuration by id: " + id);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_CONFIGURATION.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check account id is a valid uuid
        if (!accountId.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid account-id {}", accountId);
            return new ResponseEntity<>("Invalid account-id: " + accountId, BAD_REQUEST);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        if (!accountService.getAccountById(accountId).isPresent()) {
            log.warn("account with id {} does not exist", accountId);
            return new ResponseEntity<>("account with id: " + accountId + " does not exist", NOT_FOUND);
        }

        Optional<Configuration> configuration = configurationService.getConfigurationById(id);
        return configuration.isPresent() ? new ResponseEntity<>(configuration.get(), OK) : new ResponseEntity<>("configuration with id: " + id + " not found", NOT_FOUND);
    }

    @RequestMapping(value = "/{account_id}/configurations", method = RequestMethod.POST)
    @ApiOperation(value = "Create configuration with given payload for the given account id", response = Configuration.class, produces = "application/json")
    public ResponseEntity createConfiguration(
            @RequestBody @Valid final Configuration configuration,
            @PathVariable("account_id") final String accountId,
            @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to create configuration {}", configuration);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_CONFIGURATION.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check account id is a valid uuid
        if (!accountId.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid account-id {}", accountId);
            return new ResponseEntity<>("Invalid account-id: " + accountId, BAD_REQUEST);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        //Cosmos does not auto-generate ids - set id value if it is not supplied in payload
        if (ApiUtils.isNullOrEmpty(configuration.getId())) {
            configuration.setId(UUID.randomUUID().toString());
        }

        if (!accountService.getAccountById(accountId).isPresent()) {
            log.warn("account with id {} does not exist", accountId);
            return new ResponseEntity<>("account with id: " + accountId + " does not exist", NOT_FOUND);
        }

        configuration.setCreatedBy(securityService.getUserNameForUserId(xSecurityContext.get()));     // Set to logged in user
        configuration.setModifiedBy(securityService.getUserNameForUserId(xSecurityContext.get()));    // Set to logged in user

        Optional<Configuration> newConfig = configurationService.save(accountId, configuration);
        return newConfig.map(cfg -> {
            return new ResponseEntity<>(cfg, CREATED);
        }).orElse(new ResponseEntity<>(BAD_REQUEST));
    }

    @RequestMapping(value = "/{account_id}/configurations/{configuration_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update configuration with given name", response = Configuration.class, produces = "application/json")
    public ResponseEntity updateConfiguration(
            @RequestBody final Configuration configuration,
            @PathVariable("account_id") final String accountId,
            @PathVariable("configuration_id") final String configurationId,
            @RequestParam(value = "is-rollback", required = false) final boolean isRollback,
            @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to update the {}", configuration);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_CONFIGURATION.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check account id is a valid uuid
        if (!accountId.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid account-id {}", accountId);
            return new ResponseEntity<>("Invalid account-id: " + accountId, BAD_REQUEST);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        if (!accountService.getAccountById(accountId).isPresent()) {
            log.warn("account with id {} does not exist", accountId);
            return new ResponseEntity<>("account with id: " + accountId + " does not exist", NOT_FOUND);
        }

        configuration.setModifiedBy(securityService.getUserNameForUserId(xSecurityContext.get()));   // Set to logged in user
        Optional<Configuration> updatedConfig = configurationService.update(accountId, configurationId, configuration);
        if (updatedConfig.isPresent()) {
            return new ResponseEntity<>(updatedConfig.get(), OK);
        } else {
            return new ResponseEntity<>("Configuration with id: " + configurationId + " does not exist", NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{account_id}/configurations/{configuration_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete configuration with given id", response = Configuration.class, produces = "application/json")
    public ResponseEntity deleteConfiguration(
            @PathVariable("account_id") final String accountId,
            @PathVariable("configuration_id") final String configurationId,
            @RequestParam(value = "use-name", required = false) final boolean useName,
            @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to delete configuration with id {}", configurationId);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_CONFIGURATION.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check account id is a valid uuid
        if (!accountId.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid account-id {}", accountId);
            return new ResponseEntity<>("Invalid account-id: " + accountId, BAD_REQUEST);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        if (!accountService.getAccountById(accountId).isPresent()) {
            log.warn("account with id {} does not exist", accountId);
            return new ResponseEntity<>("account with id: " + accountId + " does not exist", NOT_FOUND);
        }

        Optional<Configuration> deletedConfig = configurationService.delete(configurationId, useName);
        return deletedConfig.map(cfg -> {

            return new ResponseEntity<>(cfg, OK);
        }).orElse(new ResponseEntity<>(NOT_FOUND));
    }
}
