package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.documents.UserDocument;

import java.util.List;
import java.util.UUID;

public interface IUserQueryService {
    UserDocument getUserById(UUID id);
    UserDocument getUserByEmail(String email);
    UserDocument getUserByUserId(UUID userId);
    List<UserDocument> getAllUsers();
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    UserDocument getUserByRole(String role);
}