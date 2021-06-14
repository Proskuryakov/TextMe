package ru.vsu.cs.textme.backend.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@Service
public class StorageService {
    private final AmazonS3 s3client;

    @Value("${cloud.aws.s3.url}")
    private String url;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public StorageService(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String uploadUserAvatar(InputStream imageStream, String type, int userId) {
        return upload(imageStream, type, generateFileName(userId));
    }

    public String uploadChatAvatar(InputStream imageStream, String type, int chatId) {
        return upload(imageStream, type, generateChatFileName(chatId));
    }

    public String uploadChatMessageImage(InputStream imageStream, String type, int chatId) {
        return upload(imageStream, type, generateChatMessageFileName(chatId));
    }

    private String upload(InputStream imageStream, String type, String fileName) {
        switch (type) {
            case "image/png": {
                imageStream = convertToJpeg(imageStream);
                if (imageStream == null) return "";
                break;
            }
            case "image/jpeg": break;
            default: return "";
        }

        if (!upload(fileName, imageStream)) {
            return "";
        }

        return url + fileName;
    }

    private @Nullable InputStream convertToJpeg(InputStream pngStream) {
         BufferedImage pngImage;
         try {
             pngImage = ImageIO.read(pngStream);
             pngStream.close();
         } catch (IOException e) {
             return null;
         }

         var jpegImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(), TYPE_INT_RGB);
         var graphics = jpegImage.getGraphics();
         graphics.drawImage(pngImage, 0, 0, Color.WHITE, null);
         graphics.dispose();

         try (var byteStream = new ByteArrayOutputStream()) {
             ImageIO.write(jpegImage, "jpeg", byteStream);
             return new ByteArrayInputStream(byteStream.toByteArray());
         } catch (IOException e) {
             return null;
         }

    }


    public boolean upload(String fileName, InputStream inputStream) {
        try {
            PutObjectRequest req = new PutObjectRequest(bucketName, fileName, inputStream, new ObjectMetadata());
            s3client.putObject(req.withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String generateFileName(int userId) {
        return String.format("avatars/user-%d.jpeg", userId);
    }

    private String generateChatFileName(int chatId) {
        return String.format("avatars/chat-%d.jpeg", chatId);
    }
    private String generateChatMessageFileName(int chatId) {
        return String.format("message-files/%d/%s.jpeg", chatId, UUID.randomUUID().toString());
    }
}

