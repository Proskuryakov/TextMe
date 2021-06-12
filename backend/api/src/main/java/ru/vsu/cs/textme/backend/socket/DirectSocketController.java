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
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/send", out);
        template.convertAndSendToUser(request.getRecipient(), "/queue/direct/send", out);
    }

    @MessageMapping("/direct/update-message/")
    public void updateMessage(@Payload MessageUpdate message, @AuthenticationPrincipal CustomUserDetails principal) {
        var out = directService.update(principal.getUsername(), message);
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/update", out);
        template.convertAndSendToUser(out.getTo().getName(), "/queue/direct/update", out);
    }

    @MessageMapping("/direct/delete-message/#{id}")
    public void deleteMessage(@Payload Integer id, @AuthenticationPrincipal CustomUserDetails principal) {
        var out=  directService.deleteBy(principal.getUsername(), id);

        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/delete", id);
        template.convertAndSendToUser(out.getTo().getName(), "/queue/direct/delete", id);
    }


    @MessageMapping("/direct/read-message/#{id}")
    public void readMessage(@Payload Integer id, @AuthenticationPrincipal CustomUserDetails principal) {
        var out=  directService.readBy(principal.getUsername(), id);
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/read", id);
        template.convertAndSendToUser(out.getTo().getName(), "/queue/direct/read", id);
    }


    @MessageExceptionHandler
    @SendToUser(destinations="/queue/direct/errors", broadcast=false)
    public DirectException handleException(DirectException error) {
        return error;
    }
}
