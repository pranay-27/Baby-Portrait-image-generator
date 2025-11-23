package com.baby.potrait.generator.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baby.potrait.generator.ai.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}
