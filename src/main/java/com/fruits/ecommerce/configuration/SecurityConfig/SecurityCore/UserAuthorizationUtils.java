package com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("userAuthorizationUtils")
public class UserAuthorizationUtils {


    public boolean hasUserId(Authentication authentication, Long userId) {
        log.info("Checking authorization for user ID: {}", userId);
        log.info("Authentication: {}", authentication);
        log.info("Principal: {}", authentication.getPrincipal());

        if (authentication.getPrincipal() instanceof UserData) {
            UserData userData = (UserData) authentication.getPrincipal();
            log.info("UserData ID: {}", userData.getId());
            boolean hasPermission = userData.getId().equals(userId);
            log.info("Has permission: {}", hasPermission);
            return hasPermission;
        } else {
            log.warn("Principal is not an instance of UserData. Type: {}",
                    authentication.getPrincipal().getClass().getName());
            return false;
        }
    }
}