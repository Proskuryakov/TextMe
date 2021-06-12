package ru.vsu.cs.textme.backend.socket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.ChatMessageInfo;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.exception.ChatException;
import ru.vsu.cs.textme.backend.services.ChatService;

import java.security.Principal;

@Controller
public class ChatSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    public ChatSocketController(ChatService chatService, SimpMessagingTemplate template) {
        this.chatService = chatService;
        this.template = template;
    }


    @MessageMapping("/chat/send/{id}")
    public void sendMessage(@DestinationVariable Integer id, @Payload String content, @AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.send(id, content, principal.getUsername());
        var response = new ChatMessageInfo(msg.getUser(), msg.getChat().getInfo(), msg.getMessage());
        for (var member : msg.getChat().getMembers()) {
            if (member.getRole() != ChatRole.ROLE_BLOCKED)
                template.convertAndSendToUser(member.getMember().getName(), "/queue/chat/send", response);

        }
    }

    @MessageMapping("/chat/update/{id}")
    public void updateMessage(@DestinationVariable Integer id, @Payload MessageUpdate message,@AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.update(id, message, principal.getUsername());
        var response = new ChatMessageInfo(msg.getUser(), msg.getChat().getInfo(), msg.getMessage());
        for (var member : msg.getChat().getMembers()) {
            if (member.getRole() != ChatRole.ROLE_BLOCKED)
                template.convertAndSendToUser(member.getMember().getName(), "/queue/chat/update", response);

        }
    }

    @MessageMapping("/chat/delete/{id}")
    public void deleteMessage(@DestinationVariable Integer id, @Payload Integer message,@AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.deleteBy(principal.getUsername(), id, message);
        if (msg != null) {
            var response = new ChatMessageInfo(msg.getUser(), msg.getChat().getInfo(), msg.getMessage());
            for (var member : msg.getChat().getMembers()) {
                if (member.getRole() != ChatRole.ROLE_BLOCKED)
                    template.convertAndSendToUser(member.getMember().getName(), "/queue/chat/delete", response);
            }
        }
    }

    @MessageMapping("/chat/read/{id}")
    public void read(@DestinationVariable Integer id, @Payload Integer message, @AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.readBy(principal.getUsername(), id, message);
        if (msg != null) {
            var response = new ChatMessageInfo(msg.getUser(), msg.getChat().getInfo(), msg.getMessage());
            for (var member : msg.getChat().getMembers()) {
                if (member.getRole() != ChatRole.ROLE_BLOCKED)
                    template.convertAndSendToUser(member.getMember().getName(), "/queue/chat/read", response);
            }
        }
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/chat/errors", broadcast=false)
    public ChatException handleException(ChatException error) {
        return error;
    }
}
