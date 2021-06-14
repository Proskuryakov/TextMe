package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class WSDefaultHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler handler, Map<String, Object> attr) {
        log.log(Level.INFO, "DETERMINE USER PRINCIPAL");

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        var principal = auth.getPrincipal();
        return (CustomUserDetails) principal;
    }
}
