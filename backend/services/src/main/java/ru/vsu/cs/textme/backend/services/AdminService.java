package ru.vsu.cs.textme.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.request.PermissionRequest;

import java.util.List;

import static ru.vsu.cs.textme.backend.db.model.AppRole.MODER;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserMapper userMapper;

    public void changePermission(PermissionRequest request) {
        if (MODER.getId().equals(request.getRole())) {
            if (request.isPermitted()) userMapper.saveRole(request.getUser(), request.getRole());
            else userMapper.removeRole(request.getUser(), request.getRole());
        }
    }

    public Profile getUserProfile(String name) {
        return userMapper.findProfileByName(name);
    }

    public List<Info> getModerators(Integer page) {
        return userMapper.findUsersByRole(MODER.getId(),128, page * 128);
    }
}
