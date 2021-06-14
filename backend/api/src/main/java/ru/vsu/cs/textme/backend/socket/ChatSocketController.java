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
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;
import ru.vsu.cs.textme.backend.db.model.request.NewMessageRequest;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.exception.ChatException;
import ru.vsu.cs.textme.backend.services.ChatService;

import java.security.Principal;
import java.util.List;

import static ru.vsu.cs.textme.backend.db.model.info.MessageInfo.DestinationType.CHAT;

@Controller
public class ChatSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    public ChatSocketController(ChatService chatService, SimpMessagingTemplate template) {
        this.chatService = chatService;
        this.template = template;
    }


    @MessageMapping("/chat/send-message/")
    public void sendMessage(@Payload NewMessageRequest request, Principal principal) {
        var details = (CustomUserDetails) principal;

        var msg = chatService.send(details.getUser().getId(), request);
        if (msg == null) return;

        var response = msg.getInfo();
        response.setDestination(CHAT);
        sendAll(msg.getMembers(), response, "send");
    }

    @MessageMapping("/chat/update-message/")
    public void updateMessage(@Payload MessageUpdate message, @AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.update(principal.getUser().getId(), message);
        if (msg == null) return;

        var response = msg.getInfo();
        response.setDestination(CHAT);
        sendAll(msg.getMembers(), response, "update");
    }

    @MessageMapping("/chat/delete-message/{msgId}")
    public void deleteMessage(@DestinationVariable Integer msgId, @AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.deleteBy(principal.getUser().getId(), msgId);
        var response = msg.getInfo();
        response.setDestination(CHAT);
        sendAll(msg.getMembers(), response, "delete");
    }

    @MessageMapping("/chat/read-message/{msgId}")
    public void read(@DestinationVariable Integer msgId, @AuthenticationPrincipal CustomUserDetails principal) {
        var msg = chatService.readBy(principal.getUser().getId(), msgId);
        if (msg == null) return;

        var response = msg.getInfo();
        response.setDestination(CHAT);
        sendAll(msg.getMembers(), response, "read");
    }

    private void sendAll(List<ChatMemberInfo> list, Object obj, String path) {
        for (var info : list) {
            if (info.canRead()) {
                template.convertAndSendToUser(
                        info.getMember().getId().toString(),
                        "/queue/messenger/" + path, obj);
            }
        }
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/chat/errors", broadcast=false)
    public ChatException handleException(ChatException error) {
        return error;
    }
}
