package com.baby.potrait.generator.ai.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "styles")
public class Style {

    public Style() {
        
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    public Style(Long id, String name, List<Portrait> portraits, String prompt, String sampleImageUrl) {
        this.id = id;
        this.name = name;
        this.portraits = portraits;
        this.prompt = prompt;
        this.sampleImageUrl = sampleImageUrl;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Portrait> getPortraits() {
        return portraits;
    }

    public void setPortraits(List<Portrait> portraits) {
        this.portraits = portraits;
    }

    public String getSampleImageUrl() {
        return sampleImageUrl;
    }

    public void setSampleImageUrl(String sampleImageUrl) {
        this.sampleImageUrl = sampleImageUrl;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    private String sampleImageUrl;

    @OneToMany(mappedBy = "style", cascade = CascadeType.ALL)
    private List<Portrait> portraits;
}