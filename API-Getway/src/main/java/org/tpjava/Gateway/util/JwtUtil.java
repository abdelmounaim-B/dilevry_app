package org.tpjava.Gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;


    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token: " + token);
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            System.out.println("Token is valid.");
            return true;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
