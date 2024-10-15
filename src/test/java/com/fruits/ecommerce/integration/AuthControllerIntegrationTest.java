package com.fruits.ecommerce.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruits.ecommerce.models.dtos.LoginRequestDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.entities.Role;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.repository.RoleRepository;
import com.fruits.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    //Before each test, you can clean the database (optional)
    @BeforeEach
    void setUp() {
        // Clean the database (if necessary)
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Add the basic roles
        for (RoleType roleType : RoleType.values()) {
            Role role = new Role();
            role.setName(roleType);
            roleRepository.save(role);
        }
    }

    //  RegisterUser
    @Test
    void testRegisterUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // LoginUser
    @Test
    void testLoginUser_Success() throws Exception {
        // أولاً، تسجيل مستخدم
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        //Then log in
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // Test the LockUser method
    @Test
    @WithMockUser(roles = "ADMIN")
    void testLockUser_Success() throws Exception {
        // Register a user
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        // Execute the test: lock the user
        mockMvc.perform(post("/api/auth/lock-user")
                        .param("identifier", "testuser"))
                .andExpect(status().isOk());
    }

    //Test the unlockUser method
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUnlockUser_Success() throws Exception {
        // Register a user
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        //Lock the user before performing the unlock
        mockMvc.perform(post("/api/auth/lock-user")
                        .param("identifier", "testuser"))
                .andExpect(status().isOk());

        // Execute the test: unlock the user
        mockMvc.perform(post("/api/auth/unlock-user")
                        .param("identifier", "testuser"))
                .andExpect(status().isOk());
    }

    //Test the addRoleToUser method
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddRoleToUser_Success() throws Exception {
        //Register a user
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Retrieve the userId from the response
        String content = result.getResponse().getContentAsString();
        UserDTO createdUser = objectMapper.readValue(content, UserDTO.class);
        Long userId = createdUser.getId();

        // Execute the test: add a role to the user
        mockMvc.perform(post("/api/auth/{userId}/add", userId)
                        .param("roleType", "ADMIN"))
                .andExpect(status().isOk());
    }

    // Test the RemoveRoleFromUser method
    @Test
    @WithMockUser(roles = "ADMIN")
    void testRemoveRoleFromUser_Success() throws Exception {
        //Register a user
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Retrieve the userId from the response
        String content = result.getResponse().getContentAsString();
        UserDTO createdUser = objectMapper.readValue(content, UserDTO.class);
        Long userId = createdUser.getId();

        // Add a role to the user so that it can be removed
        mockMvc.perform(post("/api/auth/{userId}/add", userId)
                        .param("roleType", "CLIENT"))
                .andExpect(status().isOk());

        //Execute the test: remove the role from the user
        mockMvc.perform(delete("/api/auth/{userId}/remove", userId)
                        .param("roleType", "CLIENT"))
                .andExpect(status().isOk());
    }
}
