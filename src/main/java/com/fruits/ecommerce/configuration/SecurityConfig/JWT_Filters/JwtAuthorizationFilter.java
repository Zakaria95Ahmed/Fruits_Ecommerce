package com.fruits.ecommerce.configuration.SecurityConfig.JWT_Filters;

import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;
//    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider,
                                  SecurityProperties securityProperties
//                                  CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
//        this.customUserDetailsService=customUserDetailsService;
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
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
////////////////////////////////////////////////
//@Slf4j
//@Component
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//
//    private final JWTTokenProvider jwtTokenProvider;
//    private final SecurityProperties securityProperties;
//    private final CustomUserDetailsService customUserDetailsService;
//
//    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider,
//                                  SecurityProperties securityProperties,
//                                  CustomUserDetailsService customUserDetailsService) {
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.securityProperties = securityProperties;
//        this.customUserDetailsService = customUserDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//        log.info("JwtAuthorizationFilter: Processing request for URI: {}", request.getRequestURI());
//
//        String authorizationHeader = request.getHeader(AUTHORIZATION);
//        if (authorizationHeader == null || !authorizationHeader.startsWith(securityProperties.getTokenPrefix())) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authorizationHeader.substring(securityProperties.getTokenPrefix().length());
//        String username = jwtTokenProvider.getSubject(token);
//
//        if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
//            // استرجاع UserData من خلال CustomUserDetailsService
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
//
//            // إعداد Authentication باستخدام UserDetails وليس username
//            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else {
//            SecurityContextHolder.clearContext();
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
