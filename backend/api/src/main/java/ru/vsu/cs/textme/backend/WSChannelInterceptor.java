package ru.vsu.cs.textme.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtFilter;
import ru.vsu.cs.textme.backend.security.JwtProvider;

public class WSChannelInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService detailsService;

    public WSChannelInterceptor(JwtProvider jwtProvider, CustomUserDetailsService detailsService) {
        this.jwtProvider = jwtProvider;
        this.detailsService = detailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var token = JwtFilter.getTokenFromHeaders(accessor.getNativeHeader("Authorization"));
            accessor.removeNativeHeader("Authorization");

            if (token != null && jwtProvider.validateToken(token)) {
                var nickname = jwtProvider.getNicknameFromToken(token);
                var details = detailsService.loadUserByUsername(nickname);
                var tok = new UsernamePasswordAuthenticationToken(details, details);
                SecurityContextHolder.getContext().setAuthentication(tok);
                accessor.setUser(details);
            }
        }
        return message;
    }

}
