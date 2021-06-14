package ru.vsu.cs.textme.backend;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtFilter;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import java.security.Principal;
import java.util.Map;

public class WSDefaultHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtProvider provider;
    private final CustomUserDetailsService detailsService;

    public WSDefaultHandshakeHandler(JwtProvider provider, CustomUserDetailsService detailsService) {
        this.provider = provider;
        this.detailsService = detailsService;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler handler, Map<String, Object> attr) {
        var header = request.getHeaders().getFirst("Authorization");
        var token = JwtFilter.getTokenFromHeader(header);
        var nickname = token != null && provider.validateToken(token) ?
                provider.getNicknameFromToken(token) : "";
        return detailsService.loadUserByUsername(nickname);
    }
}
