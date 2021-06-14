package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.request.async.SecurityContextCallableProcessingInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtFilter;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WSDefaultHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler handler, Map<String, Object> attr) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        var principal = auth.getPrincipal();
        return (CustomUserDetails) principal;
    }
}
