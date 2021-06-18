package ru.vsu.cs.textme.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.request.FavoriteRequest;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService service;

    @GetMapping("/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getFavorites(@AuthenticationPrincipal CustomUserDetails details,
                                      @PathVariable Integer page) {
        return service.getFavorites(details.getUser().getId(), page);
    }

    @DeleteMapping("/")
    public void  deleteFavorite(@AuthenticationPrincipal CustomUserDetails details,
                                @RequestBody FavoriteRequest favorite) {
        service.deleteFavorite(details.getUser().getId(), favorite.getId());
    }

    @PostMapping("/")
    public void  addFavorite(@AuthenticationPrincipal CustomUserDetails details,
                                @RequestBody FavoriteRequest favorite) {
        service.addFavorite(details.getUser().getId(), favorite.getId());
    }
}
