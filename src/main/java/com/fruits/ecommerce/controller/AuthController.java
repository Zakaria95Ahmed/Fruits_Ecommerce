package com.fruits.ecommerce.controller;

import com.fruits.ecommerce.exceptions.exceptionsDomain.users.InvalidRoleException;
import com.fruits.ecommerce.models.dtos.*;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.services.Interfaces.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestHeader HttpHeaders headers) {
        log.info("Received secure registration request");
        UserDTO createdUser = authService.register(headers);
        log.info("User registered successfully: {}", createdUser.getUsername());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestHeader HttpHeaders headers) {
        log.info("Received secure login request");
        AuthResponseDTO authResponse = authService.login(headers);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }


    //Lock and UnLock the user account
    /**
     * Lock the user account.
     *
     * @param identifier Username or email of the user to lock.
     * @return ResponseEntity with no content.
     */
    // ADMIN (Access-Only)
    @PostMapping("/lock-user")
    public ResponseEntity<Void> lockUser(@RequestParam String identifier) {
        authService.lockUser(identifier);
        return ResponseEntity.ok().build();
    }

    /**
     * Unlock the user account.
     *
     * @param identifier Username or email of the user to unlock.
     * @return ResponseEntity with no content.
     */
    // ADMIN (Access-Only)
    @PostMapping("/unlock-user")
    public ResponseEntity<Void> unlockUser(@RequestParam String identifier) {
        authService.unlockUser(identifier);
        return ResponseEntity.ok().build();
    }

    /**
     * Add a role to a user.
     *
     * @param userId   User ID to add the role to.
     * @param roleType Role type to add.
     * @return ResponseEntity with no content.
     */
    // ADMIN (Access-Only)
    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addRoleToUser(@PathVariable Long userId, @RequestParam RoleType roleType) {
        authService.addRoleToUser(userId, roleType);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a role from a user.
     *
     * @param userId   User ID to remove the role from.
     * @param roleType Role type to remove.
     * @return ResponseEntity with no content.
     */
    // ADMIN (Access-Only)
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @RequestParam RoleType roleType) {
        authService.removeRoleFromUser(userId, roleType);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all customers.
     *
     * @return ResponseEntity with a list of UserDTOs.
     * @throws --RoleNotFoundException if the CUSTOMER role is not found.
     */
    // ADMIN (Access-Only)
    @GetMapping("/customers")
    public ResponseEntity<List<UserDTO>> getAllCustomers() throws InvalidRoleException {
        List<UserDTO> customers = authService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Get all users.
     *
     * @return ResponseEntity with a list of UserDTOs.
     */
    // ADMIN (Access-Only)
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update a user's information.
     *
     * @param userId  User ID to update.
     * @param userDTO Updated user details.
     * @return ResponseEntity with updated UserDTO.
     */
    // We use @PreAuthorize("#userId == authentication.principal.id and hasRole('ADMIN')")
    // To Prevents users from changing each other's passwords
    // Ensures that each user can only change their own password
    // Security level is verified before accessing the code
    // Can be easily changed without modifying the method code
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorizationUtils.hasUserId(authentication, #userId)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = authService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Change a user's password.
     *
     * @param userId                User ID to change password for.
     * @param passwordChangeRequest Password change details.
     * @return ResponseEntity with no content.
     */
    // We use @PreAuthorize("#userId == authentication.principal.id and hasRole('ADMIN')")
    // To Prevents users from changing each other's passwords
    // Ensures that each user can only change their own password
    // Security level is verified before accessing the code
    // Can be easily changed without modifying the method code
    // @PreAuthorize("#userId == authentication.principal.id and hasRole('ADMIN')")
    // Use a separate bean
    @PutMapping("/{userId}/change-password")
    @PreAuthorize("@userAuthorizationUtils.hasUserId(authentication, #userId)")
    public ResponseEntity<Void> changePassword(@PathVariable Long userId, @Valid @RequestBody ChangePasswordRequestDTO passwordChangeRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("User ID from URL: {}", userId);
        log.info("Principal ID: {}", auth.getPrincipal());
        log.info("Changing password for user ID: {}", userId);
        authService.changePassword(userId, passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());
        log.info("Password changed successfully for user ID: {}", userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Reset user password by admin.
     * System will generate a new password and send it to user's email.
     * (ADMIN-Access-Only)
     *
     * @param userId The ID of the user to reset their password
     * @return ResponseEntity with no content
     */
    // ADMIN (Access-Only)
    @PutMapping("/{userId}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long userId) throws MessagingException {
        log.info("Received request to reset password for user ID: {}", userId);
        authService.resetPassword(userId);
        log.info("Password reset successfully completed for user ID: {}", userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a user.
     *
     * @param userId User ID to delete.
     * @return ResponseEntity with no content.
     */
    // ADMIN (Access-Only)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        authService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


}

