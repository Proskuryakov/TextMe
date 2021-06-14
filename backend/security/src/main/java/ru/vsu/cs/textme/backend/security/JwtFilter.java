package ru.vsu.cs.textme.backend.security;
import lombok.extern.java.Log;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.services.exception.UserForbiddenException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Log
@Component
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtProvider jwtProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtProvider = jwtProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("do filter...");
        var authToken = getAuthToken(getAuthHeader((HttpServletRequest) servletRequest));
        if (authToken != null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static String getAuthHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    public static String getTokenFromHeader(String header) {
        return hasText(header) && header.startsWith("Bearer ") ? header.substring(7) : null;
    }

    public UsernamePasswordAuthenticationToken getAuthToken(String header) {
        var token = getTokenFromHeader(header);
        if (token != null && jwtProvider.validateToken(token)) {
            String nickname = jwtProvider.getNicknameFromToken(token);
            var details = customUserDetailsService.loadUserByUsername(nickname);
            return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        }
        return null;
    }

}
