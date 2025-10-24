package stock_trading.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    private final Validator validator;

    public CreateUserRequestTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("should create valid CreateUserRequest with all fields")
    void shouldCreateValidCreateUserRequest() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();

        assertThat(request.getFirstName()).isEqualTo("John");
        assertThat(request.getLastName()).isEqualTo("Doe");
        assertThat(request.getUsername()).isEqualTo("johndoe");
        assertThat(request.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(request.getPassword()).isEqualTo("securePassword123");
    }

    @Test
    @DisplayName("Should validate all constraints when all fields are valid")
    void shouldValidateAllConstraintsWhenValid() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should fail validation when firstName is blank or null")
    void shouldFailValidationWhenFirstNameIsBlankOrNull(String firstName) {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName(firstName)
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<CreateUserRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
        assertThat(violation.getMessage()).isNotBlank();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should fail validation when lastName is blank or null")
    void shouldFailValidationWhenLastNameIsBlankOrNull(String lastName) {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName(lastName)
                .username("johndoe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<CreateUserRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("lastName");
        assertThat(violation.getMessage()).isNotBlank();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should fail validation when username is blank or null")
    void shouldFailValidationWhenUsernameIsBlankOrNull(String username) {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username(username)
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<CreateUserRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("username");
        assertThat(violation.getMessage()).isNotBlank();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should fail validation when password is blank or null")
    void shouldFailValidationWhenPasswordIsBlankOrNull(String password) {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password(password)
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<CreateUserRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
        assertThat(violation.getMessage()).isNotBlank();
    }

    @Test
    @DisplayName("Should fail validation when multiple fields are invalid")
    void shouldFailValidationWhenMultipleFieldsAreInvalid() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("")
                .lastName("")
                .username("")
                .email("invalid-email")
                .password("")
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(5);

        Set<String> violatedFields = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());

        assertThat(violatedFields).containsExactlyInAnyOrder(
                "firstName", "lastName", "username", "email", "password"
        );
    }

    @Test
    @DisplayName("Should validate valid email formats")
    void shouldValidateValidEmailFormats() {
        // Given
        String[] validEmails = {
                "test@example.com",
                "test.user@example.co.uk",
                "test_user@example-domain.com",
                "test+tag@example.com",
                "test@sub.domain.com"
        };

        for (String email : validEmails) {
            CreateUserRequest request = CreateUserRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .username("johndoe")
                    .email(email)
                    .password("securePassword123")
                    .build();

            Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("Should have proper toString method")
    void shouldHaveProperToStringMethod() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();

        String toStringResult = request.toString();

        assertThat(toStringResult).contains("John", "Doe", "johndoe", "john.doe@example.com", "securePassword123");
        assertThat(toStringResult).doesNotContain("invalidvalue");
    }

    @Test
    @DisplayName("Should work with lombok builder pattern")
    void shouldWorkWithLombokBuilderPattern() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .username("alicej")
                .email("alice.johnson@example.com")
                .password("alicePassword")
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getFirstName()).isEqualTo("Alice");
        assertThat(request.getLastName()).isEqualTo("Johnson");
        assertThat(request.getUsername()).isEqualTo("alicej");
        assertThat(request.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(request.getPassword()).isEqualTo("alicePassword");
    }
}