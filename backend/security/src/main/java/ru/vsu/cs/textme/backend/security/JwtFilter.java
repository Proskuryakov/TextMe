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
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && jwtProvider.validateToken(token)) {
            String nickname = jwtProvider.getNicknameFromToken(token);
            CustomUserDetails details = customUserDetailsService.loadUserByUsername(nickname);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static String getTokenFromRequest(HttpServletRequest request) {
        return getTokenFromHeader(request.getHeader(AUTHORIZATION));
    }

    public static String getTokenFromHeader(String header) {
        return hasText(header) && header.startsWith("Bearer ") ? header.substring(7) : null;
    }

    public static String getTokenFromHeaders(List<String> headers) {
        return headers == null || headers.isEmpty() ? null : getTokenFromHeader(headers.get(0));
    }
}
