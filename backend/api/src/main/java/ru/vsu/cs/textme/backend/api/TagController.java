package ru.vsu.cs.textme.backend.api;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.Tag;
import ru.vsu.cs.textme.backend.services.TagService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController()
@RequestMapping("/api/tag")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/like/{begin}")
    @ResponseStatus(OK)
    public List<String> getTags(@PathVariable String begin) {
        return tagService.getTags(begin);
    }
}
