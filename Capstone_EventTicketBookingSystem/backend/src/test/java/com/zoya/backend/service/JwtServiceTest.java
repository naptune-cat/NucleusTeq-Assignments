package com.zoya.backend.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    // 32-char secret → valid HS256 key
    private static final String SECRET = "TestSecretKeyForJwtUnitTesting12";
    private static final long EXPIRATION_MS = 1000L * 60 * 30; // 30 minutes

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    // tests for token generation

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");

        assertThat(token).isNotNull().isNotBlank();
    }

    //  additional test to verify token structure (3 parts separated by dots)
    @Test
    void generateToken_returnsThreePartJwt() {
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");

        // JWT = header.payload.signature
        assertThat(token.split("\\.")).hasSize(3);
    }

    // different emails should produce different tokens
    @Test
    void generateToken_differentEmailsProduceDifferentTokens() {
        String token1 = jwtService.generateToken("alice@test.com", "CUSTOMER");
        String token2 = jwtService.generateToken("bob@test.com", "CUSTOMER");

        assertThat(token1).isNotEqualTo(token2);
    }

    // different roles should also produce different tokens even if email is same
    @Test
    void generateToken_differentRolesProduceDifferentTokens() {
        String token1 = jwtService.generateToken("user@test.com", "CUSTOMER");
        String token2 = jwtService.generateToken("user@test.com", "ORGANIZER");

        assertThat(token1).isNotEqualTo(token2);
    }

    // tests for extracting email and role from token

    @Test
    void extractEmail_returnsCorrectEmail() {
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");

        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("user@test.com");
    }

    // additional test to verify email is correctly embedded and extracted
    @Test
    void extractEmail_roundTrip_preservesEmail() {
        String expected = "organizer@company.com";
        String token = jwtService.generateToken(expected, "ORGANIZER");

        assertThat(jwtService.extractEmail(token)).isEqualTo(expected);
    }

    // extract role tests

    @Test
    void extractRole_returnsCustomerRole() {
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("CUSTOMER");
    }

    // additional test to verify organizer role is extracted correctly
    @Test
    void extractRole_returnsOrganizerRole() {
        String token = jwtService.generateToken("org@test.com", "ORGANIZER");

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("ORGANIZER");
    }

    // round-trip test to ensure role is correctly embedded and extracted
    @Test
    void extractRole_roundTrip_preservesRole() {
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");

        assertThat(jwtService.extractRole(token)).isEqualTo("CUSTOMER");
    }

    //tests for expired tokens

    @Test
    void extractEmail_throwsExpiredJwtException_whenTokenExpired() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L); // already expired
        String expiredToken = jwtService.generateToken("user@test.com", "CUSTOMER");

        // Reset to normal so parsing uses same key
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);

        assertThatThrownBy(() -> jwtService.extractEmail(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    // role extraction should also fail for expired tokens
    @Test
    void extractRole_throwsExpiredJwtException_whenTokenExpired() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String expiredToken = jwtService.generateToken("user@test.com", "CUSTOMER");

        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);

        assertThatThrownBy(() -> jwtService.extractRole(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    // checking invalid tokens

    @Test
    void extractEmail_throwsException_whenTokenIsGarbage() {
        assertThatThrownBy(() -> jwtService.extractEmail("not.a.jwt"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void extractRole_throwsException_whenTokenTampered() {
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThatThrownBy(() -> jwtService.extractRole(tampered))
                .isInstanceOf(Exception.class);
    }

    // test to verify that token signed with different key cannot be parsed
    @Test
    void extractEmail_throwsException_whenSignedWithDifferentKey() {
        // Generate token with current key
        String token = jwtService.generateToken("user@test.com", "CUSTOMER");

        // Swap key to simulate wrong-key scenario
        ReflectionTestUtils.setField(jwtService, "secretKey", "DifferentSecretKeyForJwtTest1234");

        assertThatThrownBy(() -> jwtService.extractEmail(token))
                .isInstanceOf(Exception.class);
    }
}