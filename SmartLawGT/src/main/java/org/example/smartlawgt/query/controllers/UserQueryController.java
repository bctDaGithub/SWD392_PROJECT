package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.UserDocument;
import org.example.smartlawgt.query.services.define.IUserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.query-path}/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final IUserQueryService userQueryService;

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDocument> getUserByEmail(@PathVariable String email) {
        UserDocument user = userQueryService.getUserByEmail(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<UserDocument> getUserByUserId(@PathVariable UUID userId) {
        UserDocument user = userQueryService.getUserByUserId(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDocument>> getAllUsers() {
        List<UserDocument> users = userQueryService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        return ResponseEntity.ok(userQueryService.existsByEmail(email));
    }

    @GetMapping("/check/username/{userName}")
    public ResponseEntity<Boolean> checkUserNameExists(@PathVariable String userName) {
        return ResponseEntity.ok(userQueryService.existsByUserName(userName));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<UserDocument> getUserByRole(@PathVariable String role) {
        UserDocument user = userQueryService.getUserByRole(role);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}