package com.user_microservice.user.infrastructure.security.service;

import com.user_microservice.user.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String secretKey = "VGhpcyBJcyBBIFZlcnkgU2VjdXJlIEtleSBUaGF0IE5lZWRzIHRvIGJlIDQyIGJ5dGVz";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    }

    @Test
    void generateToken_ValidUser_ShouldReturnValidToken() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_USER");

        String token = jwtService.generateToken(user, extraClaims);

        assertNotNull(token);
        assertFalse(token.isEmpty());

    }

    @Test
    void extractUsername_ValidToken_ShouldReturnUserId() {

        Long userId = 1L;
        String token = generateTestToken(userId);

        String extractedUserId = jwtService.extractUsername(token);


        assertEquals(userId.toString(), extractedUserId);
    }

    @Test
    void isTokenValid_ValidToken_ShouldReturnTrue() {

        String token = generateTestToken(1L);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_ExpiredToken_ShouldReturnFalse() {

        String expiredToken = generateExpiredTestToken(1L);

        assertFalse(jwtService.isTokenValid(expiredToken));
    }

    @Test
    void isTokenValid_InvalidToken_ShouldReturnFalse() {

        String invalidToken = "invalid.token.value";

        assertFalse(jwtService.isTokenValid(invalidToken));
    }


    private String generateTestToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey));
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    private String generateExpiredTestToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey));
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 20))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}