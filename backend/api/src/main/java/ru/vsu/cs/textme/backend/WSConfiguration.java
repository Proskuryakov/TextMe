package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

//@Configuration
@RequiredArgsConstructor
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WSConfiguration implements WebSocketMessageBrokerConfigurer {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService detailsService;


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WSChannelInterceptor(jwtProvider, detailsService));
    }
}