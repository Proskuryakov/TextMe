package ru.vsu.cs.textme.backend.socket;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import ru.vsu.cs.textme.backend.db.model.MessageUpdate;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.exception.DirectException;
import ru.vsu.cs.textme.backend.services.DirectService;

import java.security.Principal;

@Controller
public class DirectSocketController {
    private final DirectService directService;
    private final SimpMessagingTemplate template;
    public DirectSocketController(DirectService directService, SimpMessagingTemplate template) {
        this.directService = directService;
        this.template = template;
    }

    @MessageMapping("/direct/send/{to}")
    public void sendMessage(@DestinationVariable String to, @Payload String message, @AuthenticationPrincipal CustomUserDetails principal) {
        var out = directService.send(principal.getUsername(), to, message);
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/send", out);
        template.convertAndSendToUser(to, "/queue/direct/send", out);
    }

    @MessageMapping("/direct/update/{to}")
    public void updateMessage(@DestinationVariable String to, @Payload MessageUpdate message, @AuthenticationPrincipal CustomUserDetails principal) {
        var out = directService.update(principal.getUsername(), to, message);
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/update", out);
        template.convertAndSendToUser(to, "/queue/direct/update", out);
    }

    @MessageMapping("/direct/delete/{to}")
    public void deleteMessage(@DestinationVariable String to, @Payload Integer id, @AuthenticationPrincipal CustomUserDetails principal) {
        if ( !directService.deleteBy(principal.getUsername(), to, id)) return;
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/delete", id);
        template.convertAndSendToUser(to, "/queue/direct/delete", id);
    }


    @MessageMapping("/direct/read/{to}")
    public void read(@DestinationVariable String to, @Payload Integer id,  @AuthenticationPrincipal CustomUserDetails principal) {
        if (!directService.readBy(principal.getUsername(), to, id)) return;
        template.convertAndSendToUser(principal.getUsername(), "/queue/direct/read", id);
        template.convertAndSendToUser(to, "/queue/direct/read", id);
    }


    @MessageExceptionHandler
    @SendToUser(destinations="/queue/direct/errors", broadcast=false)
    public DirectException handleException(DirectException error) {
        return error;
    }
}
