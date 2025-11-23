package com.baby.potrait.generator.ai.service;

import com.baby.potrait.generator.ai.entity.Style;
import com.baby.potrait.generator.ai.repository.StyleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StyleService {

    private final StyleRepository styleRepository;


    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    public List<Style> getAllStyles() {
        return styleRepository.findAll();
    }

    public Optional<Style> getStyleById(Long id) {
        return styleRepository.findById(id);
    }

    public Style createStyle(Style style) {
        return styleRepository.save(style);
    }
}
