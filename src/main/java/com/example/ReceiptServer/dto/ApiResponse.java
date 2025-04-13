package com.example.ReceiptServer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API response wrapper")
public class ApiResponse<T> {
    @Schema(description = "Response message")
    private String message;
    @Schema(description = "API Response data including tokens")
    private T data;
    @Schema(description = "HTTP status code")
    private int status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ApiResponse(String message, T data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }
}
