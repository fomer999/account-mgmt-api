package com.lf.controller;

import com.lf.model.Account;
import com.lf.model.UserRole;
import com.lf.service.AccountService;
import com.lf.service.SecurityService;
import com.lf.model.SecurityContext;
import com.lf.util.PatternMatcher;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/lf")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/accounts/{account_id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get account with given id", response = Account.class, produces = "application/json")
    public ResponseEntity getById(@PathVariable("account_id") final String id,
                                  @RequestParam(value = "use-name", required = false) final boolean useName,
                                  @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to retrieve account by id: " + id);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // If looking up by id make sure the UUID matches
        if (!id.matches(PatternMatcher.UUID_PATTERN) && !useName) {
            log.warn("Invalid id {}", id);
            return new ResponseEntity<>("Invalid id: " + id, BAD_REQUEST);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_ACCOUNT.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        // If by name, then first get the account if present, if so use the account id found by querying the name for the next 403 check
        Optional<Account> account = (useName) ? accountService.getAccountByName(id) : accountService.getAccountById(id);
        if(!account.isPresent()) {
            log.error((useName) ? "There was not account found using name :" + id  : " There was no account found using id: " + id);
            new ResponseEntity<>((useName) ?  "account with name : " + id + " node found" : "account with id: " + id + " not found", NOT_FOUND);
        }

        return account.isPresent() ? new ResponseEntity<>(account.get(), OK) : new ResponseEntity<>((useName) ?  "account with name : " + id + " node found" : "account with id: " + id + " not found", NOT_FOUND);
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    @ApiOperation(value = "Create Account with given payload", response = Account.class, produces = "application/json")
    public ResponseEntity createAccount(@RequestBody @Valid final Account account,
                                        @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to create the {}", account);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_ACCOUNT.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        account.setCreatedBy(securityService.getUserNameForUserId(xSecurityContext.get()));   // Set to logged in user
        Optional<Account> savedAccount = accountService.save(account);
        return savedAccount.isPresent() ? new ResponseEntity<>(savedAccount.get(), CREATED) : new ResponseEntity<>("Something went wrong", BAD_REQUEST);
    }

    @RequestMapping(value = "/accounts/{account_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update account with given id", response = Account.class, produces = "application/json")
    public ResponseEntity updateAccount(@RequestBody final Account newAccountData,
                                                 @PathVariable("account_id") final String id,
                                                 @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to update the {}", newAccountData);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // If looking up by id make sure the UUID matches
        if (!id.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid id {}", id);
            return new ResponseEntity<>("Invalid id: " + id, BAD_REQUEST);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_ACCOUNT.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        Optional<Account> updatedAccount = accountService.update(id, newAccountData);
        return updatedAccount.isPresent() ? new ResponseEntity<>(updatedAccount.get(), OK) : new ResponseEntity<>("account with id: " + id + " not found", NOT_FOUND);
    }

    @RequestMapping(value = "/accounts/{account_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete account with given id", produces = "application/json")
    public ResponseEntity deleteAccount(@PathVariable("account_id") final String id,
                                        @RequestHeader("X-Security-Context") final String xSecurityHeader) {
        log.debug("Received request to delete account with id {}", id);

        // Build the SecurityContext object
        Optional<SecurityContext> xSecurityContext = Optional.of(new SecurityContext(xSecurityHeader));
        if(!xSecurityContext.isPresent()) {
            log.warn("Error constructing xSecurityContext");
            return new ResponseEntity<>("Error constructing xSecurityContext", FORBIDDEN);
        }

        // If looking up by id make sure the UUID matches
        if (!id.matches(PatternMatcher.UUID_PATTERN)) {
            log.warn("Invalid id {}", id);
            return new ResponseEntity<>("Invalid id: " + id, BAD_REQUEST);
        }

        // Check access role
        if(!xSecurityContext.get().hasRole(UserRole.ROLE_RETRIEVE_ACCOUNT.value())) {
            log.warn("User {} account {}, does not have the correct role to interact with this endpoint.", xSecurityContext.get().getUserId(), xSecurityContext.get().getAccountId());
            return new ResponseEntity<>("User: " +  xSecurityContext.get().getUserId() + "account: " +  xSecurityContext.get().getAccountId() + " does not have the correct role to interact with this endpoint.", FORBIDDEN);
        }

        // if Logged-in user is inactive, return 403
        if (!securityService.isLoggedInUserActive(xSecurityContext.get())) {
            log.warn("Attempt to access data when the logged on user is inactive");
            return new ResponseEntity<>("Attempt to access data when the logged on user is inactive", FORBIDDEN);
        }

        Optional<Account> deletedAccount = accountService.delete(id);
        return deletedAccount.isPresent() ? new ResponseEntity<>(deletedAccount.get(), OK) : new ResponseEntity<>("account with id: " + id + " not found", NOT_FOUND);
    }
}
