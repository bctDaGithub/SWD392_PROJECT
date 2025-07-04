package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.Law.ApiResponse;
import org.example.smartlawgt.query.dtos.LawDTO;
import org.example.smartlawgt.query.dtos.LawSearchCriteria;
import org.example.smartlawgt.query.services.define.ILawQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/query/law")
@RequiredArgsConstructor
public class LawQueryController {
    private final ILawQueryService lawQueryService;

    @GetMapping("/{lawId}")
    public ResponseEntity<ApiResponse<LawDTO>> getLawById(@PathVariable UUID lawId) {
        LawDTO law = lawQueryService.getLawById(lawId);
        return ResponseEntity.ok(ApiResponse.success(law));
    }

    @GetMapping("/number/{lawNumber}")
    public ResponseEntity<ApiResponse<LawDTO>> getLawByNumber(@PathVariable String lawNumber) {
        LawDTO law = lawQueryService.getLawByNumber(lawNumber);
        return ResponseEntity.ok(ApiResponse.success(law));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LawDTO>>> getAllLaws(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "effectiveDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<LawDTO> laws = lawQueryService.getAllLaws(pageable);
        return ResponseEntity.ok(ApiResponse.success(laws));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<LawDTO>>> searchLaws(
            @RequestBody LawSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "effectiveDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<LawDTO> laws = lawQueryService.searchLaws(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(laws));
    }

    @GetMapping("/valid")
    public ResponseEntity<ApiResponse<List<LawDTO>>> getActiveLaws() {
        List<LawDTO> laws = lawQueryService.getActiveLaws();
        return ResponseEntity.ok(ApiResponse.success(laws));
    }

    @GetMapping("/type/{lawTypeName}")
    public ResponseEntity<ApiResponse<List<LawDTO>>> getLawsByType(@PathVariable String lawTypeName) {
        List<LawDTO> laws = lawQueryService.getLawsByTypeName(lawTypeName);
        return ResponseEntity.ok(ApiResponse.success(laws));
    }

    @GetMapping("/issuing-body/{issuingBody}")
    public ResponseEntity<ApiResponse<List<LawDTO>>> getLawsByIssuingBody(@PathVariable String issuingBody) {
        List<LawDTO> laws = lawQueryService.getLawsByIssuingBody(issuingBody);
        return ResponseEntity.ok(ApiResponse.success(laws));
    }
}