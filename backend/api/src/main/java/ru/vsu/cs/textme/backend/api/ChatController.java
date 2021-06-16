package ru.vsu.cs.textme.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.model.Chat;
import ru.vsu.cs.textme.backend.db.model.request.ChatNameRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatRoleRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatStatusRequest;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.ChatService;
import ru.vsu.cs.textme.backend.services.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final StorageService storageService;


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public Chat createChat(@AuthenticationPrincipal CustomUserDetails details, @RequestBody ChatNameRequest request) {
         return chatService.create(details.getUser().getId(), request.getName());
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Chat getChat(@AuthenticationPrincipal CustomUserDetails details,@PathVariable Integer id) {
        return chatService.getChat(details.getUser().getId(), id);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteChat(@AuthenticationPrincipal CustomUserDetails details, @PathVariable Integer id) {
        chatService.deleteChat(details.getUser().getId(), id);
    }

    @PostMapping("/name/{id}")
    public void setName(@AuthenticationPrincipal CustomUserDetails details,
                            @PathVariable Integer id,
                            @RequestBody ChatNameRequest request) {
       chatService.changeName(details.getUser().getId(), id, request.getName());
    }

    @PostMapping("/role/{id}")
    public void setRole(@AuthenticationPrincipal CustomUserDetails details,
                            @PathVariable Integer id,
                            @RequestBody PostChatRoleRequest request) {
        chatService.setUserRole(details.getUser().getId(), id, request);
    }

    @PostMapping("/status/{id}")
    public void setStatus(@AuthenticationPrincipal CustomUserDetails details,
                              @PathVariable Integer id,
                              @RequestBody PostChatStatusRequest request) {

        chatService.setUserStatus(details.getUser().getId(), id, request);
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void setProfileImage(@AuthenticationPrincipal CustomUserDetails details,
                                @PathVariable Integer id,
                                @RequestParam("image") MultipartFile image) {
        if (image == null) return;
        String type = image.getContentType();
        if (type == null) return;
        int userId = details.getUser().getId();
        chatService.checkAvatarPermissions(userId, id);
        var path = "";
        try {
            path = storageService.uploadChatAvatar(image.getInputStream(), type, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (path.isEmpty()) return;
        chatService.saveAvatar(id, path);
    }

    @PostMapping("/join/{id}")
    public void join(@AuthenticationPrincipal CustomUserDetails details,
                        @PathVariable Integer id) {
        chatService.joinChat(details.getUser().getId(), id);
    }

    @PostMapping("/leave/{id}")
    public void leave(@AuthenticationPrincipal CustomUserDetails details,
                     @PathVariable Integer id) {
        chatService.leaveChat(details.getUser().getId(), id);
    }


}
