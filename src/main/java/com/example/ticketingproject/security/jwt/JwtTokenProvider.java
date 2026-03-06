package com.example.ticketingproject.security.jwt;

import com.example.ticketingproject.security.CustomUserDetails;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String SECRET = "ticketing-project-test-secretKey";
    private final long EXPIRATION = 1000 * 60 * 60;

    public String createToken(CustomUserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getEmail())
                .claim("userId", userDetails.getId())
                .claim("role", userDetails.getUserRole().name())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
