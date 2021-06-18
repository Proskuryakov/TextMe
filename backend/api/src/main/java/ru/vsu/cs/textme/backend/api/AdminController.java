package ru.vsu.cs.textme.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.request.PermissionRequest;
import ru.vsu.cs.textme.backend.services.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @RequestMapping("/permission")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void changePermission(@RequestBody PermissionRequest request) {
        adminService.changePermission(request);
    }

    @RequestMapping("/user")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Profile getUser(@RequestParam("name") String name) {
        return adminService.getUserProfile(name);
    }

    @RequestMapping("/moders/{page}")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Info> getModerators(@PathVariable Integer page) {
        return adminService.getModerators(page);
    }
}
