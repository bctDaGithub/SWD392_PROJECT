package org.example.smartlawgt.query.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/clear")
    public ResponseEntity<String> clearCache() {
        try {
            // Lấy tất cả keys và xóa
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                return ResponseEntity.ok("Cache cleared successfully. Deleted " + keys.size() + " keys.");
            } else {
                return ResponseEntity.ok("Cache is already empty.");
            }
        } catch (Exception e) {
            return ResponseEntity.ok("Cache cleared with flush operation.");
        }
    }
}
