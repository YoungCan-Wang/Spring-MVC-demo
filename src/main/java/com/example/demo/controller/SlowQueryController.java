package com.example.demo.controller;

import com.example.demo.service.SlowQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/slow")
public class SlowQueryController {

    private final SlowQueryService slowQueryService;

    public SlowQueryController(SlowQueryService slowQueryService) {
        this.slowQueryService = slowQueryService;
    }

    /**
     * 执行慢查询
     */
    @GetMapping("/query/{seconds}")
    public ResponseEntity<Map<String, Object>> slowQuery(@PathVariable int seconds) {
        try {
            String result = slowQueryService.slowQuery(seconds);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", result,
                "duration", seconds + " 秒"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 执行复杂查询
     */
    @GetMapping("/complex")
    public ResponseEntity<Map<String, Object>> complexQuery() {
        try {
            String result = slowQueryService.complexQuery();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
