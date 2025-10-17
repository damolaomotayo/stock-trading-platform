package stock_trading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;


    public AuthResponse createUser(CreateUserRequest request) {
        checkUserExists(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .roles(new HashSet<>())
                .build();

        User newUser = userRepository.save(user);

        LoginRequest loginRequest = LoginRequest
                .builder()
                .email(newUser.getEmail())
                .password(request.getPassword())
                .build();

        return authenticateUser(loginRequest);
    }

    public AuthResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenUtil.generateToken(authentication);

        return new AuthResponse(token);
    }

    public UserRecord getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return mapToUserRecord(user);
    }

    private void checkUserExists(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistsException("email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserExistsException("username already in use");
        }
    }

    private UserRecord mapToUserRecord(User user) {
        return UserRecord.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
