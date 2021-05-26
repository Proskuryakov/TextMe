package ru.vsu.cs.textme.backend.socket;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.vsu.cs.textme.backend.db.model.MessageUpdate;
import ru.vsu.cs.textme.backend.services.DirectException;
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

    @MessageMapping("/direct/{to}")
    public void sendMessage(@DestinationVariable String to, @Payload String message, Principal principal) {
        var out = directService.send(principal.getName(), to, message);
        template.convertAndSendToUser(principal.getName(), "/user/queue/send/direct", out);
        template.convertAndSendToUser(to, "/user/queue/send/direct", out);
    }

    @MessageMapping("/direct/update/{to}")
    public void updateMessage(@DestinationVariable String to, @Payload MessageUpdate message, Principal principal) {
        var out = directService.update(principal.getName(), to, message);
        template.convertAndSendToUser(principal.getName(), "/user/queue/update/direct", out);
        template.convertAndSendToUser(to, "/user/queue/update/direct", out);
    }

    @MessageMapping("/direct/delete/{to}")
    public void deleteMessage(@DestinationVariable String to, @Payload Integer id, Principal principal) {
        if ( !directService.deleteBy(principal.getName(), to, id)) return;
        template.convertAndSendToUser(principal.getName(), "/user/queue/delete/direct", id);
        template.convertAndSendToUser(to, "/user/queue/delete/direct", id);
    }


    @MessageMapping("/direct/read/{to}")
    public void read(@DestinationVariable String to, @Payload Integer id, Principal principal) {
        if (!directService.readBy(principal.getName(), to, id)) return;
        template.convertAndSendToUser(principal.getName(), "/user/queue/read/direct", id);
        template.convertAndSendToUser(to, "/user/queue/read/direct", id);
    }


    @MessageExceptionHandler
    @SendToUser(destinations="/queue/direct/errors", broadcast=false)
    public DirectException handleException(DirectException error) {
        return error;
    }
}
