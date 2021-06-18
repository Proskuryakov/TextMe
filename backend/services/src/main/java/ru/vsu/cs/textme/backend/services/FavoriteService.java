package ru.vsu.cs.textme.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.info.Profile;

import java.util.List;

import static ru.vsu.cs.textme.backend.services.CardService.CARD_LIMIT;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final UserMapper userMapper;
    public List<Profile> getFavorites(Integer id, Integer page) {
        return userMapper.getFavorites(id, CARD_LIMIT, page * CARD_LIMIT);
    }

    public void deleteFavorite(Integer user, Integer favorite) {
        userMapper.deleteFavorite(user, favorite);
    }

    public void addFavorite(Integer user, Integer favorite) {
        userMapper.addFavorite(user, favorite);
    }
}
