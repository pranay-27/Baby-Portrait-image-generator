package com.baby.potrait.generator.ai.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    public User() {
        
    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portrait> portraits;

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", getName()=" + getName() + ", getEmail()="
                + getEmail() + ", toString()=" + super.toString() + "]";
    }
}
