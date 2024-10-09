package com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore;

import com.fruits.ecommerce.models.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserData implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }

    // These methods are currently unused but maintained for potential future use in API or UI
    // They provide access to additional user details that may be needed in future implementations

    @SuppressWarnings("unused")
    public Long getId() {
        return user.getId();
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return user.getEmail();
    }

    @SuppressWarnings("unused")
    public String getFirstName() {
        return user.getFirstName();
    }

    @SuppressWarnings("unused")
    public String getLastName() {
        return user.getLastName();
    }







}
