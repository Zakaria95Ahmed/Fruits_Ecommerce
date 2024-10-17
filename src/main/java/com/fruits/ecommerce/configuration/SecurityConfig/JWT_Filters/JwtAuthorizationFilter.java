package com.fruits.ecommerce.configuration.SecurityConfig.JWT_Filters;

import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.SecurityProperties;
import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.UserData;
import com.fruits.ecommerce.models.entities.User;
import com.fruits.ecommerce.services.Interfaces.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;
    private final IUserService userService;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider,
                                  SecurityProperties securityProperties,
                                  @Lazy IUserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JwtAuthorizationFilter: Processing request for URI: {}", request.getRequestURI());
        if (request.getMethod().equalsIgnoreCase(securityProperties.getOptionsHttpMethod())) {
            response.setStatus(OK.value());
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(securityProperties.getTokenPrefix())) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(securityProperties.getTokenPrefix().length());
            String username = jwtTokenProvider.getSubject(token);
            if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Optional<User> userOptional = userService.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    UserData userPrincipal = new UserData(user);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("User not found for username: {}", username);
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}