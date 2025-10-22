package stock_trading.entity;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserPrincipalTest {

    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Set.of("ROLE_USER", "ROLE_ADMIN"))
                .build();
    }

    @Test
    @DisplayName("Should create UserPrincipal from User with correct properties")
    void shouldCreateUserPrincipalFromUser() {
        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal).isNotNull();
        assertThat(principal.getId()).isEqualTo(1L);
        assertThat(principal.getEmail()).isEqualTo("test@example.com");
        assertThat(principal.getUsername()).isEqualTo("test@example.com");
        assertThat(principal.getPassword()).isEqualTo("encodedPassword");
        assertThat(principal.getAuthorities()).hasSize(2);

        Set<String> authorityNames = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertThat(authorityNames).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should handle user with no roles")
    void shouldHandleUserWithNoRoles() {
        user.setRoles(Collections.emptySet());
        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal).isNotNull();
        assertThat(principal.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Should handle user with single role")
    void shouldHandleUserWithSingleRole() {
        user.setRoles(Set.of("ROLE_USER"));

        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal.getAuthorities()).hasSize(1);
        assertThat(principal.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("Should implement UserDetails interface correctly")
    void shouldImplementUserDetailsInterfaceCorrectly() {
        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal).isInstanceOf(UserDetails.class);
        assertThat(principal.getUsername()).isEqualTo("test@example.com");
        assertThat(principal.getPassword()).isEqualTo("encodedPassword");
        assertThat(principal.getAuthorities()).isNotNull();
    }

    @Test
    @DisplayName("Should have default true for account status methods")
    void shouldHaveDefaultTrueForAccountStatusMethods() {
        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should work with builder pattern directly")
    void shouldWorkWithBuilderPatternDirectly() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_MODERATOR")
        );

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "John Doe");
        attributes.put("profile", "https://example.com/profile");

        UserPrincipal principal = UserPrincipal.builder()
                .id(2L)
                .email("builder@example.com")
                .password("builderPassword")
                .authorities(authorities)
                .attributes(attributes)
                .build();

        assertThat(principal.getId()).isEqualTo(2L);
        assertThat(principal.getEmail()).isEqualTo("builder@example.com");
        assertThat(principal.getPassword()).isEqualTo("builderPassword");
        assertThat(principal.getAuthorities()).hasSize(2);
        assertThat(principal.getAttributes()).isEqualTo(attributes);

        Set<String> authorityNames = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertThat(authorityNames).containsExactlyInAnyOrder("ROLE_USER", "ROLE_MODERATOR");
    }

    @Test
    @DisplayName("Should handle null attributes")
    void shouldHandleNullAttributes() {
        UserPrincipal principal = UserPrincipal.builder()
                .id(3L)
                .email("noattributes@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .attributes(null)
                .build();

        assertThat(principal.getAttributes()).isNull();
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void shouldHandleSpecialCharactersInEmail() {
        String specialEmail = "test.user+tag@example-domain.com";
        user.setEmail(specialEmail);

        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal.getEmail()).isEqualTo(specialEmail);
        assertThat(principal.getUsername()).isEqualTo(specialEmail);
    }

    @Test
    @DisplayName("Should handle null user gracefully")
    void shouldHandleNullUserGracefully() {
        assertThatThrownBy(() -> UserPrincipal.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should handle null password")
    void shouldHandleNullPassword() {
        user.setPassword(null);
        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal.getPassword()).isNull();
    }

    @Test
    @DisplayName("Should handle empty password")
    void shouldHandleEmptyPassword() {
        user.setPassword("");
        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal.getPassword()).isEmpty();
    }

    @Test
    @DisplayName("Should work with custom attributes")
    void shouldWorkWithCustomAttributes() {
        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("firstName", "John");
        customAttributes.put("lastName", "Doe");
        customAttributes.put("age", 30);

        UserPrincipal principal = UserPrincipal.builder()
                .id(4L)
                .email("custom@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .attributes(customAttributes)
                .build();

        assertThat(principal.getAttributes()).isEqualTo(customAttributes);
        assertThat(principal.getAttributes().get("firstName")).isEqualTo("John");
        assertThat(principal.getAttributes().get("lastName")).isEqualTo("Doe");
        assertThat(principal.getAttributes().get("age")).isEqualTo(30);
    }

    @Test
    @DisplayName("Should handle very long user ID")
    void shouldHandleVeryLongUserId() {
        Long veryLongId = Long.MAX_VALUE;
        user.setId(veryLongId);

        UserPrincipal principal = UserPrincipal.create(user);

        assertThat(principal.getId()).isEqualTo(veryLongId);
    }
}