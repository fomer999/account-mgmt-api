package com.lf.service;

import com.lf.model.User;
import com.lf.model.UserRole;
import com.lf.model.HTTPEnums;
import com.lf.model.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Service
public class SecurityServiceImpl implements SecurityService {
    private static final Logger log = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Value("${dlvr.oauth.server.baseurl}")
    private String oauthServerBaseEndpoint;

    public Optional<SecurityContext> validate(final String accessToken) {
        log.trace("getAccessToken(), gather the access token from the OAuth Server.");
        ResponseEntity validateAccessToken = new RestTemplate().exchange(oauthServerBaseEndpoint + HTTPEnums.OAuthServerEndpoints.VALIDATE.getValue() + "?access_token=" + accessToken, HttpMethod.POST, null, Void.class);
        HttpHeaders httpHeaders = validateAccessToken.getHeaders();
        List<String> securityContext = httpHeaders.get("X-Security-Context");
        return (securityContext != null && !securityContext.isEmpty()) ? Optional.of(new SecurityContext(securityContext.get(0))) : Optional.empty();
    }

    public Boolean userHasRoles(final SecurityContext securityContext) {
        log.trace("hasRoles() securityContext {}", securityContext);
        // Use the SC to process the roles and see if a user can interact with this endpoint
        Optional<User> user = userService.getUserById(securityContext.getUserId());
        if(!user.isPresent()) {
            log.warn("The user should be present in order to process the roles, {}", user);
            return false;
        }

        // Does the user have the correct roles to access this endpoint?
        Boolean hasRole = false;
        List<UserRole> getUserRoles = user.get().getUserRoles();
        for(UserRole role : getUserRoles) {
            if(securityContext.hasRole(role.value())) {
                log.info("User {} has role {}", user.get().getUsername(), role.toString());
                hasRole = true;
            }
        }
        return hasRole;
    }

    public Boolean isLoggedInUserActive(final SecurityContext securityContext) {
        log.trace("isLoggedInUserActive() securityContext {}", securityContext);
        Optional<User> currentUser = userService.getUserById(securityContext.getUserId());
        if (currentUser.isPresent() && currentUser.get().getIsActive())
            return true;
        return false;
    }

    public String getAccountIdForLoggedInUser(final SecurityContext securityContext) {
        log.trace("getAccountIdForLoggedInUser() securityContext {}", securityContext);
        Optional<User> currentUser = userService.getUserById(securityContext.getUserId());
        if(!currentUser.isPresent()) {
            log.warn("The user should be present in order to process getting the accountId, {}", currentUser);
            return "";
        }
        return currentUser.get().getAccountId();
    }

    public String getUserNameForUserId(final SecurityContext securityContext) {
        log.trace("getAccountIdForLoggedInUser() securityContext {}", securityContext);
        Optional<User> currentUser = userService.getUserById(securityContext.getUserId());
        if(!currentUser.isPresent()) {
            log.warn("The user should be present in order to process getting the accountId, {}", currentUser);
            return "";
        }
        return currentUser.get().getName();
    }
}
