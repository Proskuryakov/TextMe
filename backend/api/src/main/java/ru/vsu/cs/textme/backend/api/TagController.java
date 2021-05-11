package ru.vsu.cs.textme.backend.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.textme.backend.db.model.Tag;
import ru.vsu.cs.textme.backend.services.TagService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController("/api/tag")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/like/{begin}")
    @ResponseStatus(OK)
    public List<Tag> getTags(@PathVariable String begin) {
        return tagService.getTags(begin);
    }
}
