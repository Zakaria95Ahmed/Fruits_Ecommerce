package com.fruits.ecommerce.Services.Implementations;

import com.fruits.ecommerce.Configuration.SecurityConfig.ExtraServices.EmailService;
import com.fruits.ecommerce.Configuration.SecurityConfig.ExtraServices.LoginAttemptService;
import com.fruits.ecommerce.Configuration.SecurityConfig.JWT_Filters.JWTTokenProvider;
import com.fruits.ecommerce.Configuration.SecurityConfig.SecurityCore.UserData;
import com.fruits.ecommerce.Exceptions.ExceptionsDomain.*;
import com.fruits.ecommerce.Models.DTOs.AuthResponseDTO;
import com.fruits.ecommerce.Models.DTOs.LoginRequestDTO;
import com.fruits.ecommerce.Models.DTOs.UserDTO;
import com.fruits.ecommerce.Models.Entities.Role;
import com.fruits.ecommerce.Models.Entities.User;
import com.fruits.ecommerce.Models.Enum.RoleType;
import com.fruits.ecommerce.Models.Mappers.UserMapper;
import com.fruits.ecommerce.Repository.RoleRepository;
import com.fruits.ecommerce.Repository.UserRepository;
import com.fruits.ecommerce.Services.Interfaces.IUserService;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthImplementation implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private final Validator validator;

    /**
     * Registering a new user with the provided details.
     *
     * @param userDTO --->   The new user's details.
     * @return UserDTO  --->  with the registered user's information.
     * @throws UsernameExistException   --->  if the username already exists.
     * @throws EmailExistException      --->  if the email already exists.
     * @throws InvalidRoleException     --->  if an invalid role is provided.
     * @throws InvalidUserDataException ---> if the user data is invalid.
     *                                  //@throws MessagingException        ---> if the email fails to send.
     */


    @Override
    public UserDTO register(UserDTO userDTO) {
        log.info("Starting registration process for user: {}", userDTO.getUsername());

        try {
            validateNewUser(userDTO);
            Set<Role> roles = getRolesFromNames(userDTO.getRoles());
            User user = createUser(userDTO, roles);
            user = userRepository.save(user);

            sendRegistrationEmailAsync(user, userDTO.getPassword());

            log.info("User registered successfully: {}", user.getUsername());
            return userMapper.toDTO(user);
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            throw e;
        }
    }

    private User createUser(UserDTO userDTO, Set<Role> roles) {
        User user = userMapper.toEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setNotLocked(true);
        return user;
    }

    @Async
    public void sendRegistrationEmailAsync(User user, String password) {
        sendRegistrationEmail(user, password);
    }

    /**
     * User login and data validation.
     *
     * @param loginRequest --->  The login details.
     * @return AuthResponseDTO          --->  with the JWT Token and user information.
     * @throws AuthenticationException --->  if authentication fails.
     * @throws UserNotFoundException   --->   if the user is not found.
     * @throws AccountLockedException  --->  if the account is locked.
     * @throws BadCredentialsException --->  if the credentials are incorrect.
     */
    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequest) throws AuthenticationException,
            UserNotFoundException, AccountLockedException, BadCredentialsException {

        String identifier = StringUtils.hasText(loginRequest.getUsername()) ?
                loginRequest.getUsername() : loginRequest.getEmail();

        // Find the user
        User user = getUserByIdentifier(identifier);
        // Check if the account is locked
        if (!user.isNotLocked()) {
            throw new AccountLockedException("Your account has been locked due to multiple failed login attempts. " +
                    "Please contact support.");
        }

        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword())
            );

            // If authentication is successful, reset failed attempts and update last login
            loginAttemptService.evictUserFromLoginAttemptCache(user.getId());
            user.updateLastLogin();
            user.setNotLocked(true);
            userRepository.save(user);

            // Create JWT Token
            String token = jwtTokenProvider.generateJwtToken((UserData) authentication.getPrincipal());

            // Return authentication response
            return new AuthResponseDTO(token, userMapper.toDTO(user));

        } catch (AuthenticationException ex) {
            // Add failed login attempt
            log.error("Login failed for {}: {}", identifier, ex.getMessage());
            loginAttemptService.addUserToLoginAttemptCache(user.getId());

            // Check if max attempts exceeded after this attempt
            if (loginAttemptService.hasExceededMaxAttempts(user.getId())) {
                user.setNotLocked(false);
                userRepository.save(user);
                throw new AccountLockedException("Your account has been locked due to multiple failed login attempts. " +
                        "Please try again later or Contact Technical Support-Team.");
            }

            throw new BadCredentialsException("In-Correct Credentials");
        }
    }


    @Override
    public void lockUser(String identifier) {
        User user = getUserByIdentifier(identifier);

        if (user.isNotLocked()) {
            user.setNotLocked(false);
            loginAttemptService.addUserToLoginAttemptCache(user.getId());
            userRepository.save(user);
            sendAccountLockedEmailAsync(user);
            log.info("User account locked: {}", identifier);
        } else {
            log.info("User account already locked: {}", identifier);
            throw new AccountLockedException("User account is already locked: " + identifier);
        }
    }

    @Override
    public void unlockUser(String identifier) {
        User user = getUserByIdentifier(identifier);

        if (!user.isNotLocked()) {
            user.setNotLocked(true);
            loginAttemptService.evictUserFromLoginAttemptCache(user.getId());
            userRepository.save(user);
            sendAccountUnlockedEmailAsync(user);
            log.info("User account unlocked: {}", identifier);
        } else {
            log.info("User account already unlocked: {}", identifier);
            throw new UnLockedAccountException("User account is already unlocked: " + identifier);
        }
    }

    @Async
    public void sendAccountLockedEmailAsync(User user) {
        sendAccountLockedEmail(user);
    }

    @Async
    public void sendAccountUnlockedEmailAsync(User user) {
        sendAccountUnlockedEmail(user);
    }

    private void sendAccountLockedEmail(User user) {
        try {
            emailService.sendAccountLockedEmail(user.getFirstName(), user.getEmail());
            log.info("Account locked email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send account locked email to: {}. Error: {}", user.getEmail(), e.getMessage());
            // Optionally handle the exception (e.g., retry or log)
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendAccountUnlockedEmail(User user) {
        try {
            emailService.sendAccountUnlockedEmail(user.getFirstName(), user.getEmail());
            log.info("Account unlocked email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send account unlocked email to: {}. Error: {}", user.getEmail(), e.getMessage());
            // Optionally handle the exception
            throw new RuntimeException(e.getMessage());
        }
    }


    private User getUserByIdentifier(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UserNotFoundException("In-Valid UserAccount with: " + identifier));
    }

    // Private-Methods

    /**
     * Validate the new user's data.
     *
     * @param userDTO --->   The new user's information.
     * @throws UsernameExistException   --->  If the username already exists.
     * @throws EmailExistException      ---> If the email already exists.
     * @throws InvalidUserDataException ---> If the user data is invalid.
     */

    // This method will validate the new user data before saving it
    private void validateNewUser(UserDTO userDTO) {
        if (!StringUtils.hasText(userDTO.getPassword())) {
            throw new InvalidUserDataException("Password cannot be empty or null.");
        }

        if (userDTO.getFirstName() == null || userDTO.getLastName() == null ||
                userDTO.getUsername() == null || userDTO.getEmail() == null) {
            throw new InvalidUserDataException("All required fields must be provided.");
        }

        if (!isValidEmail(userDTO.getEmail())) {
            throw new InvalidUserDataException("Invalid email format.");
        }

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameExistException("Username already exists.");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailExistException("Email already exists.");
        }
        // Validate the user DTO using the validation annotations
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            // Throw an InvalidUserDataException if there are any validation errors
            StringBuilder errorMessage = new StringBuilder("Invalid user data: ");
            for (ConstraintViolation<UserDTO> violation : violations) {
                errorMessage.append(violation.getMessage()).append(", ");
            }
            throw new InvalidUserDataException(errorMessage.toString().trim());
        }
    }

    // Helper method to validate the email address
    private boolean isValidEmail(String email) {
        // Regular expression to validate email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Retrieve roles from names.
     *
     * @param roleNames ---->  A set of role names.
     * @return ---->    Set<Role> A set of roles.
     * @throws InvalidRoleException ---->   If an invalid role is found.
     */
    private Set<Role> getRolesFromNames(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                try {
                    RoleType roleType = RoleType.valueOf(roleName.toUpperCase());
                    Role role = roleRepository.findByName(roleType)
                            .orElseThrow(() -> new InvalidRoleException("Role not found: " + roleName));
                    roles.add(role);
                } catch (IllegalArgumentException ex) {
                    throw new InvalidRoleException("Invalid Role: " + roleName);
                }
            }
        } else {
            //Assign the default Role
            Role defaultRole = roleRepository.findByName(RoleType.VISITOR)
                    .orElseThrow(() -> new InvalidRoleException("Default Role not found."));
            roles.add(defaultRole);
        }
        return roles;
    }



    private void sendRegistrationEmail(User user, String password) {
        try {
            emailService.sendNewPasswordEmail(user.getFirstName(), user.getUsername(), password, user.getEmail());
            log.info("Registration email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send registration email to: {}. Error: {}", user.getEmail(), e.getMessage());
            // We're not throwing an exception here to avoid rolling back the registration process
            throw new RuntimeException("Failed to send registration email to:", e.getCause());
        }
    }
    /**
     * توليد كلمة مرور عشوائية .Generating a Random Password
     *
     * @return Generated-Password
     */
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    @Override
    public void addRoleToUser(Long userId, RoleType roleType) throws UserNotFoundException, InvalidRoleException {
        if (roleType == null) {
            throw new InvalidRoleException("Role type cannot be null");
        }

        // Fetch user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Fetch role by RoleType
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new InvalidRoleException("Role not found in database: " + roleType));

        // Check if user already has the role
        if (user.getRoles().contains(role)) {
            log.warn("User {} already has role {}", userId, roleType);
            return;
        }

        // Add role to user's set of roles
        user.getRoles().add(role);

        // Save user
        userRepository.save(user);

        // Log the action
        log.info("Role {} added to user {}", roleType, userId);
    }
    @Override
    public void removeRoleFromUser(Long userId, RoleType roleType) throws UserNotFoundException, InvalidRoleException {
        // Fetch user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Fetch role by RoleType
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new InvalidRoleException("Role not found with name: " + roleType));

        // Remove role from user's set of roles
        boolean removed = user.getRoles().remove(role);

        if (removed) {
            // Save user if role was removed
            userRepository.save(user);
            // Log the action
            log.info("Role {} removed from user {}", roleType, userId);
        } else {
            // Log that the user did not have the role
            log.warn("User {} does not have role {}", userId, roleType);
            // Optionally, you can throw an exception if this is considered an error
             throw new RuntimeException("User does not have role: " + roleType);
        }
    }

}