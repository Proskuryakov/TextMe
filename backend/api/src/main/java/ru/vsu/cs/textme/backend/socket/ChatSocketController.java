package ru.vsu.cs.textme.backend.socket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.services.ChatException;
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


    @MessageMapping("/chat/{id}")
    public void sendMessage(@DestinationVariable Integer id, @Payload String content, Principal principal) {
        var msg = chatService.send(id, content, principal.getName());
        for (var member : msg.getChat().getMembers()) {
            if (member.getRole() != ChatRole.ROLE_BLOCKED)
                template.convertAndSendToUser(member.getUser().getNickname(), "/user/queue/send/chat", msg);

        }
    }

    @MessageMapping("/chat/update/{id}")
    public void updateMessage(@DestinationVariable Integer id, @Payload MessageUpdate message, Principal principal) {
        var msg = chatService.update(id, message, principal.getName());
        for (var member : msg.getChat().getMembers()) {
            if (member.getRole() != ChatRole.ROLE_BLOCKED)
                template.convertAndSendToUser(member.getUser().getNickname(), "/user/queue/update/chat", msg);

        }
    }

    @MessageMapping("/chat/delete/{id}")
    public void deleteMessage(@DestinationVariable Integer id, @Payload Integer message, Principal principal) {
        var msg = chatService.deleteBy(principal.getName(), id, message);
        if (msg != null)
            for (var member : msg.getChat().getMembers())
                if (member.getRole() != ChatRole.ROLE_BLOCKED)
                    template.convertAndSendToUser(member.getUser().getNickname(), "/user/queue/delete/chat", msg);
    }


    @MessageMapping("/chat/read/{id}")
    public void read(@DestinationVariable Integer id, @Payload Integer message, Principal principal) {
        var msg = chatService.readBy(principal.getName(), id, message);
        if (msg != null)
            for (var member : msg.getChat().getMembers())
                if (member.getRole() != ChatRole.ROLE_BLOCKED)
                    template.convertAndSendToUser(member.getUser().getNickname(), "/user/queue/read/chat", msg);
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/chat/errors", broadcast=false)
    public ChatException handleException(ChatException error) {
        return error;
    }
}
