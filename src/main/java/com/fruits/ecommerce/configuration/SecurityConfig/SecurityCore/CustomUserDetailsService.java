package com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore;

import com.fruits.ecommerce.models.entities.User;
import com.fruits.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        //Allow login using username or email
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        "User not found with username or email: " + identifier)));
        return new UserData(user);
    }

}
