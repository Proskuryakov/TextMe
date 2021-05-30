package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.info.Card;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.CardService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Card getUserCard(@PathVariable @Valid @Size int id) {
        return cardService.findUserCardById(id);
    }

    @PostMapping("/user/tag")
    @ResponseStatus(HttpStatus.OK)
    public void addUserTag(@AuthenticationPrincipal CustomUserDetails details,
                  @RequestBody @Valid @NotEmpty @Size(max = 64) String tag) {
        cardService.addUserTag(details.getUser(), tag);
    }

    @DeleteMapping("/user/tag")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserTag(@AuthenticationPrincipal CustomUserDetails details,
                           @RequestBody @Valid @NotEmpty @Size(max = 64) String tag) {
        cardService.deleteTag(details.getUser(), tag);
    }

    @PostMapping("/user/content")
    @ResponseStatus(HttpStatus.OK)
    public void saveContent(@AuthenticationPrincipal CustomUserDetails details,
                  @RequestBody @Valid @Size(max = 256) String content) {
        cardService.saveUserContent(details.getUser(), content);
    }

    @GetMapping("/chat/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Card getChatCard(@PathVariable @Valid @Size int id) {
        return cardService.findChatCardById(id);
    }

    @GetMapping("/users/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getNearbyUsersCards(@AuthenticationPrincipal CustomUserDetails details,
                                             @PathVariable @Valid @Size Integer page,
                                             @RequestParam @Nullable String tag) {
        if (tag != null) tag = URLEncoder.encode(tag, StandardCharsets.UTF_8);
        return cardService.findNearbyUserProfilesById(details.getUser().getId(), page, encode(tag));
    }

    @GetMapping("/users/random")
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getRandomUsersCards() {
        return cardService.findRandomUserProfiles();
    }

    @GetMapping("/chats/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getNearbyChatCards(@AuthenticationPrincipal CustomUserDetails details,
                                            @PathVariable @Valid @Size Integer page,
                                            @RequestParam @Nullable String tag) {
        return cardService.findNearbyChatProfilesById(details.getUser().getId(), page, encode(tag));
    }

    @GetMapping("/chats/random")
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getRandomChatCards() {
        return cardService.findRandomChatProfiles();
    }

    private String encode(@Nullable String str) {
        return (str == null)  ? "" : URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
