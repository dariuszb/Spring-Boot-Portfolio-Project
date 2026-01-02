package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.example.exceptions.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key secret;

    private Jws<Claims> claimsJws;

    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtil(@Value("${jwt.secret}")String secretString) {
        if (secretString.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 characters long");
        }
        secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

    }

    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid token");
        }
    }

    public String getUserName(String token) {
        return getClaimsFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimsFromToken(String token,
                                     Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = claimsJws.getBody();

            return claimsResolver.apply(claims);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid token");
        }
    }
}
