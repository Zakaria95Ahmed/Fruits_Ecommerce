package com.fruits.ecommerce.controller;

import com.fruits.ecommerce.models.dtos.AuthResponseDTO;
import com.fruits.ecommerce.models.dtos.LoginRequestDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.services.Interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private IUserService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // اختبارات الميثود registerUser
    @Test
    void testRegisterUser_Success() {
        // ترتيب البيانات
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setEmail("test@example.com");

        when(authService.register(any(UserDTO.class))).thenReturn(userDTO);

        // تنفيذ الميثود
        ResponseEntity<UserDTO> response = authController.registerUser(userDTO);

        // التحقق من النتائج
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    // اختبارات الميثود loginUser
    @Test
    void testLoginUser_Success() {
        // ترتيب البيانات
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken("jwt-token");
        authResponse.setUser(new UserDTO());

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponse);

        // تنفيذ الميثود
        ResponseEntity<AuthResponseDTO> response = authController.loginUser(loginRequest);

        // التحقق من النتائج
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
    }

    // اختبارات الميثود lockUser
    @Test
    void testLockUser_Success() {
        // ترتيب البيانات
        String identifier = "testuser";

        doNothing().when(authService).lockUser(identifier);

        // تنفيذ الميثود
        ResponseEntity<Void> response = authController.lockUser(identifier);

        // التحقق من النتائج
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // اختبارات الميثود unlockUser
    @Test
    void testUnlockUser_Success() {
        // ترتيب البيانات
        String identifier = "testuser";

        doNothing().when(authService).unlockUser(identifier);

        // تنفيذ الميثود
        ResponseEntity<Void> response = authController.unlockUser(identifier);

        // التحقق من النتائج
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // اختبارات الميثود addRoleToUser
    @Test
    void testAddRoleToUser_Success() {
        // ترتيب البيانات
        Long userId = 1L;
        RoleType roleType = RoleType.CUSTOMER;

        doNothing().when(authService).addRoleToUser(userId, roleType);

        // تنفيذ الميثود
        ResponseEntity<Void> response = authController.addRoleToUser(userId, roleType);

        // التحقق من النتائج
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // اختبارات الميثود removeRoleFromUser
    @Test
    void testRemoveRoleFromUser_Success() {
        // ترتيب البيانات
        Long userId = 1L;
        RoleType roleType = RoleType.CUSTOMER;

        doNothing().when(authService).removeRoleFromUser(userId, roleType);

        // تنفيذ الميثود
        ResponseEntity<Void> response = authController.removeRoleFromUser(userId, roleType);

        // التحقق من النتائج
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}