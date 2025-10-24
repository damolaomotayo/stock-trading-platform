package stock_trading.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;
import stock_trading.dto.CustomException;
import stock_trading.dto.ValidationError;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {

    @InjectMocks
    private CustomExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple field errors")
    void shouldHandleMethodArgumentNotValidException() {
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("createUserRequest", "email", "Email must be valid"),
                new FieldError("createUserRequest", "password", "Password is required"),
                new FieldError("createUserRequest", "username", "Username is required")
        );

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ValidationError response = exceptionHandler.handleValidationError(methodArgumentNotValidException);

        assertThat(response).isNotNull();
        assertThat(response.errors()).hasSize(3);
        assertThat(response.errors()).containsKeys("email", "password", "username");
        assertThat(response.errors().get("email")).isEqualTo("Email must be valid");
        assertThat(response.errors().get("password")).isEqualTo("Password is required");
        assertThat(response.errors().get("username")).isEqualTo("Username is required");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithEmptyErrors() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ValidationError response = exceptionHandler.handleValidationError(methodArgumentNotValidException);

        assertThat(response).isNotNull();
        assertThat(response.errors()).isEmpty();
    }

    @Test
    @DisplayName("Should handle UserExistsException with conflict status")
    void shouldHandleUserExistsException() {
        String errorMessage = "User with email already exists";
        UserExistsException exception = new UserExistsException(errorMessage);

        CustomException response = exceptionHandler.handleUserExists(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle UsernameNotFoundException with not found status")
    void shouldHandleUsernameNotFoundException() {
        String errorMessage = "User not found with username: testuser";
        UsernameNotFoundException exception = new UsernameNotFoundException(errorMessage);

        CustomException response = exceptionHandler.handleUserNotFound(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle HttpRequestMethodNotSupportedException with bad request status")
    void shouldHandleHttpRequestMethodNotSupportedException() {
        String errorMessage = "Request method 'PUT' is not supported";
        HttpRequestMethodNotSupportedException exception =
                new HttpRequestMethodNotSupportedException("PUT");

        CustomException response = exceptionHandler.handleMethodNotSupported(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle NoHandlerFoundException with bad request status")
    void shouldHandleNoHandlerFoundException() {
        String errorMessage = "No endpoint GET /invalid-endpoint.";
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/invalid-endpoint", null);

        CustomException response = exceptionHandler.handleNoHandlerFound(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle AuthenticationException with unauthorized status")
    void shouldHandleAuthenticationException() {
        String errorMessage = "Authentication failed";
        AuthenticationException exception = new AuthenticationException(errorMessage) {};

        CustomException response = exceptionHandler.handleAuthentication(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle duplicate field errors by keeping the last one")
    void shouldHandleDuplicateFieldErrorsByKeepingLastOne() {
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("createUserRequest", "email", "First error message"),
                new FieldError("createUserRequest", "email", "Last error message"),
                new FieldError("createUserRequest", "password", "Password error")
        );

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ValidationError response = exceptionHandler.handleValidationError(methodArgumentNotValidException);

        assertThat(response).isNotNull();
        assertThat(response.errors()).hasSize(2);
        assertThat(response.errors().get("email")).isEqualTo("Last error message");
        assertThat(response.errors().get("password")).isEqualTo("Password error");
    }

    @Test
    @DisplayName("Should preserve all unique field errors")
    void shouldPreserveAllUniqueFieldErrors() {
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("createUserRequest", "email", "Invalid email"),
                new FieldError("createUserRequest", "username", "Username too short"),
                new FieldError("createUserRequest", "firstName", "First name required"),
                new FieldError("createUserRequest", "lastName", "Last name required")
        );

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ValidationError response = exceptionHandler.handleValidationError(methodArgumentNotValidException);

        assertThat(response).isNotNull();
        assertThat(response.errors()).hasSize(4);
        assertThat(response.errors()).containsOnlyKeys("email", "username", "firstName", "lastName");
    }

    @Test
    @DisplayName("Should handle exceptions with null messages gracefully")
    void shouldHandleExceptionsWithNullMessagesGracefully() {
        UserExistsException exception = new UserExistsException(null);

        CustomException response = exceptionHandler.handleUserExists(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isNull();
    }

    @Test
    @DisplayName("Should handle AuthenticationException with custom message")
    void shouldHandleAuthenticationExceptionWithCustomMessage() {
        String customMessage = "Invalid credentials provided";
        AuthenticationException exception = new AuthenticationException(customMessage) {};

        CustomException response = exceptionHandler.handleAuthentication(exception);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(customMessage);
    }

}