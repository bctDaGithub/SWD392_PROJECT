package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.UserPackageDocument;
import org.example.smartlawgt.query.services.define.IUserPackageQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.query-path}/package/user")
@RequiredArgsConstructor
public class UserPackageQueryController {
    private final IUserPackageQueryService userPackageQueryService;

    @GetMapping("/active/{userId}")
    public ResponseEntity<List<UserPackageDocument>> getUserActiveSubscriptions(@PathVariable UUID userId) {
        return ResponseEntity.ok(userPackageQueryService.findUserActiveSubscriptions(userId));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<Page<UserPackageDocument>> getUserSubscriptionHistory(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userPackageQueryService.getUserSubscriptionHistory(userId, pageable));
    }

    @GetMapping("/expired")
    public ResponseEntity<List<UserPackageDocument>> getExpiredSubscriptions() {
        return ResponseEntity.ok(userPackageQueryService.findExpiredSubscriptions());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserPackageDocument>> searchByPackageName(@RequestParam String packageName) {
        return ResponseEntity.ok(userPackageQueryService.searchSubscriptionsByPackageName(packageName));
    }
}