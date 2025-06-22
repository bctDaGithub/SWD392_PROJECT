package org.example.smartlawgt.command.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.PurchaseRequestDTO;
import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.example.smartlawgt.command.mappers.UserPackageMapper;
import org.example.smartlawgt.command.services.define.IUserPackageCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.command-path}/user-package")
@RequiredArgsConstructor
public class UserPackageCommandController {

    private final IUserPackageCommandService service;

    @PostMapping
    public ResponseEntity<Void> recordPurchase(@RequestBody PurchaseRequestDTO dto) {
        UserPackageEntity entity = UserPackageMapper.toEntity(dto);
        service.recordPurchase(entity);
        return ResponseEntity.ok().build();
    }

}
