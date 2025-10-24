package stock_trading.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import stock_trading.dto.AuthResponse;
import stock_trading.dto.CreateUserRequest;
import stock_trading.dto.LoginRequest;
import stock_trading.dto.UserRecord;
import stock_trading.entity.User;
import stock_trading.entity.UserPrincipal;
import stock_trading.exception.UserExistsException;
import stock_trading.repository.UserRepository;
import stock_trading.util.JwtTokenUtil;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .roles(new HashSet<>())
                .build();

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create user successfully when user does not exist")
    void shouldCreateUserSuccessfullyWhenUserDoesNotExist() {
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        String expectedToken = "jwt.token.here";
        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn(expectedToken);

        AuthResponse response = userService.createUser(createUserRequest);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(expectedToken);

        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(userRepository).existsByUsername(createUserRequest.getUsername());
        verify(passwordEncoder).encode(createUserRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateToken(mockAuthentication);
    }

    @Test
    @DisplayName("Should throw UserExistsException when email already exists")
    void shouldThrowUserExistsExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(UserExistsException.class)
                .hasMessage("email already in use");

        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserExistsException when username already exists")
    void shouldThrowUserExistsExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(UserExistsException.class)
                .hasMessage("username already in use");

        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(userRepository).existsByUsername(createUserRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void shouldAuthenticateUserSuccessfullyWithValidCredentials() {
        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        String expectedToken = "auth.token.here";
        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn(expectedToken);

        AuthResponse response = userService.authenticateUser(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(expectedToken);

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateToken(mockAuthentication);

        var securityContextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(securityContextAuth).isEqualTo(mockAuthentication);
    }

    @Test
    @DisplayName("Should throw exception when authentication fails")
    void shouldThrowExceptionWhenAuthenticationFails() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> userService.authenticateUser(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil, never()).generateToken(any(Authentication.class));
    }

    @Test
    @DisplayName("Should get current user successfully")
    void shouldGetCurrentUserSuccessfully() {
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getEmail()).thenReturn("john.doe@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        UserRecord userRecord = userService.getCurrentUser(authentication);

        assertThat(userRecord).isNotNull();
        assertThat(userRecord.getId()).isEqualTo(1L);
        assertThat(userRecord.getFirstName()).isEqualTo("John");
        assertThat(userRecord.getLastName()).isEqualTo("Doe");
        assertThat(userRecord.getUsername()).isEqualTo("johndoe");
        assertThat(userRecord.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when current user not found in database")
    void shouldThrowUsernameNotFoundExceptionWhenCurrentUserNotFoundInDatabase() {
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getEmail()).thenReturn("nonexistent@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(authentication))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should encode password when creating user")
    void shouldEncodePasswordWhenCreatingUser() {
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123");
            return user;
        });

        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn("token");

        userService.createUser(createUserRequest);

        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should create user with empty roles set")
    void shouldCreateUserWithEmptyRolesSet() {
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getRoles()).isNotNull();
            assertThat(savedUser.getRoles()).isEmpty();
            return user;
        });

        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn("token");

        userService.createUser(createUserRequest);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should use correct authentication token for login")
    void shouldUseCorrectAuthenticationTokenForLogin() {
        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenAnswer(invocation -> {
            UsernamePasswordAuthenticationToken token = invocation.getArgument(0);
            assertThat(token.getPrincipal()).isEqualTo(loginRequest.getEmail());
            assertThat(token.getCredentials()).isEqualTo(loginRequest.getPassword());
            return mockAuthentication;
        });

        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn("token");

        userService.authenticateUser(loginRequest);

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should handle user creation with special characters in fields")
    void shouldHandleUserCreationWithSpecialCharactersInFields() {
        CreateUserRequest specialRequest = CreateUserRequest.builder()
                .firstName("Jöhn")
                .lastName("Döe- Smith")
                .username("user_name-123")
                .email("user+tag@example.com")
                .password("p@ssw0rd!")
                .build();

        when(userRepository.existsByEmail(specialRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(specialRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(specialRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn("token");

        AuthResponse response = userService.createUser(specialRequest);

        assertThat(response).isNotNull();
        verify(userRepository).existsByEmail(specialRequest.getEmail());
        verify(userRepository).existsByUsername(specialRequest.getUsername());
    }

    @Test
    @DisplayName("Should clear security context before setting new authentication")
    void shouldClearSecurityContextBeforeSettingNewAuthentication() {
        var previousAuth = new UsernamePasswordAuthenticationToken("previous", "creds");
        SecurityContextHolder.getContext().setAuthentication(previousAuth);

        Authentication mockAuthentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(jwtTokenUtil.generateToken(mockAuthentication)).thenReturn("token");

        userService.authenticateUser(loginRequest);

        var currentAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(currentAuth).isEqualTo(mockAuthentication);
        assertThat(currentAuth).isNotEqualTo(previousAuth);
    }

    @Test
    @DisplayName("Should handle null authentication in getCurrentUser")
    void shouldHandleNullAuthenticationInGetCurrentUser() {
        assertThatThrownBy(() -> userService.getCurrentUser(null))
                .isInstanceOf(NullPointerException.class);
    }
    
}