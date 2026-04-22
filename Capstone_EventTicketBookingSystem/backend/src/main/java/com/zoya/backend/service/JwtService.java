package com.zoya.backend.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // JWT will use this secret_key to make tokens
    private static final String SECRET_KEY = "a18hU9IpX11BunJ9@meow89x#bZ5security-03mmZqwerty2eR";


    // this method will be called only when log in is successful
    public String generateToken(String email) {

            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            return Jwts.builder()
                            .subject(email) //subject is current logged in user
                            .issuedAt(new Date())
                            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                            .signWith(key)
                            .compact();
    }

    // this method takes token as input and returns emailfor the logged in user 
    public String extractEmail(String token) {

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        }
}
