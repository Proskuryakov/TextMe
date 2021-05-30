package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.ChatService;

@RestController("/api/chat")
public class ChatController {
    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }


    @GetMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public void createChat(@AuthenticationPrincipal CustomUserDetails details, String name) {
//        service.newChat(details.getUser(), name);
    }
}
