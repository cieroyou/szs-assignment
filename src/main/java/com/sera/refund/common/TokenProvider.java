package com.sera.refund.common;

import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {
    // TODO: secret key, expiration day 환경변수설정
    private final String SECRET_KEY = "kDuoRf8S0NZsv9smpEePYDNd3icYD3hC";
    private final Long EXPIRATION_DAY = 90L;

    public String generateToken(String userId) {
        long expirationMillis = EXPIRATION_DAY * 24 * 60 * 60 * 1000;

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT에서 userId 추출
    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

}
