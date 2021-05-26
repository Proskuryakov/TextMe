package ru.vsu.cs.textme.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import ru.vsu.cs.textme.backend.db.model.AppRole;

import static org.springframework.messaging.simp.SimpMessageType.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/app/**").hasRole(AppRole.USER.getContent())
                .simpSubscribeDestMatchers("user/**").hasRole(AppRole.USER.getContent())
                .simpSubscribeDestMatchers("queue/**").hasRole(AppRole.USER.getContent())
                .simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll()
                .anyMessage().denyAll();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker(
                "/queue/direct/errors",
                "/queue/chat/errors",

                "/user/queue/send/direct",
                "/user/queue/update/direct",
                "/user/queue/delete/direct",
                "/user/queue/read/direct",

                "/user/queue/send/chat",
                "/user/queue/update/chat",
                "/user/queue/delete/chat",
                "/user/queue/read/chat"
        );
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/direct").withSockJS();
        registry.addEndpoint("/chat").withSockJS();
    }
}