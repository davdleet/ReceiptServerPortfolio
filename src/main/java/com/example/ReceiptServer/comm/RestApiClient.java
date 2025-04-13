package com.example.ReceiptServer.comm;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class RestApiClient implements ApiClient {
    private final RestTemplate restTemplate;

    public RestApiClient() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public <T, R> R sendRequest(String url, HttpMethod method, HttpHeaders headers, T body, Class<R> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<R> response = restTemplate.exchange(url, method, requestEntity, responseType);
        return response.getBody();
    }

    @Override
    public <R> R sendMultipartRequest(String url, HttpHeaders headers, MultiValueMap<String, Object> parts, Class<R> responseType) {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
        ResponseEntity<R> response = restTemplate.postForEntity(url, requestEntity, responseType);
        return response.getBody();
    }
}