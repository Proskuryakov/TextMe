package ru.vsu.cs.textme.backend.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

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

    public String upload(InputStream file, String type, int userId) {
        if (type.contains("png")) {
            //TODO converting to jpeg
            return "";
        }

        String fileName = generateFileName(userId);

        if (!upload(fileName, file)) {
            return "";
        }

        return url + fileName;
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
        return String.format("%d.jpeg", userId);
    }
}

