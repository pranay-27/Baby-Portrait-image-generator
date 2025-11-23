package com.baby.potrait.generator.ai.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "portraits")
public class Portrait {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uploaded_file_name")
    private String uploadedFileName;

    @Column(name = "uploaded_file_url")
    private String uploadedFileUrl;

    @Column(name = "generated_image_url")
    private String generatedImageUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id")
    private Style style;

    public Portrait() {
        
    }

    public Portrait(LocalDateTime createdAt, String generatedImageUrl, Long id, Style style, String uploadedFileName, String uploadedFileUrl, User user) {
        this.createdAt = createdAt;
        this.generatedImageUrl = generatedImageUrl;
        this.id = id;
        this.style = style;
        this.uploadedFileName = uploadedFileName;
        this.uploadedFileUrl = uploadedFileUrl;
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGeneratedImageUrl() {
        return generatedImageUrl;
    }

    public void setGeneratedImageUrl(String generatedImageUrl) {
        this.generatedImageUrl = generatedImageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUploadedFileUrl() {
        return uploadedFileUrl;
    }

    public void setUploadedFileUrl(String uploadedFileUrl) {
        this.uploadedFileUrl = uploadedFileUrl;
    }
}
