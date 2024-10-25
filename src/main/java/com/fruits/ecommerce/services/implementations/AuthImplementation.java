package com.fruits.ecommerce.services.implementations;

import com.fruits.ecommerce.configuration.CreateUser;
import com.fruits.ecommerce.configuration.SecurityConfig.JWT_Filters.JWTTokenProvider;
import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.UserData;
import com.fruits.ecommerce.exceptions.exceptionsDomain.users.*;
import com.fruits.ecommerce.models.dtos.AuthResponseDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.entities.Address;
import com.fruits.ecommerce.models.entities.Customer;
import com.fruits.ecommerce.models.entities.Role;
import com.fruits.ecommerce.models.entities.User;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.models.mappers.UserMapper;
import com.fruits.ecommerce.repository.CustomerRepository;
import com.fruits.ecommerce.repository.RoleRepository;
import com.fruits.ecommerce.repository.UserRepository;
import com.fruits.ecommerce.services.Interfaces.UserService;
import com.fruits.ecommerce.services.Utils.EmailService;
import com.fruits.ecommerce.services.Utils.LoginAttemptService;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthImplementation implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CustomerRepository customerRepository; // Injected CustomerRepository
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private final Validator validator;

    private User createUser(@Validated(CreateUser.class) UserDTO userDTO, Set<Role> roles) {
        User user = userMapper.toEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setLocked(false);
        return user;
    }


    @Async
    public void sendRegistrationEmailAsync(User user, String password) {
        sendRegistrationEmail(user, password);
    }


    @Override
    public UserDTO register(HttpHeaders headers) {
        log.info("Starting secure registration process");

        // Validate headers first
        validateRegistrationHeaders(headers);

        // Extract and convert registration data
        UserDTO userDTO = extractRegistrationData(headers);

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

    @Override
    public AuthResponseDTO login(HttpHeaders headers) {
        // Validate headers first
        validateLoginHeaders(headers);

        // Extract credentials
        String identifier = headers.getFirst("X-Auth-Username"); // maybe username or email
        String password = headers.getFirst("X-Auth-Password");

        // Find the user by username or email
        User user = getUserByIdentifierOrEmail(identifier);

        // Check if account is locked
        if (user.isLocked()) {
            throw new AccountLockedException("Your account has been locked due to multiple failed login attempts.");
        }

        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );

            // Reset failed attempts and update last login
            loginAttemptService.evictUserFromLoginAttemptCache(user.getId());
            user.updateLastLogin();
            user.setLocked(false);
            userRepository.save(user);

            // Generate JWT Token
            String token = jwtTokenProvider.generateJwtToken((UserData) authentication.getPrincipal());

            return new AuthResponseDTO(token, userMapper.toDTO(user));

        } catch (AuthenticationException ex) {
            handleFailedLogin(user, identifier, ex);
            throw ex;
        }
    }

    //  Add a new method to search for a user using either username or email
    private User getUserByIdentifierOrEmail(String identifier) {
        //  Use Optional to search for the user
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found with identifier: " + identifier)));
        return user;
    }


    // Private methods for header validation and data extraction
    private void validateRegistrationHeaders(HttpHeaders headers) {
        List<String> requiredHeaders = Arrays.asList(
                "X-Register-Username",
                "X-Register-Password",
                "X-Register-Email",
                "X-Register-FirstName",
                "X-Register-LastName",
                "X-Register-Address"
        );

        List<String> missingHeaders = requiredHeaders.stream()
                .filter(header -> !headers.containsKey(header))
                .collect(Collectors.toList());

        if (!missingHeaders.isEmpty()) {
            throw new InvalidRequestHeaderException("Missing required headers: " +
                    String.join(", ", missingHeaders));
        }
    }

    private void validateLoginHeaders(HttpHeaders headers) {
        if (!headers.containsKey("X-Auth-Username")) {
            throw new InvalidRequestHeaderException("Missing X-Auth-Username header");
        }
        if (!headers.containsKey("X-Auth-Password")) {
            throw new InvalidRequestHeaderException("Missing X-Auth-Password header");
        }
    }

    private UserDTO extractRegistrationData(HttpHeaders headers) {
        return UserDTO.builder()
                .username(headers.getFirst("X-Register-Username"))
                .password(headers.getFirst("X-Register-Password"))
                .email(headers.getFirst("X-Register-Email"))
                .firstName(headers.getFirst("X-Register-FirstName"))
                .lastName(headers.getFirst("X-Register-LastName"))
                .address(headers.getFirst("X-Register-Address"))
                .roles(parseRoles(headers.getFirst("X-Register-Roles")))
                .build();
    }

    private Set<String> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private void handleFailedLogin(User user, String identifier, AuthenticationException ex) {
        log.error("Login failed for {}: {}", identifier, ex.getMessage());
        loginAttemptService.addUserToLoginAttemptCache(user.getId());

        if (loginAttemptService.hasExceededMaxAttempts(user.getId())) {
            user.setLocked(true);
            userRepository.save(user);
            throw new AccountLockedException("Account locked due to multiple failed attempts.");
        }
    }

    // Custom exception for header validation
    public static class InvalidRequestHeaderException extends RuntimeException {
        public InvalidRequestHeaderException(String message) {
            super(message);
        }
    }


    @Override
    public void lockUser(String identifier) {
        User user = getUserByIdentifier(identifier);

        if (!user.isLocked()) {
            user.setLocked(true);
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

        if (user.isLocked()) {
            user.setLocked(false);
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
            //Assign the default Role AS USER
            Role defaultRole = roleRepository.findByName(RoleType.USER)
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

    @Override
    @Transactional
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
        log.info("Role {} added to user {}", roleType, userId);

        // If the role added is CUSTOMER, create an entry in the customers table
        if (roleType == RoleType.CUSTOMER) {
            createCustomerEntry(user);
        }
    }


    @Override
    @Transactional
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
            // Save user
            userRepository.save(user);
            log.info("Role {} removed from user {}", roleType, userId);

            // If the role removed is CUSTOMER, perform a soft delete on the customer entry
            if (roleType == RoleType.CUSTOMER) {
                softDeleteCustomerEntry(userId);
            }
        } else {
            log.warn("User {} does not have role {}", userId, roleType);
            throw new RuntimeException("User does not have role: " + roleType);
        }
    }

    // Helper method to create a Customer entry
    private void createCustomerEntry(User user) {
        Optional<Customer> existingCustomer = customerRepository.findByUserId(user.getId());
        if (existingCustomer.isPresent()) {
            log.info("Customer entry already exists for user {}", user.getId());
            return;
        }
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setBillingAddress(new Address());  // Initialize with default or provided data DTO
        customer.setShippingAddress(new Address());  // Initialize with default or provided data DTO
        customer.setActive(true); // Enable record
        customerRepository.save(customer);
        log.info("Customer entry created for user {}", user.getId());
    }

    // Helper method to perform a soft delete on the Customer entry
    private void softDeleteCustomerEntry(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer entry not found for user: " + userId));

        // Implement soft delete logic, e.g., setting an 'isDeleted' flag
        customer.setActive(false); // Assuming 'active' indicates soft deletion
        customerRepository.save(customer);
        log.info("Customer entry soft-deleted for user {}", userId);
    }


    @Override
    public List<UserDTO> getAllCustomers() throws InvalidRoleException {
        // Getting the Role from DB
        Role role = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new InvalidRoleException("Role not found: CUSTOMER"));
        return userRepository.findAllByRoles(role)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) throws UserNotFoundException,
            InvalidUserDataException, EmailExistException, InvalidRoleException, UsernameExistException {

        log.info("Updating user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Update Username with Validation
        if (!user.getUsername().equals(userDTO.getUsername())) {
            if (userRepository.existsByUsername(userDTO.getUsername())) {
                throw new UsernameExistException("Username already exists: " + userDTO.getUsername());
            }
            user.setUsername(userDTO.getUsername());
        }

        // Update Email with Validation
        if (!user.getEmail().equals(userDTO.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new EmailExistException("Email already exists: " + userDTO.getEmail());
            }
            user.setEmail(userDTO.getEmail());
        }

        // Update Other Fields
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAddress(userDTO.getAddress());

        // Optionally, update roles if provided
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<Role> roles = getRolesFromNames(userDTO.getRoles());
            user.setRoles(roles);
        }

        // Validate updated user data
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Invalid user data: ");
            for (ConstraintViolation<User> violation : violations) {
                errorMessage.append(violation.getMessage()).append(", ");
            }
            throw new InvalidUserDataException(errorMessage.toString().trim());
        }

        // Save Updated User
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return userMapper.toDTO(updatedUser);
    }


    @Override
    @Transactional
    public void deleteUser(Long userId) throws UserNotFoundException {
        log.info("Attempting to delete user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
        log.info("User deleted successfully: {}", user.getUsername());
    }


    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword)
            throws UserNotFoundException, BadCredentialsException, InvalidUserDataException {
        log.info("Attempting to change password for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Invalid old password attempt for user ID: {}", userId);
            throw new BadCredentialsException("Current password is incorrect");
        }
        // Validate new password
        validatePassword(newPassword);
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Send confirmation email
        try {
            emailService.sendPasswordChangeConfirmationEmail(user.getFirstName(), user.getEmail());
            log.info("Password changed successfully for user ID: {}", userId);
        } catch (MessagingException e) {
            log.error("Failed to send password change confirmation email", e);
            // Don't throw exception as password was already changed successfully
        }
    }

    @Override
    @Transactional
    public void resetPassword(Long userId) {
        try {
            log.info("Attempting to reset password for user ID: {}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

            String newPassword = generateResetPassword();
            log.info("-----------------------------------------------");
            log.info("new password : {}  for user ID: {}",newPassword, userId);
            log.info("-----------------------------------------------");
            String encodedPassword = passwordEncoder.encode(newPassword);
            // update password
            user.setPassword(encodedPassword);
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user.getFirstName(), user.getUsername(), newPassword, user.getEmail());
            log.info("Password reset successfully for user ID: {}", userId);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw e; // Re-throw exception
        } catch (MessagingException e) {
            log.error("Error sending confirmation email: {}", e.getMessage());
            throw new EmailExistException("Error sending confirmation email.");
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            throw new InvalidUserDataException("An unexpected error occurred.");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // generate reset password
    private String generateResetPassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    private void validatePassword(String password) throws InvalidUserDataException {
        if (password == null || password.length() < 8) {
            throw new InvalidUserDataException("Password must be at least 8 characters long");
        }
        // Add more password validation rules as needed
    }

}