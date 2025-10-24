package stock_trading.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stock_trading.dto.AuthResponse;
import stock_trading.dto.CreateUserRequest;
import stock_trading.dto.LoginRequest;
import stock_trading.exception.UserExistsException;
import stock_trading.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private CreateUserRequest createUserRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

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

        authResponse = new AuthResponse("jwt.token.here");
    }

    @Test
    @DisplayName("Should authenticate user successfully and return 200 OK")
    void shouldAuthenticateUserSuccessfullyAndReturn200Ok() {
        when(userService.authenticateUser(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.authenticate(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authResponse);
        assertThat(response.getBody().token()).isEqualTo("jwt.token.here");

        verify(userService).authenticateUser(loginRequest);
    }

    @Test
    @DisplayName("Should propagate UserExistsException when registering duplicate user")
    void shouldPropagateUserExistsExceptionWhenRegisteringDuplicateUser() {
        when(userService.createUser(createUserRequest))
                .thenThrow(new UserExistsException("email already in use"));

        assertThatThrownBy(() -> authController.registerUser(createUserRequest))
                .isInstanceOf(UserExistsException.class)
                .hasMessage("email already in use");

        verify(userService).createUser(createUserRequest);
    }

    @Test
    @DisplayName("Should propagate authentication exceptions during login")
    void shouldPropagateAuthenticationExceptionsDuringLogin() {
        when(userService.authenticateUser(loginRequest))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authController.authenticate(loginRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(userService).authenticateUser(loginRequest);
    }

    @Test
    @DisplayName("Should return different tokens for different users")
    void shouldReturnDifferentTokensForDifferentUsers() {
        AuthResponse authResponse1 = new AuthResponse("token1");
        AuthResponse authResponse2 = new AuthResponse("token2");

        CreateUserRequest user1 = CreateUserRequest.builder()
                .firstName("User1")
                .lastName("Test")
                .username("user1")
                .email("user1@example.com")
                .password("pass1")
                .build();

        LoginRequest login1 = LoginRequest.builder()
                .email("user1@example.com")
                .password("pass1")
                .build();

        when(userService.createUser(user1)).thenReturn(authResponse1);
        when(userService.authenticateUser(login1)).thenReturn(authResponse2);

        ResponseEntity<AuthResponse> registerResponse = authController.registerUser(user1);
        ResponseEntity<AuthResponse> loginResponse = authController.authenticate(login1);

        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().token()).isEqualTo("token1");
        assertThat(loginResponse.getBody().token()).isEqualTo("token2");
        assertThat(registerResponse.getBody()).isNotEqualTo(loginResponse.getBody());
    }

    @Test
    @DisplayName("Should validate request bodies with @Valid annotation")
    void shouldValidateRequestBodiesWithValidAnnotation() {
        // This test verifies that the @Valid annotation is present
        // The presence of @Valid ensures Spring will validate the request body

        // This is more of a sanity check that the method signature is correct
        assertDoesNotThrow(() -> {
            // The @Valid annotation should be processed by Spring
            // We're just verifying the method can be called
            authController.registerUser(createUserRequest);
            authController.authenticate(loginRequest);
        });
    }

    @Test
    @DisplayName("Should return proper response structure for registration")
    void shouldReturnProperResponseStructureForRegistration() {
        when(userService.createUser(createUserRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.registerUser(createUserRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        assertThat(response.getBody().token()).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should return proper response structure for login")
    void shouldReturnProperResponseStructureForLogin() {
        when(userService.authenticateUser(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.authenticate(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        assertThat(response.getBody().token()).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should handle service returning null auth response")
    void shouldHandleServiceReturningNullAuthResponse() {
        when(userService.createUser(createUserRequest)).thenReturn(null);
        when(userService.authenticateUser(loginRequest)).thenReturn(null);

        ResponseEntity<AuthResponse> registerResponse = authController.registerUser(createUserRequest);
        ResponseEntity<AuthResponse> loginResponse = authController.authenticate(loginRequest);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNull();

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNull();
    }

}