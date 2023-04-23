package com.codecorrect.code_submission_application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecorrect.code_submission_application.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

   Optional<User> findByUsername(String username);

    
    
}
