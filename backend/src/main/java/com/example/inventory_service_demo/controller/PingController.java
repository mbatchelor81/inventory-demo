package com.example.inventory_service_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Health Check", description = "API health check and status endpoints")
public class PingController {

    @GetMapping("/ping")
    @Operation(summary = "Health check", description = "Check if the inventory service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy and running")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Inventory Service is up and running!");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}
