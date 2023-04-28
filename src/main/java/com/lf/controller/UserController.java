package com.lf.controller;

import com.lf.model.SecurityContext;
import com.lf.model.User;
import com.lf.model.UserRole;
import com.lf.util.PatternMatcher;
import com.lf.service.AccountService;
import com.lf.service.SecurityService;
import com.lf.service.UserService;
import com.lf.util.ApiUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/lf/accounts")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;


    @RequestMapping(value = "/{account_id}/users", method = RequestMethod.GET)
    @ApiOperation(value = "Get all Users for the given account id", response = User.class, responseContainer = "List", produces = "application/json")
    public ResponseEntity listUsers(@PathVariable("account_id") final String accountId,
                                   @RequestParam(value = "is-active", required = false) final Boolean isActive,
                                   @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to list all users");

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_USER.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check that account id is a valid uuid
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

        Iterable<User> users = (isActive != null) ? userService.list(accountId).stream()
                .filter(user -> user.getIsActive().equals(isActive.booleanValue()))
                .collect(Collectors.toList()) : userService.list(accountId);
        return new ResponseEntity<>(users, OK);
    }

    // :.+ is used in the case of query parameter use_name = true, this matches usernames with . extensions i.e. alpha@dlvr.com
    @RequestMapping(value = "/{account_id}/users/{id:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "Get User with given user id and account id", response = User.class, produces = "application/json")
    public ResponseEntity getById(@PathVariable("account_id") final String accountId,
                                  @PathVariable("id") final String id,
                                  @RequestParam(value = "use_name", required = false) boolean useName,
                                  @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to retrieve User by id");

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_USER.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check that account id is a valid uuid
        if (!accountId.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid account-id {}", accountId);
            return new ResponseEntity<>("Invalid account-id: " + accountId, BAD_REQUEST);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        // If looking up by name, make sure the UUID is valid
        if (!id.matches(PatternMatcher.UUID_PATTERN) && !useName) {
            log.warn("Invalid user-id {}", id);
            return new ResponseEntity<>("Invalid user-id: " + id, BAD_REQUEST);
        }

        if (!accountService.getAccountById(accountId).isPresent()) {
            log.warn("account with id {} does not exist", accountId);
            return new ResponseEntity<>("account with id: " + accountId + " does not exist", NOT_FOUND);
        }

        Optional<User> user = (useName) ? userService.getUserByUsername(id) : userService.getUserById(id);
        return user.isPresent() ? new ResponseEntity<>(user.get(), OK) : new ResponseEntity<>((useName) ? "user with name: " + id + " not found" : "user with id: " + id + " not found", NOT_FOUND);
    }

    @RequestMapping(value = "/{account_id}/users/{id}/userRoles", method = RequestMethod.GET)
    @ApiOperation(value = "Get User with given user id and account id and provide their roles", response = User.class, produces = "application/json")
    public ResponseEntity getUserRoles(@PathVariable("account_id") final String accountId,
                                       @PathVariable("id") final String id,
                                       @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to retrieve User by id");

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_USER.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check that account id is a valid uuid
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

        Optional<List<UserRole>> userRoles = userService.getUserRolesById(id);
        return userRoles.isPresent() ? new ResponseEntity<>(userRoles.get(), OK) : new ResponseEntity<>("user with id: " + id + " does not have roles associated with it", NOT_FOUND);
    }

    @RequestMapping(value = "/{account_id}/users", method = RequestMethod.POST)
    @ApiOperation(value = "Create User with given payload for the given account id", response = User.class, produces = "application/json")
    public ResponseEntity createUser(@RequestBody @Valid final User user,
                                     @PathVariable("account_id") final String accountId,
                                     @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to create the {}", user);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_USER.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check that account id is a valid uuid
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

        if (!ApiUtils.isNullOrEmpty(user.getUsername()) && !EmailValidator.getInstance().isValid(user.getUsername())) {
            return new ResponseEntity<>("username: " + user.getUsername() + "is not a valid email address", BAD_REQUEST);
        }

        user.setCreatedBy(securityService.getUserNameForUserId(xSecurityContext.get()));   // Set to logged in user

        if (!ApiUtils.isNullOrEmpty(user.getPassword())) {   // store password as BCrypt
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }

        Optional<User> newUser = userService.save(accountId, user);
        return newUser.isPresent() ? new ResponseEntity<>(newUser.get(), CREATED) : new ResponseEntity<>("user with username: " + user.getUsername() + " already exists", CONFLICT);
    }

    @RequestMapping(value = "/{account_id}/users/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update User with given id", response = User.class, produces = "application/json")
    public ResponseEntity updateUser(@RequestBody final User user, @PathVariable("account_id") final String accountId,
                                     @PathVariable("id") final String id,
                                     @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to update the {}", user);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_USER.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check that account id is a valid uuid
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

        if (!ApiUtils.isNullOrEmpty(user.getUsername()) && !EmailValidator.getInstance().isValid(user.getUsername())) {
            return new ResponseEntity<>("username: " + user.getUsername() + "is not a valid email address", BAD_REQUEST);
        }

        if (!ApiUtils.isNullOrEmpty(user.getPassword())) {   // store password as BCrypt
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }

        Optional<User> updatedUser = userService.updateWithOverwrite(accountId,id, user);
        return updatedUser.isPresent() ? new ResponseEntity<>(updatedUser.get(), OK) : new ResponseEntity<>("user with id: " + id + " not found", NOT_FOUND);
    }

    @RequestMapping(value = "/{account_id}/users/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete User with given id", produces = "application/json")
    public ResponseEntity deleteUser(@PathVariable("account_id") final String accountId,
                                     @PathVariable("id") final String id,
                                     @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        // Mark the user as inactive instead of physical deletion
        log.debug("Received request to delete user with id {}", id);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_USER.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // check that account id is a valid uuid
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

        Optional<User> deletedUser = userService.delete(accountId, id);
        return deletedUser.isPresent() ? new ResponseEntity<>(deletedUser.get(), OK) : new ResponseEntity<>("user with id: " + id + " not found", NOT_FOUND);
    }

}
