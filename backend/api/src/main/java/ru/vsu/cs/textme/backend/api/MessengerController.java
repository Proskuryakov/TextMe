package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.MessengerService;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController()
@RequestMapping("/api/messenger")
public class MessengerController {
    private final MessengerService messengerService;

    public MessengerController(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/list/{page}/{type}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageInfo> getList(@AuthenticationPrincipal CustomUserDetails details,
                                     @PathVariable @Valid @Size int page,
                                     @PathVariable String type) {
        var list = Collections.<MessageInfo>emptyList();
        switch (type) {
            case "chat": list =  messengerService.getChats(details.getUser().getId(), page); break;
            case "direct": list = messengerService.getDirects(details.getUser().getId(), page); break;
            case "all": {
                var chats = messengerService.getChats(details.getUser().getId(), page);
                var directs =  messengerService.getDirects(details.getUser().getId(), page);
                chats.addAll(directs);
                list = chats;
            }
        }
        list.sort(Comparator.comparing(o -> o.getMessage().getDateCreate()));
        Collections.reverse(list);
        return list;
    }

    @GetMapping("/direct/{id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageInfo> getDirectPage(@AuthenticationPrincipal CustomUserDetails details,
                                           @PathVariable Integer id,
                                           @PathVariable Integer page) {
        return messengerService.getDirectPage(details.getUser().getId(), id, page);
    }

    @GetMapping("/chat/{id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageInfo> getChatPage(@AuthenticationPrincipal CustomUserDetails details,
                                             @PathVariable Integer id,
                                             @PathVariable Integer page) {
        return messengerService.getChatPage(details.getUser().getId(), id, page);
    }
}
