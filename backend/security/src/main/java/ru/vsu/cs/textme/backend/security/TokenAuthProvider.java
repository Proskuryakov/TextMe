package ru.vsu.cs.textme.backend.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.hasText;

@Component
@AllArgsConstructor
public class TokenAuthProvider {
    public static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;


    public String getTokenFromHeader(String header) {
        return hasText(header) && header.startsWith("Bearer ") ? header.substring(7) : null;
    }

    public UsernamePasswordAuthenticationToken initAuthToken(HttpServletRequest request) {
        return initAuthToken(request.getHeader(AUTHORIZATION));
    }

    public UsernamePasswordAuthenticationToken initAuthToken(String header) {
        String token = getTokenFromHeader(header);
        if (token != null && jwtProvider.validateToken(token)) {
            var nickname = jwtProvider.getNicknameFromToken(token);
            var details = userDetailsService.loadUserByUsername(nickname);
            var authToken = new UsernamePasswordAuthenticationToken(
                    details,
                    null,
                    details.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return authToken;
        }
        return null;
    }
}
