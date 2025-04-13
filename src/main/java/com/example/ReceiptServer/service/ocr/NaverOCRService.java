package com.example.ReceiptServer.service.ocr;

import com.example.ReceiptServer.comm.ApiClient;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NaverOCRService implements OCRService {
    @Value("${OCR_SECRET_KEY}")
    private String ocrSecret;

    @Value("${NAVER_OCR_URL}")
    private String ocrUrl;

    private final ApiClient apiClient;

    public NaverOCRService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<String> extractItemsFromImage(MultipartFile file) {
        byte[] imageBytes = null;
        try{
            imageBytes = file.getBytes();
        }
        catch (Exception e)
        {
            if (e instanceof IOException)
            {
                throw new RuntimeException("Error while reading file");
            }
        }
        String fileName = file.getOriginalFilename();
        return extractItemsFromImage(imageBytes, fileName);
    }

    public List<String> extractItemsFromImage(byte[] imageBytes, String filename) {
        String fileFormat = getFileFormat(filename);
        if (fileFormat.isEmpty()) {
            throw new IllegalArgumentException("File extension was invalid");
        }

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-OCR-SECRET", ocrSecret);

        // Prepare multipart body
        MultiValueMap<String, Object> body = prepareMultipartBody(imageBytes, filename, fileFormat);

        // Send request using the client
        Map<String, Object> response = apiClient.sendMultipartRequest(ocrUrl, headers, body, Map.class);

        // Extract text from response
        return extractTextFromResponse(response);
    }

    private MultiValueMap<String, Object> prepareMultipartBody(byte[] imageBytes, String filename, String fileFormat) {
        // JSON metadata part
        String messageJson = getInputMessageJSON(fileFormat);
        HttpHeaders jsonPartHeaders = new HttpHeaders();
        jsonPartHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // File part
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        // Combine parts
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Add parts with respective headers
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        body.add("message", new HttpEntity<>(messageJson, jsonHeaders));

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        body.add("file", new HttpEntity<>(imageResource, fileHeaders));

        return body;
    }

    public String getInputMessageJSON(String fileExtension) {
        String requestId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        // Clean up the file extension just in case
        String format = fileExtension.toLowerCase().replace(".", "");

        return String.format(
                "{" +
                        "  \"version\": \"V2\"," +
                        "  \"requestId\": \"%s\"," +
                        "  \"timestamp\": %d," +
                        "  \"images\": [" +
                        "    {" +
                        "      \"format\": \"%s\"," +
                        "      \"name\": \"demo\"" +
                        "    }" +
                        "  ]" +
                        "}", requestId, timestamp, format
        );
    }

    public String getFileFormat(String filename) {
        String fileExtension = "";

        if (filename != null && filename.contains(".")) {
            fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(); // e.g., "png"
        }
        return fileExtension;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractTextFromResponse(Map<String, Object> responseBody) {
        List<String> results = new ArrayList<>();

        if (responseBody == null || !responseBody.containsKey("images")) {
            return results;
        }

        List<Map<String, Object>> images = (List<Map<String, Object>>) responseBody.get("images");

        for (Map<String, Object> image : images) {
            if (!image.containsKey("fields")) {
                continue;
            }

            List<Map<String, Object>> fields = (List<Map<String, Object>>) image.get("fields");
            StringBuilder buffer = new StringBuilder();

            for (Map<String, Object> field : fields) {
                String inferText = (String) field.get("inferText");
                Boolean lineBreak = (Boolean) field.get("lineBreak");

                if (inferText != null && !inferText.isBlank()) {
                    buffer.append(inferText);

                    if (Boolean.TRUE.equals(lineBreak)) {
                        results.add(buffer.toString());
                        buffer = new StringBuilder();
                    } else {
                        buffer.append(" ");
                    }
                }
            }

            // Add any remaining text
            if (buffer.length() > 0) {
                results.add(buffer.toString().trim());
            }
        }

        return results;
    }
}