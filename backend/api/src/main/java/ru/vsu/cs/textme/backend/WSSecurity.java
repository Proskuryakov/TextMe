package ru.vsu.cs.textme.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtProvider;

@Configuration
@EnableWebSocketMessageBroker
public class WSSecurity extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    private final JwtProvider provider;
    private final CustomUserDetailsService detailsService;

    public WSSecurity(JwtProvider provider, CustomUserDetailsService detailsService) {
        this.provider = provider;
        this.detailsService = detailsService;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().authenticated();
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
                .setHandshakeHandler(new WSDefaultHandshakeHandler(provider, detailsService))
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setSupressCors(true);
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
