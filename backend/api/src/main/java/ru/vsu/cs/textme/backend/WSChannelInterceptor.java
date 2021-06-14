package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.vsu.cs.textme.backend.security.JwtFilter;

@Component
@RequiredArgsConstructor
public class WSChannelInterceptor implements ChannelInterceptor {
    private final JwtFilter jwtFilter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        var token = jwtFilter.getAuthToken(
                accessor.getFirstNativeHeader(JwtFilter.AUTHORIZATION));
        if (token != null) {
            SecurityContextHolder.getContext().setAuthentication(token);
            accessor.setUser(token);
        }
        return message;
    }

}
