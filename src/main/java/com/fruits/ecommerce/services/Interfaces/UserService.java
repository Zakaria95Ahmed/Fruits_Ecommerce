package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.exceptions.exceptionsDomain.users.*;
import com.fruits.ecommerce.models.dtos.AuthResponseDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.entities.User;
import com.fruits.ecommerce.models.enums.RoleType;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Registers a new user.
     * //-------------------------//
     * //@param userDTO The user data transfer object containing registration information.
     * //@return The registered user's data transfer object.
     * //@throws UsernameExistException If the username already exists in the system.
     * //@throws EmailExistException If the email already exists in the system.
     * //@throws InvalidRoleException If the specified role is invalid.
     * //@throws InvalidUserDataException If the user data is invalid or incomplete.
     */
//    UserDTO register(UserDTO userDTO);

    /**
     * Authenticates a user and logs them in.
     *
     * @param loginRequest The login request data transfer object.
     * @return The authentication response data transfer object.
     * @throws AuthenticationException If there's a general authentication error.
     * @throws UserNotFoundException   If the user is not found in the system.
     * @throws AccountLockedException  If the user's account is locked.
     * @throws BadCredentialsException If the provided credentials are incorrect.
     */
    //AuthResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * Registers a new user using header-based authentication.
     *
     * @param headers HTTP headers containing registration information
     * @return The registered user's data transfer object
     * @throws UsernameExistException if the username already exists
     * @throws EmailExistException if the email already exists
     * @throws InvalidRoleException if the specified role is invalid
     * @throws InvalidUserDataException if the user data is invalid
     */
    UserDTO register(HttpHeaders headers);

    /**
     * Authenticates a user using header-based authentication.
     *
     * @param headers HTTP headers containing login credentials
     * @return The authentication response with JWT token
     * @throws AuthenticationException if authentication fails
     * @throws UserNotFoundException if the user is not found
     * @throws AccountLockedException if the account is locked
     * @throws BadCredentialsException if credentials are incorrect
     */
    AuthResponseDTO login(HttpHeaders headers);
    void lockUser(String identifier);
    void unlockUser(String identifier);
    // adding Role To User-Account
    void addRoleToUser(Long userId, RoleType roleType) throws UserNotFoundException, InvalidRoleException;
    void removeRoleFromUser(Long userId, RoleType roleType) throws UserNotFoundException, InvalidRoleException;
    List<UserDTO> getAllCustomers() throws InvalidRoleException;
    List<UserDTO> getAllUsers();


    /**
     * Updates an existing user's information.
     *
     * @param userId The ID of the user to update.
     * @param userDTO The updated user information.
     * @return The updated user's data transfer object.
     * @throws UserNotFoundException If the user is not found.
     * @throws InvalidUserDataException If the updated data is invalid.
     * @throws EmailExistException If the new email already exists for another user.
     */
    UserDTO updateUser(Long userId, UserDTO userDTO) throws UserNotFoundException,
            InvalidUserDataException, EmailExistException,InvalidRoleException;

    /**
     * Deletes a user from the system. Only accessible by admin.
     *
     * @param userId The ID of the user to delete.
     * @throws UserNotFoundException If the user is not found.
     * @throws AccessDeniedException If the current user is not an admin.
     */
    void deleteUser(Long userId) throws UserNotFoundException, AccessDeniedException;

    /**
     * Changes the user's password when the user knows their current password.
     *
     * @param userId The ID of the user
     * @param oldPassword The current password
     * @param newPassword The new password to set
     * @throws UserNotFoundException If the user is not found
     * @throws BadCredentialsException If the old password is incorrect
     * @throws InvalidUserDataException If the new password is invalid
     */
    void changePassword(Long userId, String oldPassword, String newPassword)
            throws UserNotFoundException, BadCredentialsException, InvalidUserDataException;

    void resetPassword(Long userId) throws UserNotFoundException, MessagingException;

    Optional<User> findByUsername(String username);
}
