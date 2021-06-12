package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.textme.backend.db.model.DirectMessage;
import ru.vsu.cs.textme.backend.db.model.info.ChatMessageInfo;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.MessengerService;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController("/api/messenger")
public class MessengerController {
    private final MessengerService messengerService;

    public MessengerController(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/direct/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<DirectMessage> getDirectList(@AuthenticationPrincipal CustomUserDetails details,
                                                        @PathVariable @Valid @Size int page) {
        return messengerService.getDirects(details.getUser().getId(), page);
    }

    @GetMapping("/direct/{id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<DirectMessage> getDirectPage(@AuthenticationPrincipal CustomUserDetails details,
                                             @PathVariable Integer id,
                                             @PathVariable Integer page) {
        return messengerService.getDirectPage(details.getUser().getId(), id, page);
    }

    @GetMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public List<ChatMessageInfo> getChatList(@AuthenticationPrincipal CustomUserDetails details) {
        return messengerService.getChatList(details.getUser().getId());
    }

    @GetMapping("/chat/{id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<ChatMessageInfo> getChatPage(@AuthenticationPrincipal CustomUserDetails details,
                                             @PathVariable Integer id,
                                             @PathVariable Integer page) {
        return messengerService.getChatPage(details.getUser().getId(), id, page);
    }
}
