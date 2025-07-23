package org.example.smartlawgt.command.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.CreateLawCommand;
import org.example.smartlawgt.command.dtos.UpdateLawCommand;
import org.example.smartlawgt.command.entities.LawStatus;
import org.example.smartlawgt.command.services.define.ILawCommandService;
import org.example.smartlawgt.integration.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.smartlawgt.command.dtos.ApiResponse;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/command/law")
@RequiredArgsConstructor
public class LawCommandController {
    private final ILawCommandService lawCommandService;
    private final JwtUtil jwt;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UUID>> createLaw(@RequestHeader ("Authorization") String token, @Valid @RequestBody CreateLawCommand command){
        UUID userId = jwt.getUserIdFromToken(token.replace("Bearer ", ""));
        command.setCreatedByUserId(userId);
        UUID lawId = lawCommandService.createLaw(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Law created successfully", lawId));
    }

    @PutMapping("/{lawId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateLaw(
            @RequestHeader ("Authorization") String token,
            @PathVariable UUID lawId,
            @Valid @RequestBody UpdateLawCommand command) {
        UUID userId = jwt.getUserIdFromToken(token.replace("Bearer ", ""));
        command.setUpdateByUserId(String.valueOf(userId));
        lawCommandService.updateLaw(lawId, command);
        return ResponseEntity.ok(ApiResponse.success("Law updated successfully"));
    }

    @DeleteMapping("/{lawId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLaw(@PathVariable UUID lawId) {
        lawCommandService.deleteLaw(lawId);
        return ResponseEntity.ok(ApiResponse.success("Law deleted successfully"));
    }

    @PatchMapping("/{lawId}/status")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<ApiResponse<Void>> changeLawStatus(
            @PathVariable UUID lawId,
            @RequestBody LawStatus status) {
        lawCommandService.changeLawStatus(lawId, status);
        return ResponseEntity.ok(ApiResponse.success("Law status changed successfully"));
    }
}

