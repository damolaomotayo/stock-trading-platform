package stock_trading.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import stock_trading.service.CustomUserDetailService;
import stock_trading.util.JwtTokenUtil;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should set authentication when valid JWT token is provided")
    void shouldSetAuthenticationWhenValidJwtTokenIsProvided() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String email = "user@example.com";
        UserDetails userDetails = mock(UserDetails.class);

        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);
        when(jwtTokenUtil.getEmailFromToken(validToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken(validToken);
        verify(jwtTokenUtil).getEmailFromToken(validToken);
        verify(userDetailsService).loadUserByUsername(email);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities()).isEqualTo(userDetails.getAuthorities());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when no Authorization header is present")
    void shouldNotSetAuthenticationWhenNoAuthorizationHeaderIsPresent() throws ServletException, IOException {

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when Authorization header is empty")
    void shouldNotSetAuthenticationWhenAuthorizationHeaderIsEmpty() throws ServletException, IOException {
        request.addHeader("Authorization", "");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when Authorization header does not start with Bearer")
    void shouldNotSetAuthenticationWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic base64encodedcredentials");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when JWT token is invalid")
    void shouldNotSetAuthenticationWhenJwtTokenIsInvalid() throws ServletException, IOException {
        String invalidToken = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + invalidToken);

        when(jwtTokenUtil.validateToken(invalidToken)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken(invalidToken);
        verify(jwtTokenUtil, never()).getEmailFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when user is not found")
    void shouldNotSetAuthenticationWhenUserIsNotFound() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String email = "nonexistent@example.com";
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);
        when(jwtTokenUtil.getEmailFromToken(validToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenThrow(new UsernameNotFoundException("User not found"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken(validToken);
        verify(jwtTokenUtil).getEmailFromToken(validToken);
        verify(userDetailsService).loadUserByUsername(email);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle token with only Bearer keyword")
    void shouldHandleTokenWithOnlyBearerKeyword() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }


    @Test
    @DisplayName("Should set authentication details with web authentication details")
    void shouldSetAuthenticationDetailsWithWebAuthenticationDetails() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String email = "user@example.com";
        UserDetails userDetails = mock(UserDetails.class);

        request.addHeader("Authorization", "Bearer " + validToken);
        request.setRemoteAddr("192.168.1.1");
        request.setRequestedSessionId("test-session-id");

        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);
        when(jwtTokenUtil.getEmailFromToken(validToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getDetails()).isNotNull();
        assertThat(authentication.getDetails()).isInstanceOf(WebAuthenticationDetails.class);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle case when JWT token validation throws exception")
    void shouldHandleCaseWhenJwtTokenValidationThrowsException() throws ServletException, IOException {
        String malformedToken = "malformed.jwt.token";
        request.addHeader("Authorization", "Bearer " + malformedToken);

        when(jwtTokenUtil.validateToken(malformedToken)).thenThrow(new RuntimeException("Token validation error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken(malformedToken);
        verify(jwtTokenUtil, never()).getEmailFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle case when getting email from token throws exception")
    void shouldHandleCaseWhenGettingEmailFromTokenThrowsException() throws ServletException, IOException {
        String validButCorruptedToken = "valid.but.corrupted.token";
        request.addHeader("Authorization", "Bearer " + validButCorruptedToken);

        when(jwtTokenUtil.validateToken(validButCorruptedToken)).thenReturn(true);
        when(jwtTokenUtil.getEmailFromToken(validButCorruptedToken)).thenThrow(new RuntimeException("Failed to extract email"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken(validButCorruptedToken);
        verify(jwtTokenUtil).getEmailFromToken(validButCorruptedToken);
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain even when exception occurs")
    void shouldContinueFilterChainEvenWhenExceptionOccurs() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer valid.token");

        when(jwtTokenUtil.validateToken(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response); // Should always be called
    }

    @Test
    @DisplayName("Should handle token with special characters")
    void shouldHandleTokenWithSpecialCharacters() throws ServletException, IOException {
        String tokenWithSpecialChars = "token.with.special.chars-123_abc";
        request.addHeader("Authorization", "Bearer " + tokenWithSpecialChars);

        when(jwtTokenUtil.validateToken(tokenWithSpecialChars)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken(tokenWithSpecialChars);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle multiple authorization headers by using first one")
    void shouldHandleMultipleAuthorizationHeadersByUsingFirstOne() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer first.token");
        request.addHeader("Authorization", "Bearer second.token");

        when(jwtTokenUtil.validateToken("first.token")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil).validateToken("first.token");
        verify(jwtTokenUtil, never()).validateToken("second.token");
        verify(filterChain).doFilter(request, response);
    }
}