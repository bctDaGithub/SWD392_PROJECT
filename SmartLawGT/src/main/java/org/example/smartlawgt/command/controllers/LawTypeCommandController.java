package org.example.smartlawgt.command.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.ApiResponse;
import org.example.smartlawgt.command.dtos.CreateLawTypeCommand;
import org.example.smartlawgt.command.dtos.UpdateLawTypeCommand;
import org.example.smartlawgt.command.services.define.ILawTypeService;
import org.example.smartlawgt.integration.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/command/lawtype")
@RequiredArgsConstructor
public class LawTypeCommandController {
    private final ILawTypeService lawTypeService;
    private final JwtUtil jwt;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<ApiResponse<UUID>> createLawType(@Valid @RequestBody CreateLawTypeCommand command) {
        UUID lawTypeId = lawTypeService.createLawType(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Law type created successfully", lawTypeId));
    }

    @PutMapping("/{lawTypeId}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<ApiResponse<Void>> updateLawType(
            @RequestHeader ("Authorization") String token,
            @PathVariable UUID lawTypeId,
            @Valid @RequestBody UpdateLawTypeCommand command) {
        UUID userId = jwt.getUserIdFromToken(token.replace("Bearer ", ""));

        lawTypeService.updateLawType(lawTypeId, command, userId);
        return ResponseEntity.ok(ApiResponse.success("Law type updated successfully"));
    }

    @DeleteMapping("/{lawTypeId}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<ApiResponse<Void>> deleteLawType(@PathVariable UUID lawTypeId) {
        lawTypeService.deleteLawType(lawTypeId);
        return ResponseEntity.ok(ApiResponse.success("Law type deleted successfully"));
    }

}

