package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.*;
import ru.vsu.cs.textme.backend.security.JwtFilter;

@Component
@RequiredArgsConstructor
@Log4j2
public class WSChannelInterceptor implements ChannelInterceptor {
    private final JwtFilter jwtFilter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getMessageType() != null) switch (accessor.getMessageType()) {
            case CONNECT:
            case DISCONNECT:
            case MESSAGE:
            case UNSUBSCRIBE:
            case SUBSCRIBE: log.log(Level.WARN, "РАБОТАЕТ");
                var auth = jwtFilter.getAuthToken(
                        accessor.getFirstNativeHeader(JwtFilter.AUTHORIZATION));
                if (auth == null) return message;

                accessor.setUser(auth);
                accessor.setLeaveMutable(true);
                return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }
        return message;
    }
}
