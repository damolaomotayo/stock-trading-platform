package stock_trading.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private final Validator validator;

    public LoginRequestTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should create valid LoginRequest with all fields")
    void shouldCreateValidLoginRequest() {
        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("password123")
                .build();

        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Should validate all constraints when all fields are valid")
    void shouldValidateAllConstraintsWhenValid() {
        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should fail validation when email is blank, null, or whitespace")
    void shouldFailValidationWhenEmailIsBlankOrNull(String email) {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).containsAnyOf("email is required", "email must be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "missing.at.com", "@domain.com", "test@", "no@.com", "@"})
    @DisplayName("Should fail validation when email format is invalid")
    void shouldFailValidationWhenEmailFormatIsInvalid(String invalidEmail) {
        LoginRequest request = LoginRequest.builder()
                .email(invalidEmail)
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("email must be valid");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should fail validation when password is blank, null, or whitespace")
    void shouldFailValidationWhenPasswordIsBlankOrNull(String password) {
        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password(password)
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
        assertThat(violation.getMessage()).isEqualTo("password is required");
    }

    @Test
    @DisplayName("Should fail validation when both email and password are invalid")
    void shouldFailValidationWhenBothEmailAndPasswordAreInvalid() {
        LoginRequest request = LoginRequest.builder()
                .email("invalid-email")
                .password("")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);

        Set<String> violatedFields = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());

        assertThat(violatedFields).containsExactlyInAnyOrder("email", "password");

        // Verify specific error messages
        Set<String> errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());

        assertThat(errorMessages).containsExactlyInAnyOrder(
                "email must be valid",
                "password is required"
        );
    }

    @Test
    @DisplayName("Should validate various valid email formats")
    void shouldValidateVariousValidEmailFormats() {
        String[] validEmails = {
                "user@example.com",
                "first.last@example.co.uk",
                "user_name@example-domain.com",
                "user+tag@example.com",
                "user@sub.domain.com",
                "user@example.io",
                "123@example.com",
                "user@123.456.789",
                "user@example-test.com"
        };

        for (String email : validEmails) {
            LoginRequest request = LoginRequest.builder()
                    .email(email)
                    .password("password123")
                    .build();

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertThat(violations).as("Email: " + email).isEmpty();
        }
    }

    @Test
    @DisplayName("Should work with lombok builder pattern")
    void shouldWorkWithLombokBuilderPattern() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("testPassword")
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("testPassword");
    }

    @Test
    @DisplayName("Should handle case sensitivity in email")
    void shouldHandleCaseSensitivityInEmail() {
        LoginRequest request1 = LoginRequest.builder()
                .email("USER@EXAMPLE.COM")
                .password("password")
                .build();

        LoginRequest request2 = LoginRequest.builder()
                .email("user@example.com")
                .password("password")
                .build();

        assertThat(request1.getEmail()).isNotEqualTo(request2.getEmail());
        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    @DisplayName("Should allow password with various characters")
    void shouldAllowPasswordWithVariousCharacters() {
        String[] validPasswords = {
                "simple",
                "123456",
                "password with spaces",
                "special!@#$%",
                "UPPERCASE",
                "mixedCase123",
                "veryLongPasswordWithMultipleWordsAndNumbers12345"
        };

        for (String password : validPasswords) {
            LoginRequest request = LoginRequest.builder()
                    .email("user@example.com")
                    .password(password)
                    .build();

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertThat(violations).as("Password: " + password).isEmpty();
            assertThat(request.getPassword()).isEqualTo(password);
        }
    }
}