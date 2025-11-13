package com.example.Lab4.security;

import com.example.Lab4.model.UserAccount;
import com.example.Lab4.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expirationMillis;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
                      @Value("${app.security.jwt.expiration:3600000}") long expirationMillis) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Set<UserRole> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Iterable<?> iterable) {
            return java.util.stream.StreamSupport.stream(iterable.spliterator(), false)
                    .map(Object::toString)
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserAccount user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Enum::name).toList());
        claims.put("name", user.getFullName());
        return buildToken(claims, user.getEmail());
    }

    public String generateToken(UserDetails userDetails, Set<UserRole> roles, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(Enum::name).toList());
        claims.put("name", name);
        return buildToken(claims, userDetails.getUsername());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

