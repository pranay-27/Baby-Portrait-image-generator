package com.baby.potrait.generator.ai.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class GenerateService {

    private final UploadService uploadService;
    private final StyleService styleService;

    @Value("${stability.api.key}")
    private String stabilityApiKey;

    private static final String STABILITY_ENDPOINT = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/image-to-image";
    private final OkHttpClient client;
    private static final int TARGET_WIDTH = 1024;
    private static final int TARGET_HEIGHT = 1024;

    public GenerateService(UploadService uploadService, StyleService styleService) {
        this.uploadService = uploadService;
        this.styleService = styleService;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .callTimeout(360, TimeUnit.SECONDS)
                .build();
    }
    private File resizeImage(File inputFile, int targetWidth, int targetHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        File resizedFile = new File(inputFile.getParent(), "resized_" + inputFile.getName() + ".png");
        ImageIO.write(resizedImage, "png", resizedFile);

        return resizedFile;
    }

    public String generateImage(MultipartFile originalFile, Long styleId) throws Exception {
        // 1. Upload original to Cloudinary
        String originalUrl = uploadService.uploadFile(originalFile);
        System.out.println("Original image uploaded to: " + originalUrl);

        // 2. Get style info
        var styleOpt = styleService.getStyleById(styleId);
        if (styleOpt.isEmpty()) throw new RuntimeException("Style not found");
        var style = styleOpt.get();
        String sampleUrl = style.getSampleImageUrl();
        String prompt = style.getPrompt();

        // 3. Download images locally with unique filenames
        String timestamp = String.valueOf(System.currentTimeMillis());
        File originalImg = resizeImage(downloadImage(originalUrl, "original_" + timestamp + ".png"),
                TARGET_WIDTH, TARGET_HEIGHT);
        File sampleImg = resizeImage(downloadImage(sampleUrl, "sample_" + timestamp + ".png"),
                TARGET_WIDTH, TARGET_HEIGHT);

        try {
            // 4. Call Stability AI
            File generatedFile = callStabilityAI(originalImg, sampleImg, prompt);

            // 5. Upload generated image to Cloudinary
            byte[] bytes = Files.readAllBytes(generatedFile.toPath());
            return uploadService.uploadBytes(bytes, "generated-" + timestamp);
        } finally {
            // Clean up temporary files
            cleanupFile(originalImg);
            cleanupFile(sampleImg);
        }
    }

    private File callStabilityAI(File original, File styleReference, String prompt) throws IOException {
        System.out.println("Calling Stability AI with:");
        System.out.println("Original file: " + original.getAbsolutePath() + " (exists: " + original.exists() + ", size: " + original.length() + ")");
        System.out.println("Style reference: " + styleReference.getAbsolutePath() + " (exists: " + styleReference.exists() + ", size: " + styleReference.length() + ")");
        System.out.println("Prompt: " + prompt);

        // Validate files exist and are not empty
        if (!original.exists() || original.length() == 0) {
            throw new IOException("Original image file is missing or empty");
        }
        if (!styleReference.exists() || styleReference.length() == 0) {
            throw new IOException("Style reference image file is missing or empty");
        }

        // Build multipart request for image-to-image generation
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        // Add the original image as init_image
        builder.addFormDataPart("init_image", original.getName(), 
            RequestBody.create(original, MediaType.parse("image/png")));

        // Add the prompt
        builder.addFormDataPart("text_prompts[0][text]", prompt);
        builder.addFormDataPart("text_prompts[0][weight]", "1");

        // Add negative prompt
        builder.addFormDataPart("text_prompts[1][text]", "blurry, low quality, distorted, ugly, bad anatomy");
        builder.addFormDataPart("text_prompts[1][weight]", "-1");

        // Generation parameters
        builder.addFormDataPart("image_strength", "0.35");
        builder.addFormDataPart("cfg_scale", "7");
        builder.addFormDataPart("steps", "30");
        builder.addFormDataPart("samples", "1");

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(STABILITY_ENDPOINT)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + stabilityApiKey)
                .addHeader("Accept", "application/json")
                .build();

        System.out.println("Making request to: " + STABILITY_ENDPOINT);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response code: " + response.code());

            String responseBody = response.body().string();
            System.out.println("Response body: " + responseBody);

            if (!response.isSuccessful()) {
                System.err.println("Stability API error response: " + responseBody);
                throw new IOException("Stability API failed with code " + response.code() + ": " + responseBody);
            }

            JSONObject json = new JSONObject(responseBody);

            // Parse the response for artifacts
            String base64 = null;
            try {
                if (json.has("artifacts") && json.getJSONArray("artifacts").length() > 0) {
                    JSONObject artifact = json.getJSONArray("artifacts").getJSONObject(0);
                    base64 = artifact.getString("base64");
                } else {
                    System.err.println("Unknown response format. Available keys: " + json.keySet());
                    throw new IOException("Could not find image data in response. Response keys: " + json.keySet());
                }
            } catch (Exception e) {
                System.err.println("Error parsing response: " + e.getMessage());
                System.err.println("Full response: " + responseBody);
                throw new IOException("Failed to parse response: " + e.getMessage());
            }

            if (base64 == null || base64.isEmpty()) {
                throw new IOException("No image data found in response");
            }

            byte[] bytes = java.util.Base64.getDecoder().decode(base64);
            String outputFilename = "generated_" + System.currentTimeMillis() + ".png";
            File output = new File(System.getProperty("java.io.tmpdir"), outputFilename);
            Files.write(output.toPath(), bytes);

            System.out.println("Generated image saved to: " + output.getAbsolutePath() + " (size: " + bytes.length + " bytes)");
            return output;
        }
    }

    private MediaType getMediaTypeForFile(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".webp")) return MediaType.parse("image/webp");
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return MediaType.parse("image/jpeg");
        return MediaType.parse("image/png"); // default
    }

    private File downloadImage(String url, String filename) throws IOException {
        System.out.println("Downloading image from: " + url);

        try (InputStream in = new URL(url).openStream()) {
            // Create file in system temp directory to avoid permission issues
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            File file = tempDir.resolve(filename).toFile();

            Files.copy(in, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Downloaded to: " + file.getAbsolutePath() + " (size: " + file.length() + " bytes)");
            return file;
        }
    }

    private void cleanupFile(File file) {
        if (file != null && file.exists()) {
            try {
                Files.delete(file.toPath());
                System.out.println("Cleaned up temporary file: " + file.getName());
            } catch (IOException e) {
                System.err.println("Failed to cleanup file: " + file.getName() + " - " + e.getMessage());
            }
        }
    }
}