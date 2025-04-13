package com.example.ReceiptServer.controller;

import com.example.ReceiptServer.dto.ApiResponse;
import com.example.ReceiptServer.service.ocr.OCRService;
import com.example.ReceiptServer.service.receipt.ReceiptService;
import com.example.ReceiptServer.utils.AccessTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/receipts")
@AllArgsConstructor
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    private AccessTokenUtil accessTokenUtil;
    private OCRService ocrService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<Object>>> uploadReceipt(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>("No file provided", null, 400));
            }
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("Token not found. Check if the Authorization header is in \"Bearer {RefreshToken}\" format.", null, 400));
            }

            String token = authHeader.substring(7);
            UUID userId = accessTokenUtil.getUserIdFromToken(token);

            List<Object> result = receiptService.processReceiptImage(file);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>("Image process complete", result, 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null, 500));
        }
    }
}
