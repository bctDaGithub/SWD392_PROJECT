package org.example.smartlawgt.query.services;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.UserDocument;
import org.example.smartlawgt.query.repositories.UserMongoRepository;
import org.example.smartlawgt.query.services.define.IUserQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService implements IUserQueryService {

    private final UserMongoRepository userMongoRepository;

    @Override
    public UserDocument getUserById(UUID id) {
        return userMongoRepository.findById(id).orElse(null);
    }

    @Override
    public UserDocument getUserByEmail(String email) {
        return userMongoRepository.findByEmail(email);
    }

    @Override
    public UserDocument getUserByUserId(UUID userId) {
        return userMongoRepository.findByUserId(userId);
    }

    @Override
    public List<UserDocument> getAllUsers() {
        return userMongoRepository.findAll();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMongoRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userMongoRepository.existsByUserName(userName);
    }

    @Override
    public UserDocument getUserByRole(String role) {
        return userMongoRepository.findByRole(role);
    }
}