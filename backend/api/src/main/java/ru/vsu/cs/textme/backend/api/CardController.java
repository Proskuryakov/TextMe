package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.info.Card;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.CardService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
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
                  @RequestBody @Valid @NotEmpty String tag) {
        cardService.addUserTag(details.getUser(), tag);
    }

    @DeleteMapping("/user/tag")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserTag(@AuthenticationPrincipal CustomUserDetails details,
                           @RequestBody @Valid @NotEmpty String tag) {
        cardService.deleteTag(details.getUser(), tag);
    }

    @PostMapping("/user/content")
    @ResponseStatus(HttpStatus.OK)
    public void saveContent(@AuthenticationPrincipal CustomUserDetails details,
                  @RequestBody String content) {
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
                                             @PathVariable @Valid @Size Integer page) {
        return cardService.findUserNearbyProfilesById(details.getUser().getId(), page);
    }
    @GetMapping("/chats/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getNearbyChatCards(@AuthenticationPrincipal CustomUserDetails details,
                                            @PathVariable @Valid @Size Integer page) {
        return cardService.findChatNearbyProfilesById(details.getUser().getId(), page);
    }
}
