package com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityProperties {
    private long expirationTime;
    private String tokenPrefix;
    private String tokenCannotBeVerified;
    private String issuer;
    private String audience;
    private String authorities;
    private String forbiddenMessage;
    private String accessDeniedMessage;
    private String optionsHttpMethod;
}