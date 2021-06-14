package ru.vsu.cs.textme.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import java.util.Map;

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
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        attributes.put("token", request.getHeaders().getFirst("Authorization"));
                        return true;
                    }
                })
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
