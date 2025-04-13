package com.example.ReceiptServer.service.oauth;

import com.example.ReceiptServer.comm.ApiClient;
import com.example.ReceiptServer.exception.InvalidOAuthTokenException;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KakaoOAuthProvider implements OAuthProvider {

    private final ApiClient apiClient;

    @Value("${KAKAO_OAUTH_URL")
    private static String kakaoOAuthUrl;

    @Override
    public OAuthUserInfo validateToken(String accessToken) {
        // Set up headers for Kakao API
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept-Charset", "UTF-8");

        try {
            // Use ApiClient to make the request
            String response = apiClient.sendRequest(
                    kakaoOAuthUrl,
                    HttpMethod.GET,
                    headers,
                    null,
                    String.class
            );

            // Parse the JSON response
            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            JSONObject userInfo = (JSONObject) parser.parse(response);
            return new OAuthUserInfo(userInfo.getAsString("id"));
        } catch (Exception e) {
            throw new InvalidOAuthTokenException("Failed to validate Kakao token: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "kakao";
    }
}