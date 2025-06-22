package org.example.smartlawgt.command.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.UsagePackageDTO;
import org.example.smartlawgt.command.entities.UsagePackageEntity;
import org.example.smartlawgt.command.mappers.UsagePackageMapper;
import org.example.smartlawgt.command.services.define.IUsagePackageCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.command-path}/package")
@RequiredArgsConstructor
public class UsagePackageCommandController {

    private final IUsagePackageCommandService service;

    @PostMapping
    public ResponseEntity<UsagePackageDTO> createPackage(@RequestBody UsagePackageDTO dto) {
        UsagePackageEntity entity = service.createPackage(UsagePackageMapper.toEntity(dto));
        return ResponseEntity.ok(UsagePackageMapper.toDTO(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsagePackageDTO> updatePackage(@PathVariable UUID id, @RequestBody UsagePackageDTO dto) {
        UsagePackageEntity updated = service.updatePackage(id, UsagePackageMapper.toEntity(dto));
        return ResponseEntity.ok(UsagePackageMapper.toDTO(updated));
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<Void> disablePackage(@PathVariable UUID id) {
        service.disablePackage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<Void> enablePackage(@PathVariable UUID id) {
        service.enablePackage(id);
        return ResponseEntity.noContent().build();
    }
}
