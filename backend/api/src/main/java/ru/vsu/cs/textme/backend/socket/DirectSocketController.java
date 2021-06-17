package ru.vsu.cs.textme.backend.socket;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import ru.vsu.cs.textme.backend.db.model.MessageUpdate;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;
import ru.vsu.cs.textme.backend.db.model.request.NewMessageRequest;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.exception.DirectException;
import ru.vsu.cs.textme.backend.services.DirectService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class DirectSocketController {
    private final DirectService directService;
    private final SimpMessagingTemplate template;
    public DirectSocketController(DirectService directService, SimpMessagingTemplate template) {
        this.directService = directService;
        this.template = template;
    }

    @MessageMapping("/direct/send-message/")
    public void sendMessage(@Payload @Valid NewMessageRequest request, Principal principal) {
        var auth = (Authentication) principal;
        var details = (CustomUserDetails) auth.getPrincipal();
        var id = details.getUser().getId();
        var out = directService.send(id, request);
        send(id, out.getTo().getId(), out, "send");
    }

    @MessageMapping("/direct/update-message/")
    public void updateMessage(@Payload @Valid MessageUpdate message, Principal principal) {
        var auth = (Authentication) principal;
        var details = (CustomUserDetails) auth.getPrincipal();
        var id = details.getUser().getId();
        var out = directService.update(id, message);
        send(id, out.getTo().getId(), out, "update");
    }

    @MessageMapping("/direct/delete-message/{msgId}")
    public void deleteMessage(@DestinationVariable Integer msgId, Principal principal) {
        var auth = (Authentication) principal;
        var details = (CustomUserDetails) auth.getPrincipal();
        var id = details.getUser().getId();
        var out=  directService.deleteBy(id, msgId);
        send(id, out.getTo().getId(), out, "delete");
    }

    @MessageMapping("/direct/read-message/{msgId}")
    public void readMessage(@DestinationVariable Integer msgId, Principal principal) {
        var auth = (Authentication) principal;
        var details = (CustomUserDetails) auth.getPrincipal();
        var id = details.getUser().getId();
        var out=  directService.readBy(id, msgId);
        send(id, out.getFrom().getId(), out, "read");
    }

    private void send(Integer from, Integer to, MessageInfo obj, String path) {
        template.convertAndSendToUser(from.toString(), "/queue/messenger/" + path, obj);
        if (obj.selfMessage()) return;
        template.convertAndSendToUser(to.toString(), "/queue/messenger/" + path, obj);
    }


    @MessageExceptionHandler
    @SendToUser(destinations="/queue/direct/errors")
    public DirectException handleException(DirectException error) {
        return error;
    }
}
