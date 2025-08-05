package com.auth.template.security;

import com.auth.template.exception.AuthException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        String userName = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

    }

    public String getUserNameFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "JWT Claims string is empty.");
        }
    }
}
