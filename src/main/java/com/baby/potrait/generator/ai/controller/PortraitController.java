package com.baby.potrait.generator.ai.controller;

import java.io.IOException;
import com.baby.potrait.generator.ai.service.GenerateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.baby.potrait.generator.ai.entity.Portrait;
import com.baby.potrait.generator.ai.entity.Style;
import com.baby.potrait.generator.ai.entity.User;
import com.baby.potrait.generator.ai.service.PortraitService;
import com.baby.potrait.generator.ai.service.StyleService;
import com.baby.potrait.generator.ai.service.UserService;

@Controller
@RequestMapping("/api")
public class PortraitController {

    private final PortraitService portraitService;
    private final UserService userService;
    private final StyleService styleService;
    private final GenerateService generateService;

    public PortraitController(PortraitService portraitService, UserService userService,
                              StyleService styleService, GenerateService generateService) {
        this.portraitService = portraitService;
        this.userService = userService;
        this.styleService = styleService;
        this.generateService = generateService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Portrait> uploadBabyPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        Portrait portrait = portraitService.uploadBabyPhoto(file);
        return ResponseEntity.ok(portrait);
    }
    @PostMapping("/generate")
    public String generateImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("styleId") Long styleId,
            Model model) {
        try {
            // Call service to generate image
            String generatedUrl = generateService.generateImage(file, styleId);
            // Pass the generated image URL to the template
            model.addAttribute("generatedUrl", generatedUrl);
            return "create"; // Thymeleaf template name: generateResult.html
        } catch (Exception e) {
            model.addAttribute("error", "Failed to generate image: " + e.getMessage());
            return "create";
        }
    }
    @GetMapping("portrait/{id}")
    public ResponseEntity<Portrait>getPortraitById(@PathVariable Long id) {
        Portrait portrait = portraitService.getPortraitById(id)
                            .orElseThrow(() -> new RuntimeException("Portrait not found by id "+id));
        
        return ResponseEntity.ok(portrait);
    }

//

    @PostMapping("/generate-portrait")
    public ResponseEntity<Portrait> uploadAndGenerate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("styleId") Long styleId,
            @RequestParam("userId") Long userId) {

        try {
            Portrait uploadedPortrait = portraitService.uploadBabyPhoto(file);

            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Style style = styleService.getStyleById(styleId)
                    .orElseThrow(() -> new RuntimeException("Style not found"));

            Portrait generatedPortrait = portraitService.generatePortrait(
                    user,
                    style,
                    uploadedPortrait.getUploadedFileUrl(),
                    uploadedPortrait.getUploadedFileName()
            );

            return ResponseEntity.ok(generatedPortrait);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}