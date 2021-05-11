package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.TagMapper;
import ru.vsu.cs.textme.backend.db.model.Tag;

import java.util.List;

@Service
public class TagService {
    public static final int MAX_LIKE_TAG = 5;

    private final TagMapper tagMapper;

    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public List<Tag> getTags(String begin) {
        return tagMapper.getTagsLike(begin + '%', MAX_LIKE_TAG);
    }
}
