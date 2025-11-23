package com.baby.potrait.generator.ai.service;

import com.baby.potrait.generator.ai.entity.Portrait;
import com.baby.potrait.generator.ai.entity.Style;
import com.baby.potrait.generator.ai.entity.User;
import com.baby.potrait.generator.ai.repository.PortraitRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PortraitService {

    private final PortraitRepository portraitRepository;
    private final UploadService uploadService;
    private final StyleService styleService;

    public PortraitService(PortraitRepository portraitRepository,
                           UploadService uploadService,
                           StyleService styleService) {
        this.portraitRepository = portraitRepository;
        this.uploadService = uploadService;
        this.styleService = styleService;
    }

    public Portrait uploadBabyPhoto(MultipartFile file) throws IOException {
        String fileUrl = uploadService.uploadFile(file);
        Portrait portrait = new Portrait();
        portrait.setUploadedFileUrl(fileUrl);
        return portraitRepository.save(portrait);
    }

    public List<Portrait> getPortraitsByUser(User user) {
        return portraitRepository.findByUser(user);
    }

    public List<Portrait> getAllPortraits() {
        return portraitRepository.findAll();
    }

    public Optional<Portrait> getPortraitById(Long id) {
        return portraitRepository.findById(id);
    }

    public Portrait selectStyle(Long portraitId, Long styleId) {
        Portrait portrait = portraitRepository.findById(portraitId)
                .orElseThrow(() -> new RuntimeException("Portrait not found"));
        Style style = styleService.getStyleById(styleId)
                .orElseThrow(() -> new RuntimeException("Style not found"));

        portrait.setStyle(style);
        return portraitRepository.save(portrait);
    }

    public Portrait save(Portrait portrait) {
        return portraitRepository.save(portrait);
    }
}
