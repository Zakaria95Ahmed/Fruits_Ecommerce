package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.exceptions.ExceptionsDomain.*;
import com.fruits.ecommerce.models.dtos.AuthResponseDTO;
import com.fruits.ecommerce.models.dtos.LoginRequestDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.enums.RoleType;
import org.springframework.security.core.AuthenticationException;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface IUserService {

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
    UserDTO register(UserDTO userDTO);

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
    AuthResponseDTO login(LoginRequestDTO loginRequest);
    void lockUser(String identifier);
    void unlockUser(String identifier);
    // adding Role To User-Account
    void addRoleToUser(Long userId, RoleType roleType) throws UserNotFoundException, InvalidRoleException;
    void removeRoleFromUser(Long userId, RoleType roleType) throws UserNotFoundException, InvalidRoleException;
    List<UserDTO> getAllCustomers() throws RoleNotFoundException;
    List<UserDTO> getAllUsers();

}
