package com.example.ReceiptServer.comm;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

public interface ApiClient {
    /**
     * Send a request to an external API
     *
     * @param url The endpoint URL
     * @param method HTTP method
     * @param headers Custom headers
     * @param body Request body
     * @param responseType Expected response type
     * @return Response from the API
     */
    <T, R> R sendRequest(String url, HttpMethod method, HttpHeaders headers, T body, Class<R> responseType);

    /**
     * Send a multipart form request to an external API
     *
     * @param url The endpoint URL
     * @param headers Custom headers
     * @param parts Multipart form data
     * @param responseType Expected response type
     * @return Response from the API
     */
    <R> R sendMultipartRequest(String url, HttpHeaders headers, MultiValueMap<String, Object> parts, Class<R> responseType);
}