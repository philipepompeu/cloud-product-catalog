package com.github.philipepompeu.cloud_product_catalog.domain.service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.philipepompeu.cloud_product_catalog.domain.model.UserEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class OwnerIdManager {

    
    private final UserRepository userRepository;
    private String ownerId;

    public OwnerIdManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {

        if (userRepository.count() == 0) {
            UserEntity user = new UserEntity();
            user.setId(UUID.randomUUID().toString());
            user.setUsername("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("pwd123"));
            userRepository.save(user);
            System.out.println("Usuário padrão criado com ID: " + user.getId());
        }

        this.ownerId = userRepository.findAll()
            .stream()
            .map(UserEntity::getId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Nenhum usuário encontrado no banco!"));
        
        System.out.println("OwnerId carregado: " + ownerId);
    }

    public String getOwnerId(){

        return this.ownerId;
    }

}
