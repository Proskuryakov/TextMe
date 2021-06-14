package ru.vsu.cs.textme.backend.security;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

@Log
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private final TokenAuthProvider provider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
         logger.info("do filter...");
         var authToken = provider.initAuthToken((HttpServletRequest) servletRequest);
         if (authToken == null) {
             throw new AuthenticationCredentialsNotFoundException("BAD_TOKEN");
         }
         filterChain.doFilter(servletRequest, servletResponse);
    }

}
