package com.baby.potrait.generator.ai.controller;

import java.io.InputStream;
import java.net.URL;

import com.baby.potrait.generator.ai.entity.Portrait;
import com.baby.potrait.generator.ai.entity.Style;
import com.baby.potrait.generator.ai.service.GenerateService;
import com.baby.potrait.generator.ai.service.PortraitService;
import com.baby.potrait.generator.ai.service.StyleService;
import com.baby.potrait.generator.ai.service.UploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PortraitController {

    private final PortraitService portraitService;
    private final StyleService styleService;
    private final GenerateService generateService;
    private final UploadService uploadService;

    public PortraitController(PortraitService portraitService,
                              StyleService styleService,
                              GenerateService generateService,
                              UploadService uploadService) {
        this.portraitService = portraitService;
        this.styleService = styleService;
        this.generateService = generateService;
        this.uploadService = uploadService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/create")
    public String showCreatePage() {
        return "create";
    }

    @PostMapping("/api/test-upload")
    @ResponseBody
    public ResponseEntity<String> testUpload(@RequestParam("file") MultipartFile file) {
        try {
            String url = uploadService.uploadFile(file);
            return ResponseEntity.ok("Upload successful! URL: " + url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload error: " + e.getMessage());
        }
    }

    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<String> generateImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("styleId") Long styleId) {
        try {
            String generatedUrl = generateService.generateImage(file, styleId);

            Style style = styleService.getStyleById(styleId)
                    .orElseThrow(() -> new RuntimeException("Style not found"));

            Portrait portrait = new Portrait();
            portrait.setUploadedFileName(file.getOriginalFilename());
            portrait.setGeneratedImageUrl(generatedUrl);
            portrait.setStyle(style);

            portraitService.save(portrait);

            // Return Cloudinary URL directly
            return ResponseEntity.ok(generatedUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/download/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        try {
            Portrait portrait = portraitService.getPortraitById(id)
                    .orElseThrow(() -> new RuntimeException("Portrait not found"));

            String fileUrl = portrait.getGeneratedImageUrl();
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            try (InputStream in = new URL(fileUrl).openStream()) {
                byte[] imageBytes = in.readAllBytes();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.set(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"baby-portrait-" + id + ".png\"");
                headers.setContentLength(imageBytes.length);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
