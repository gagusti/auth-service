package org.test.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Component
public class JwtManager {
    private final String SECRET_KEY = "dGVzdEBQcm9qZWN0JUtleSRvcmcudGVzdEdvb2RMdWNrZm9ybWU=";
    private final String TOKEN_ISSUER = "org.test";
    private final long SIGNUP_EXPIRATION_TIME = 150_000;
    private final long EXPIRATION_TIME = 900_000;

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public String extractIssuer(String token) {
        return extractAllClaims(token).getIssuer();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .requireIssuer(TOKEN_ISSUER)
                .setSigningKey(TextCodec.BASE64.decode(SECRET_KEY)).build()
                .parseClaimsJws(token).getBody();
    }

    public String generateSignUpToken(String email) {
        return createToken(Map.of("iss", TOKEN_ISSUER), email, SIGNUP_EXPIRATION_TIME);
    }

    public String generateToken(String email) {
        return createToken(Map.of("iss", TOKEN_ISSUER), email, EXPIRATION_TIME);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(SECRET_KEY))
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String email, String token) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(TextCodec.BASE64.decode(SECRET_KEY), SignatureAlgorithm.HS256.getJcaName());
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(secretKeySpec)
                .build();
        try {
            jwtParser.parse(token);
          } catch (Exception e) {
            throw new Exception("Could not verify JWT token integrity!", e);
        }
        return email.equals(extractEmail(token)) && TOKEN_ISSUER.equals(extractIssuer(token));
    }
}
