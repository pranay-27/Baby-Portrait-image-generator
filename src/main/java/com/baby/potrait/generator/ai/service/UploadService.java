package com.baby.potrait.generator.ai.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UploadService {

    private final Cloudinary cloudinary;

    public UploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return uploadResult.get("secure_url").toString();
    }
    public String uploadBytes(byte[] bytes, String filename) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(bytes,
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "public_id", filename.replace(".png", "")
                ));
        return uploadResult.get("secure_url").toString();
    }
}
