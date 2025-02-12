package com.example.autofinderbot.web.security;

import com.example.autofinderbot.shared.DateTimeUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long jwtExpirationInMs;
    private final DateTimeUtil dateTimeUtil;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-in-ms}") long jwtExpirationInMs,
                            DateTimeUtil dateTimeUtil) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
        this.dateTimeUtil = dateTimeUtil;
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = dateTimeUtil.convert(dateTimeUtil.now());
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
        return true;
    }
}
