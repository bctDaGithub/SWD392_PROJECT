package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.Law.ApiResponse;
import org.example.smartlawgt.query.dtos.LawTypeQueryDTO;
import org.example.smartlawgt.query.services.define.ILawTypeQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/query/law-type")
@RequiredArgsConstructor
public class LawTypeQueryController {
    private final ILawTypeQueryService lawTypeQueryService;

    @GetMapping("/{lawTypeId}")
    public ResponseEntity<ApiResponse<LawTypeQueryDTO>> getLawTypeById(@PathVariable UUID lawTypeId) {
        LawTypeQueryDTO lawType = lawTypeQueryService.getLawTypeById(lawTypeId);
        return ResponseEntity.ok(ApiResponse.success(lawType));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LawTypeQueryDTO>>> getAllLawTypes() {
        List<LawTypeQueryDTO> lawTypes = lawTypeQueryService.getAllLawTypes();
        return ResponseEntity.ok(ApiResponse.success(lawTypes));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LawTypeQueryDTO>>> getActiveLawTypes() {
        List<LawTypeQueryDTO> lawTypes = lawTypeQueryService.getActiveLawTypes();
        return ResponseEntity.ok(ApiResponse.success(lawTypes));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LawTypeQueryDTO>>> searchLawTypes(@RequestParam String name) {
        List<LawTypeQueryDTO> lawTypes = lawTypeQueryService.searchLawTypes(name);
        return ResponseEntity.ok(ApiResponse.success(lawTypes));
    }
}
