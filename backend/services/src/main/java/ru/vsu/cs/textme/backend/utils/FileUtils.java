package ru.vsu.cs.textme.backend.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtils {
    public static void saveFile(String dir, String name, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(dir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Files.copy(file.getInputStream(), uploadPath.resolve(name), REPLACE_EXISTING);
    }
}
