package ru.vsu.cs.textme.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Configuration
@EnableWebSocketMessageBroker
public class WSSecurity extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    protected boolean sameOriginDisabled() {
        return true;
    }
}
