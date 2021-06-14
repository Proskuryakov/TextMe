package ru.vsu.cs.textme.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.ChatService;
import ru.vsu.cs.textme.backend.services.DirectService;
import ru.vsu.cs.textme.backend.services.StorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/file-message")
@RequiredArgsConstructor
public class FilesMessageController {
    private final StorageService storageService;
    private final DirectService directService;
    private final ChatService chatService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/direct/{id}")
    public List<String> sendDirectMessage(@AuthenticationPrincipal CustomUserDetails details,
                                          @PathVariable Integer id,
                                          @RequestParam List<MultipartFile> images) {
        if (images.size() > 5) return null;
        directService.checkAccess(details.getUser().getId(), id);
        return loadFiles(id, images);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chat/{id}")
    public List<String> sendChatMessage(@AuthenticationPrincipal CustomUserDetails details,
                                        @PathVariable Integer id,
                                        @RequestParam List<MultipartFile> images) {
        if (images.size() > 5) return null;
        chatService.checkAccess(details.getUser().getId(), id);

        return loadFiles(id, images);
    }

    private List<String> loadFiles(Integer id, List<MultipartFile> images) {
        var list = new ArrayList<String>();
        for (var image : images) {
            if (image == null) continue;
            String type = image.getContentType();
            if (type == null) continue;
            var path = "";
            try {
                path = storageService.uploadMessageImage(image.getInputStream(), type, id);
            } catch (IOException e) { e.printStackTrace(); }
            if (path.isEmpty()) continue;
            list.add(path);
        }
        return list;
    }
}
