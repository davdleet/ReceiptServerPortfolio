package com.example.ReceiptServer.service.ocr;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OCRService {
    public List<String> extractItemsFromImage(MultipartFile file);
}
