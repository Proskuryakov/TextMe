package ru.vsu.cs.textme.backend.socket;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import ru.vsu.cs.textme.backend.db.model.MessageUpdate;
import ru.vsu.cs.textme.backend.db.model.request.NewChatMessageRequest;
import ru.vsu.cs.textme.backend.db.model.request.NewDirectMessageRequest;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.exception.DirectException;
import ru.vsu.cs.textme.backend.services.DirectService;

@Controller
public class DirectSocketController {
    private final DirectService directService;
    private final SimpMessagingTemplate template;
    public DirectSocketController(DirectService directService, SimpMessagingTemplate template) {
        this.directService = directService;
        this.template = template;
    }

    @MessageMapping("/direct/send-message/")
    public void sendMessage(@Payload NewDirectMessageRequest request, @AuthenticationPrincipal CustomUserDetails principal) {
        var out = directService.send(principal.getUsername(), request);
        template.convertAndSendToUser(principal.getUsername(), "/queue/messenger/send", out);
        template.convertAndSendToUser(request.getRecipient(), "/queue/messenger/send", out);
    }

    @MessageMapping("/direct/update-message/")
    public void updateMessage(@Payload MessageUpdate message, @AuthenticationPrincipal CustomUserDetails principal) {
        var out = directService.update(principal.getUsername(), message);
        template.convertAndSendToUser(principal.getUsername(), "/queue/messenger/update", out);
        template.convertAndSendToUser(out.getTo().getName(), "/queue/messenger/update", out);
    }

    @MessageMapping("/direct/delete-message/{msgId}")
    public void deleteMessage(@DestinationVariable Integer msgId, @AuthenticationPrincipal CustomUserDetails principal) {
        var out=  directService.deleteBy(principal.getUsername(), msgId);

        template.convertAndSendToUser(principal.getUsername(), "/queue/messenger/delete", msgId);
        template.convertAndSendToUser(out.getTo().getName(), "/queue/messenger/delete", msgId);
    }


    @MessageMapping("/direct/read-message/{msgId}")
    public void readMessage(@DestinationVariable Integer msgId, @AuthenticationPrincipal CustomUserDetails principal) {
        var out=  directService.readBy(principal.getUsername(), msgId);
        template.convertAndSendToUser(principal.getUsername(), "/queue/messenger/read", msgId);
        template.convertAndSendToUser(out.getTo().getName(), "/queue/messenger/read", msgId);
    }


    @MessageExceptionHandler
    @SendToUser(destinations="/queue/direct/errors", broadcast=false)
    public DirectException handleException(DirectException error) {
        return error;
    }
}
