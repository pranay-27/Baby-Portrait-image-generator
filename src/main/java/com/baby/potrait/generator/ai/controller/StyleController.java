package com.baby.potrait.generator.ai.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baby.potrait.generator.ai.entity.Portrait;
import com.baby.potrait.generator.ai.entity.Style;
import com.baby.potrait.generator.ai.service.PortraitService;
import com.baby.potrait.generator.ai.service.StyleService;

@RestController
@RequestMapping("/api/styles")
public class StyleController {

    private final StyleService styleService;
    private final PortraitService portraitService;

    public StyleController(StyleService styleService, PortraitService portraitService) {
        this.styleService = styleService;
        this.portraitService = portraitService;
    }

    @GetMapping
    public ResponseEntity<List<Style>> getAllStyles() {
        return ResponseEntity.ok(styleService.getAllStyles());
    }

    @PostMapping
    public ResponseEntity<Style> createStyle(@RequestBody Style style) {
        return ResponseEntity.ok(styleService.createStyle(style));
    }

    @PostMapping("/{portraitId}/select-style/{styleId}")
    public ResponseEntity<Portrait> selectStyle(
        @PathVariable Long portraitId,
        @PathVariable Long styleId) {
        Portrait updatedPortrait = portraitService.selectStyle(portraitId, styleId);
        
        return ResponseEntity.ok(updatedPortrait);
    }

}
