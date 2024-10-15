package com.fruits.ecommerce.controller;


import com.fruits.ecommerce.models.dtos.AuthResponseDTO;
import com.fruits.ecommerce.models.dtos.LoginRequestDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.services.Interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService authService;
    /**
     * Register a new user.
     *
     * @param userDTO new User Details.
     * @return ResponseEntity  UserDTO with HTTP Status.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Received registration request for user: {}", userDTO.getUsername());
        UserDTO createdUser = authService.register(userDTO);
        log.info("User registered successfully: {}", createdUser.getUsername());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * User Login .
     *
     * @param loginRequest Login Details.
     * @return ResponseEntity مع AuthResponseDTO و HTTP Status.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponse = authService.login(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    //Lock the user account
    @PostMapping("/lock-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> lockUser(@RequestParam String identifier) {
        authService.lockUser(identifier);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlock-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlockUser(@RequestParam String identifier) {
        authService.unlockUser(identifier);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{userId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addRoleToUser(@PathVariable Long userId, @RequestParam RoleType roleType) {
        authService.addRoleToUser(userId, roleType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @RequestParam RoleType roleType) {
        authService.removeRoleFromUser(userId, roleType);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllCustomers() throws RoleNotFoundException {
        List<UserDTO> customers = authService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }




}
