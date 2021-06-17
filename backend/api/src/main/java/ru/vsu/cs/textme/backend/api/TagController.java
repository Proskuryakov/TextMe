package ru.vsu.cs.textme.backend.api;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.services.TagService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController()
@RequestMapping("/api/tag")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/like")
    @ResponseStatus(OK)
    public List<String> getTags(@RequestParam("begin") String begin) {
        if (begin != null) begin = URLEncoder.encode(begin, StandardCharsets.UTF_8);
        return StringUtils.hasText(begin) ? tagService.getTags(begin) : Collections.emptyList();
    }
}
