package ru.vsu.cs.textme.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.security.CustomUserDetailsService;
import ru.vsu.cs.textme.backend.security.JwtProvider;

import javax.servlet.http.HttpServletRequest;
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
    protected boolean sameOriginDisabled() {
        return true;
    }
}
