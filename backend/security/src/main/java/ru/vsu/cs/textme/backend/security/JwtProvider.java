package ru.vsu.cs.textme.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log
@Component
public class JwtProvider {
    public static final String TIME_ZONE = "+00:00";
    public static final ZoneId ZONE = ZoneId.of(TIME_ZONE);
    public static final int EXP_DAYS = 60;

    @Value("$(jwt.secret)")
    private String secret;

    public String generateToken(User user) {
        Date date = Date.from(LocalDate.now()
                        .plusDays(EXP_DAYS)
                        .atStartOfDay(ZONE)
                        .toInstant());
        return Jwts.builder()
                .setSubject(user.getNickname())
                .setExpiration(date)
                .addClaims(generateRoleMap(user.getRoles()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Map<String, Object> generateRoleMap(List<AppRole> roles) {
        return Map.of("roles", roles
                .stream()
                .map((Function<AppRole, Object>) AppRole::getContent)
                .collect(Collectors.toList())
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.severe("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.severe("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.severe("Malformed jwt");
        } catch (SignatureException sEx) {
            log.severe("Invalid signature");
        } catch (Exception e) {
            log.severe("invalid token");
        }
        return false;
    }

    public String getNicknameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}