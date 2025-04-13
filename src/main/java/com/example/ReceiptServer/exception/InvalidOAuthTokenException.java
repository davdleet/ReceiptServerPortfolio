package com.example.ReceiptServer.exception;

public class InvalidOAuthTokenException extends RuntimeException  {
    public InvalidOAuthTokenException(String invalidIssuer) {
        super(invalidIssuer);
    }

    public InvalidOAuthTokenException()
    {
        super("The OAuth Token provided is Invalid");
    }
}
