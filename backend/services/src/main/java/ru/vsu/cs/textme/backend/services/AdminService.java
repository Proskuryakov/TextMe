package ru.vsu.cs.textme.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.request.PermissionRequest;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserMapper userMapper;

    public void changePermission(PermissionRequest request) {
        if (AppRole.MODER.getId().equals(request.getRole())) {
            if (request.isPermitted()) userMapper.saveRole(request.getUser(), request.getRole());
            else userMapper.removeRole(request.getUser(), request.getRole());
        }
    }

    public Profile getUserProfile(String name) {
        return userMapper.findProfileByName(name);
    }
}
