package com.example.ReceiptServer.service.receipt;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

@AllArgsConstructor
@Data
public class ReceiptAnalysisResponse {
    private JSONObject itemJSON;
}
