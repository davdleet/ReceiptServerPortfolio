package com.example.ReceiptServer.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException()
    {
        super("User was not found.");
    }
}
