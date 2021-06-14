package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtFilter;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WSDefaultHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtProvider provider;
    private final CustomUserDetailsService detailsService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler handler, Map<String, Object> attr) {
        var header = attr.get("token").toString();
        var token = JwtFilter.getTokenFromHeader(header);
        var nickname = token != null && provider.validateToken(token) ?
                provider.getNicknameFromToken(token) : "";
        return detailsService.loadUserByUsername(nickname);
    }
}
