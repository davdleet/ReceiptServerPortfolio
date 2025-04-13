package com.example.ReceiptServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Status", description = "Operations for server status")
public class StatusController {
    @GetMapping("/status")
    @Operation(
            summary = "Get current server status",
            description = "Returns the current operational status of the server including uptime information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Server status retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Normal Status",
                                            summary = "Server running normally",
                                            value = "{\"status\":\"UP\",\"timestamp\":\"2023-08-15T14:30:45.123\",\"version\":\"1.0\"}"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> getServerStatus()
    {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("version", "1.0");

        return ResponseEntity.ok(status);

    }

}