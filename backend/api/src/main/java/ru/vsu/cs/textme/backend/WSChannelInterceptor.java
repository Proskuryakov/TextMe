package ru.vsu.cs.textme.backend;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import ru.vsu.cs.textme.backend.security.TokenAuthProvider;

@Component
@AllArgsConstructor
@Log4j2
public class WSChannelInterceptor implements ChannelInterceptor {
    private final TokenAuthProvider provider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.log(Level.INFO, "CONNECT TO SOCKET");
            var authToken = provider.initAuthToken(
                    accessor.getFirstNativeHeader(TokenAuthProvider.AUTHORIZATION));
            if (authToken == null) {
                log.log(Level.ERROR, "CONNECT TO SOCKET -> BAD TOKEN");

                throw new AuthenticationCredentialsNotFoundException("BAD_TOKEN");
            }
            accessor.setUser(authToken);
            log.log(Level.INFO, "CONNECT TO SOCKET -> SET USER AUTH TOKEN");

        }
        return message;
    }

}
