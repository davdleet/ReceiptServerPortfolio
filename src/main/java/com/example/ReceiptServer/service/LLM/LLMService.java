package com.example.ReceiptServer.service.LLM;

import net.minidev.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface LLMService {
    public String GetStringResponseFromQuery(String query, Map<String, Object> param);
    public Map<String, Object> GetMapResponseFromQuery(String query, Map<String, Object> param);
    public String GetResultKey();
}
