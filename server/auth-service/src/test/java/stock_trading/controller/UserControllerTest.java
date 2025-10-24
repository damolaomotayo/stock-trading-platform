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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import stock_trading.dto.UserRecord;
import stock_trading.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private UserRecord userRecord;

    @BeforeEach
    void setUp() {
        userRecord = UserRecord.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    @DisplayName("Should return current user successfully")
    void shouldReturnCurrentUserSuccessfully() {
        when(userService.getCurrentUser(authentication)).thenReturn(userRecord);

        ResponseEntity<UserRecord> response = userController.getCurrentUser(authentication);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userRecord);

        verify(userService).getCurrentUser(authentication);
    }

    @Test
    @DisplayName("Should return user with correct data")
    void shouldReturnUserWithCorrectData() {
        when(userService.getCurrentUser(authentication)).thenReturn(userRecord);

        ResponseEntity<UserRecord> response = userController.getCurrentUser(authentication);
        UserRecord responseBody = response.getBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getFirstName()).isEqualTo("John");
        assertThat(responseBody.getLastName()).isEqualTo("Doe");
        assertThat(responseBody.getUsername()).isEqualTo("johndoe");
        assertThat(responseBody.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should propagate UsernameNotFoundException when user not found")
    void shouldPropagateUsernameNotFoundExceptionWhenUserNotFound() {
        when(userService.getCurrentUser(authentication))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThatThrownBy(() -> userController.getCurrentUser(authentication))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userService).getCurrentUser(authentication);
    }

    @Test
    @DisplayName("Should handle null authentication parameter")
    void shouldHandleNullAuthenticationParameter() {
        when(userService.getCurrentUser(null))
                .thenThrow(new NullPointerException());

        assertThatThrownBy(() -> userController.getCurrentUser(null))
                .isInstanceOf(NullPointerException.class);

        verify(userService).getCurrentUser(null);
    }

    @Test
    @DisplayName("Should call service with correct authentication object")
    void shouldCallServiceWithCorrectAuthenticationObject() {
        Authentication specificAuth = mock(Authentication.class);
        when(userService.getCurrentUser(specificAuth)).thenReturn(userRecord);

        ResponseEntity<UserRecord> response = userController.getCurrentUser(specificAuth);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(userService).getCurrentUser(specificAuth);
        verify(userService, never()).getCurrentUser(authentication);
    }

    @Test
    @DisplayName("Should return different user records for different authentications")
    void shouldReturnDifferentUserRecordsForDifferentAuthentications() {
        UserRecord userRecord2 = UserRecord.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .username("janesmith")
                .email("jane.smith@example.com")
                .build();

        Authentication auth1 = mock(Authentication.class);
        Authentication auth2 = mock(Authentication.class);

        when(userService.getCurrentUser(auth1)).thenReturn(userRecord);
        when(userService.getCurrentUser(auth2)).thenReturn(userRecord2);

        ResponseEntity<UserRecord> response1 = userController.getCurrentUser(auth1);
        ResponseEntity<UserRecord> response2 = userController.getCurrentUser(auth2);

        assertThat(response1.getBody()).isEqualTo(userRecord);
        assertThat(response2.getBody()).isEqualTo(userRecord2);
        assertThat(response1.getBody()).isNotEqualTo(response2.getBody());

        verify(userService).getCurrentUser(auth1);
        verify(userService).getCurrentUser(auth2);
    }

    @Test
    @DisplayName("Should return 200 OK status code")
    void shouldReturn200OkStatusCode() {
        when(userService.getCurrentUser(authentication)).thenReturn(userRecord);

        ResponseEntity<UserRecord> response = userController.getCurrentUser(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should have response headers")
    void shouldHaveResponseHeaders() {
        when(userService.getCurrentUser(authentication)).thenReturn(userRecord);

        ResponseEntity<UserRecord> response = userController.getCurrentUser(authentication);

        assertThat(response.getHeaders()).isNotNull();
    }

    @Test
    @DisplayName("Should handle service returning null user record")
    void shouldHandleServiceReturningNullUserRecord() {
        when(userService.getCurrentUser(authentication)).thenReturn(null);

        ResponseEntity<UserRecord> response = userController.getCurrentUser(authentication);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(userService).getCurrentUser(authentication);
    }

}