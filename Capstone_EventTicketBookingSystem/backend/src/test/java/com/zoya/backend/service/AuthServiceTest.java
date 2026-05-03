package com.zoya.backend.service;

import com.zoya.backend.dto.LoginRequest;
import com.zoya.backend.dto.LoginResponse;
import com.zoya.backend.dto.RegisterRequest;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.UserRole;
import com.zoya.backend.exception.InvalidPasswordException;
import com.zoya.backend.exception.UserAlreadyExistsException;
import com.zoya.backend.exception.UserNotFoundException;
import com.zoya.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("rawPassword");
        registerRequest.setPhone("9876543210");
        // no organizer code by default → role = CUSTOMER

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("rawPassword");

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setRole(UserRole.CUSTOMER);
        existingUser.setName("Test User");
    }

    // test for registerUser with unique email

    @Test
    void registerUser_success_whenEmailNotTaken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        String result = authService.registerUser(registerRequest);

        assertThat(result).isEqualTo("User registered Successfully!");
        verify(userRepository).save(any(User.class));
    }

    //  test to verify password is encoded before saving
    @Test
    void registerUser_encodesPassword_beforeSaving() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        authService.registerUser(registerRequest);

        verify(userRepository).save(argThat(u -> "encodedPassword".equals(u.getPassword())));
    }

    // test to verify name is saved correctly
    @Test
    void registerUser_assignsCustomerRole_byDefault() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        authService.registerUser(registerRequest);

        verify(userRepository).save(argThat(u -> u.getRole() == UserRole.CUSTOMER));
    }

    // test for organizer code assigning organizer role
    @Test
    void registerUser_assignsOrganizerRole_whenCorrectCodeProvided() {
        registerRequest.setOrganizerCode("PartyPalooza9988");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        authService.registerUser(registerRequest);

        verify(userRepository).save(argThat(u -> u.getRole() == UserRole.ORGANIZER));
    }

    // additional test to verify wrong organizer code does not assign organizer role
    @Test
    void registerUser_assignsCustomerRole_whenWrongOrganizerCodeProvided() {
        registerRequest.setOrganizerCode("wrongCode");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        authService.registerUser(registerRequest);

        verify(userRepository).save(argThat(u -> u.getRole() == UserRole.CUSTOMER));
    }

    // test for duplicate email
    @Test
    void registerUser_throwsUserAlreadyExistsException_whenEmailAlreadyRegistered() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authService.registerUser(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository, never()).save(any());
    }
    // additional test to verify phone number is saved correctly
    @Test
    void registerUser_savesPhoneNumber() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        authService.registerUser(registerRequest);

        verify(userRepository).save(argThat(u -> "9876543210".equals(u.getPhone())));
    }

    // test for loginUser
    @Test
    void loginUser_returnsLoginResponse_whenCredentialsValid() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("test@example.com", "CUSTOMER")).thenReturn("jwt-token");

        LoginResponse response = authService.loginUser(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void loginUser_returnsSuccessMessage() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        LoginResponse response = authService.loginUser(loginRequest);

        assertThat(response.getMessage()).isEqualTo("Login successful");
    }

    @Test
    void loginUser_callsJwtService_withEmailAndRole() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("test@example.com", "CUSTOMER")).thenReturn("jwt-token");

        authService.loginUser(loginRequest);

        verify(jwtService).generateToken("test@example.com", "CUSTOMER");
    }

    @Test
    void loginUser_throwsUserNotFoundException_whenEmailNotRegistered() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        loginRequest.setEmail("notfound@example.com");

        assertThatThrownBy(() -> authService.loginUser(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void loginUser_throwsInvalidPasswordException_whenPasswordWrong() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        loginRequest.setPassword("wrongPassword");

        assertThatThrownBy(() -> authService.loginUser(loginRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    void loginUser_doesNotGenerateToken_whenPasswordInvalid() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.loginUser(loginRequest));

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginUser_worksForOrganizerRole() {
        existingUser.setRole(UserRole.ORGANIZER);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("test@example.com", "ORGANIZER")).thenReturn("organizer-token");

        LoginResponse response = authService.loginUser(loginRequest);

        assertThat(response.getRole()).isEqualTo("ORGANIZER");
        assertThat(response.getToken()).isEqualTo("organizer-token");
    }
}