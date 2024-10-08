package com.fruits.ecommerce.services;


import com.fruits.ecommerce.configuration.SecurityConfig.ExtraServices.EmailService;
import com.fruits.ecommerce.configuration.SecurityConfig.ExtraServices.LoginAttemptService;
import com.fruits.ecommerce.configuration.SecurityConfig.JWT_Filters.JWTTokenProvider;
import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.UserData;
import com.fruits.ecommerce.exceptions.ExceptionsDomain.AccountLockedException;
import com.fruits.ecommerce.exceptions.ExceptionsDomain.EmailExistException;
import com.fruits.ecommerce.exceptions.ExceptionsDomain.UserNotFoundException;
import com.fruits.ecommerce.exceptions.ExceptionsDomain.UsernameExistException;
import com.fruits.ecommerce.models.dtos.AuthResponseDTO;
import com.fruits.ecommerce.models.dtos.LoginRequestDTO;
import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.entities.Role;
import com.fruits.ecommerce.models.entities.User;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.models.mappers.UserMapper;
import com.fruits.ecommerce.repository.RoleRepository;
import com.fruits.ecommerce.repository.UserRepository;
import com.fruits.ecommerce.services.implementations.AuthImplementation;
import jakarta.mail.MessagingException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthImplementationTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTTokenProvider jwtTokenProvider;
    @Mock
    private EmailService emailService;
    @Mock
    private Validator validator;
    @InjectMocks
    private AuthImplementation authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the validator إذا كنت تستخدم Validator حقيقي
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        authService = new AuthImplementation(
                userRepository,
                roleRepository,
                passwordEncoder,
                userMapper,
                authenticationManager,
                jwtTokenProvider,
                loginAttemptService,
                emailService,
                validator
        );
    }

    // 1. اختبارات تسجيل المستخدم

    @Test
    public void register_UserAlreadyExists_ShouldThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Existing");
        userDTO.setLastName("User");
        userDTO.setUsername("existingUser");
        userDTO.setEmail("existing@example.com");
        userDTO.setPassword("password");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        UsernameExistException thrown = assertThrows(UsernameExistException.class, () -> {
            authService.register(userDTO);
        });

        assertEquals("Username already exists.", thrown.getMessage());
    }

    @Test
    public void register_EmailAlreadyExists_ShouldThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Existing");
        userDTO.setLastName("User");
        userDTO.setUsername("existingUser");
        userDTO.setEmail("existing@example.com");
        userDTO.setPassword("password");

        when(userRepository.existsByUsername("existingUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        EmailExistException thrown = assertThrows(EmailExistException.class, () -> {
            authService.register(userDTO);
        });

        assertEquals("Email already exists.", thrown.getMessage());
    }

    @Test
    public void register_Success() throws MessagingException {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUsername("newuser");
        userDTO.setEmail("newuser@example.com");
        userDTO.setPassword("password");

        Role defaultRole = new Role();
        defaultRole.setName(RoleType.VISITOR);

        User userEntity = new User();
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setUsername("newuser");
        userEntity.setEmail("newuser@example.com");
        // ضبط باقي الحقول إذا لزم الأمر

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("John"); // أضف هذا
        savedUser.setLastName("Doe");    // إذا احتجت
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com"); // وأضف هذا
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(defaultRole));
        savedUser.setActive(true);
        savedUser.setNotLocked(true);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(userEntity);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleType.VISITOR)).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser); // savedUser الآن يحتوي على firstName و email
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // تنفيذ الميثود
        UserDTO result = authService.register(userDTO);

        // التحقق من الاستدعاء الصحيح للـ emailService
        verify(emailService, times(1)).sendNewPasswordEmail("John", "newuser", "password", "newuser@example.com");

        // باقي التحقق كما هو
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("newuser", capturedUser.getUsername());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertTrue(capturedUser.isActive());
        assertTrue(capturedUser.isNotLocked());
        assertNotNull(capturedUser.getCreatedAt());
    }

    // 2. اختبارات تسجيل الدخول

    @Test
    public void login_UserNotFound_ShouldThrowException() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("nonexistentuser");
        loginRequest.setPassword("password");

        when(userRepository.findByUsernameOrEmail("nonexistentuser", "nonexistentuser"))
                .thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("In-Valid UserAccount with: nonexistentuser", thrown.getMessage());
    }

    @Test
    public void login_AccountLocked_ShouldThrowException() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("lockeduser");
        loginRequest.setPassword("password");

        User user = new User();
        user.setNotLocked(false);
        user.setUsername("lockeduser");
        user.setEmail("lockeduser@example.com");

        when(userRepository.findByUsernameOrEmail("lockeduser", "lockeduser"))
                .thenReturn(Optional.of(user));

        AccountLockedException thrown = assertThrows(AccountLockedException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Your account has been locked due to multiple failed login attempts. Please contact support.", thrown.getMessage());
    }

    @Test
    public void login_BadCredentials_ShouldThrowException() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("validuser");
        loginRequest.setPassword("wrongpassword");

        User user = new User();
        user.setNotLocked(true);
        user.setId(1L);
        user.setUsername("validuser");
        user.setEmail("validuser@example.com");

        when(userRepository.findByUsernameOrEmail("validuser", "validuser"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("In-Correct Credentials"));

        // بعد رمي الاستثناء، نتوقع أن يكون الحساب مغلق لو تعديت عدد المحاولات
        when(loginAttemptService.hasExceededMaxAttempts(1L)).thenReturn(true);

        AccountLockedException thrown = assertThrows(AccountLockedException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Your account has been locked due to multiple failed login attempts. Please try again later or Contact Technical Support-Team.", thrown.getMessage());

        // تأكد من تعيين user.setNotLocked(false)
        verify(userRepository).save(user);
    }

    @Test
    public void login_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("validuser");
        loginRequest.setPassword("password");

        User user = new User();
        user.setNotLocked(true);
        user.setId(1L);
        user.setUsername("validuser");
        user.setEmail("validuser@example.com");
        user.setLastLogin(LocalDateTime.now());

        // إنشاء mock للـ Authentication و UserData
        Authentication mockAuthentication = mock(Authentication.class);
        UserData mockUserData = mock(UserData.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserData);
        when(mockUserData.getUsername()).thenReturn("validuser");

        // ضبط الـ AuthenticationManager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        // ضبط الـ JWTTokenProvider
        when(jwtTokenProvider.generateJwtToken(mockUserData))
                .thenReturn("jwtToken");

        // ضبط الـ UserMapper
        UserDTO userDTO = new UserDTO();
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // ضبط الـ UserRepository
        when(userRepository.findByUsernameOrEmail("validuser", "validuser")).thenReturn(Optional.of(user));

        // تنفيذ الميثود
        AuthResponseDTO response = authService.login(loginRequest);

        // التحقق من النتيجة
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        // التحقق من تحديثات المستخدم
        verify(loginAttemptService).evictUserFromLoginAttemptCache(1L);
        verify(userRepository).save(user);
    }

    // 3. اختبارات lockUser و unlockUser

    @Test
    public void lockUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authService.lockUser("nonexistentuser");
        });
    }

    @Test
    public void lockUser_UserAlreadyLocked_ShouldThrowException() {
        User user = new User();
        user.setNotLocked(false);

        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(user));

        AccountLockedException thrown = assertThrows(AccountLockedException.class, () -> {
            authService.lockUser("lockeduser");
        });

        assertEquals("User account is already locked: lockeduser", thrown.getMessage());
    }


    @Test
    public void lockUser_Success_ShouldLockUserAndSendEmail() throws MessagingException {
        String identifier = "user@example.com";

        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setFirstName("UserFirstName"); // أضف ده
        user.setEmail(identifier);
        user.setNotLocked(true);

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.of(user));
        doNothing().when(loginAttemptService).addUserToLoginAttemptCache(1L);
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(emailService).sendAccountLockedEmail("UserFirstName", identifier); // عدّل ده لو احتاجت

        authService.lockUser(identifier);

        assertFalse(user.isNotLocked());
        verify(userRepository).save(user);
        verify(emailService, times(1)).sendAccountLockedEmail("UserFirstName", identifier); // عدّل ده برضه
    }

    @Test
    public void unlockUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authService.unlockUser("nonexistentuser");
        });
    }

    @Test
    public void unlockUser_UserAlreadyUnlocked_ShouldThrowException() {
        User user = new User();
        user.setNotLocked(true);

        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(user));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authService.unlockUser("user@example.com");
        });

        assertEquals("User account is already unlocked: user@example.com", thrown.getMessage());
    }


    @Test
    public void unlockUser_Success_ShouldUnlockUserAndSendEmail() throws MessagingException {
        String identifier = "user@example.com";

        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setFirstName("user"); // أضف ده
        user.setEmail(identifier);
        user.setNotLocked(false);

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.of(user));
        doNothing().when(loginAttemptService).evictUserFromLoginAttemptCache(1L);
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(emailService).sendAccountUnlockedEmail("user", identifier);

        authService.unlockUser(identifier);

        assertTrue(user.isNotLocked());
        verify(userRepository).save(user);
        verify(emailService, times(1)).sendAccountUnlockedEmail("user", identifier);
    }
}
