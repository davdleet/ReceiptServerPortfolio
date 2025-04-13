package com.example.ReceiptServer.service.receipt;

import com.example.ReceiptServer.service.LLM.ChatGptLLMService;
import com.example.ReceiptServer.service.ocr.OCRService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptService {

    @Autowired
    private OCRService ocrService;

    @Autowired
    private ChatGptLLMService chatGptLLMService;

    // Change return type from JSONArray to List<Object>
    public List<Object> processReceiptImage(MultipartFile file) throws IOException {
        List<String> extractedItems = ocrService.extractItemsFromImage(file);
        String queryString = formQueryExtractedList(extractedItems);
        Map<String, Object> LLMResult = chatGptLLMService.GetMapResponseFromQuery(queryString, null);
        String resultKey = chatGptLLMService.GetResultKey();
        return formFoodListFromResponseMap(LLMResult, resultKey);
    }

    public String formQueryExtractedList(List<String> extractedItems) {
        if (extractedItems == null || extractedItems.isEmpty()) return "";
        StringBuilder query = new StringBuilder(
                "From the following string, return the list of foods in JSON format. Each Item will have a name, and a count field. " +
                        "There is no need for formatting such as newlines and tabs. Return the literal.\n"
        );
        for (String s : extractedItems) {
            query.append(s).append("\n");
        }
        return query.toString();
    }

    public List<Object> formFoodListFromResponseMap(Map<String, Object> responseObject, String resultKey) {
        if (responseObject == null || responseObject.isEmpty() || !responseObject.containsKey(resultKey)) {
            return new ArrayList<>();
        }

        String response = ((String) responseObject.get(resultKey)).trim();
        try {
            // Parse the string as a JSONArray and convert it to a List
            JSONArray jsonArray = new JSONArray(response);
            return jsonArray.toList();
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
