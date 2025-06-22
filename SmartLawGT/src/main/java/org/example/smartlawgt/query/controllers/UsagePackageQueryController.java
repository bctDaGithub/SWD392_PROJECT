package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.UsagePackageDocument;
import org.example.smartlawgt.query.services.define.IUsagePackageQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.query-path}/package")
@RequiredArgsConstructor
public class UsagePackageQueryController {
    private final IUsagePackageQueryService usagePackageQueryService;

    @GetMapping("/all")
    public ResponseEntity<List<UsagePackageDocument>> getAllPackages() {
        return ResponseEntity.ok(usagePackageQueryService.findAllPackages());
    }

    @GetMapping("/active")
    public ResponseEntity<List<UsagePackageDocument>> getAllActivePackages() {
        return ResponseEntity.ok(usagePackageQueryService.findAllActivePackages());
    }

    @GetMapping("/{packageId}")
    public ResponseEntity<UsagePackageDocument> getPackageById(@PathVariable UUID packageId) {
        return usagePackageQueryService.findActivePackageById(packageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsagePackageDocument>> searchPackages(@RequestParam String name) {
        return ResponseEntity.ok(usagePackageQueryService.searchActivePackagesByName(name));
    }

    @GetMapping("/price")
    public ResponseEntity<List<UsagePackageDocument>> getPackagesWithinPrice(@RequestParam Float maxPrice) {
        return ResponseEntity.ok(usagePackageQueryService.findPackagesWithinPrice(maxPrice));
    }
}