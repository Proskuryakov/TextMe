package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import ru.vsu.cs.textme.backend.db.model.AppRole;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WSSecurity extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    private final WSDefaultHandshakeHandler handshakeHandler;

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().hasRole(AppRole.USER.getContent());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/user");
        registry.setUserDestinationPrefix("/user");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setSupressCors(true);
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
