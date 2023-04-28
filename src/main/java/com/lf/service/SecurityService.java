package com.lf.service;

import com.lf.model.SecurityContext;

import java.util.Optional;

public interface SecurityService {

    Optional<SecurityContext> validate(String accessToken);

    Boolean userHasRoles(SecurityContext securityContext);

    Boolean isLoggedInUserActive(SecurityContext securityContext);

    String getAccountIdForLoggedInUser(SecurityContext securityContext);

    String getUserNameForUserId(SecurityContext securityContext);

}
