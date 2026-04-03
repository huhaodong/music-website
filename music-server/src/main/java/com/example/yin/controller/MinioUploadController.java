package com.example.yin.controller;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.annotation.PostConstruct;

@Service
public class MinioUploadController {

    private static MinioClient minioClient;
    private static String bucketName;

    @Value("${minio.bucket-name:user01}")
    private String configuredBucketName;

    /**
     * 使用 Spring 容器中注入的 MinioClient（测试环境可注入 mock），避免静态读取 application-dev.properties 导致测试联网失败。
     */
    @Autowired(required = false)
    public void setMinioClient(MinioClient client) {
        MinioUploadController.minioClient = client;
    }

    @PostConstruct
    public void initBucketName() {
        MinioUploadController.bucketName = configuredBucketName;
    }

    private static boolean ready() {
        return minioClient != null && bucketName != null && !bucketName.trim().isEmpty();
    }

    public static String uploadFile(MultipartFile file) {
        try {
            if (!ready()) {
                return "Error uploading file to MinIO: MinioClient not configured";
            }
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String uploadImgFile(MultipartFile file) {
        try {
            if (!ready()) {
                return "Error uploading file to MinIO: MinioClient not configured";
            }
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/singer/img/"+file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String uploadSonglistImgFile(MultipartFile file) {
        try {
            if (!ready()) {
                return "Error uploading file to MinIO: MinioClient not configured";
            }
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/songlist/"+file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String uploadSongImgFile(MultipartFile file) {
        try {
            if (!ready()) {
                return "Error uploading file to MinIO: MinioClient not configured";
            }
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/singer/song/"+file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String uploadAtorImgFile(MultipartFile file) {
        try {
            if (!ready()) {
                return "Error uploading file to MinIO: MinioClient not configured";
            }
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/img/avatorImages/"+file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
