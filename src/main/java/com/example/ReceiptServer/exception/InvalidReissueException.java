package com.example.ReceiptServer.exception;

public class InvalidReissueException extends RuntimeException{
    public InvalidReissueException()
    {
        super("This token is not available for reissuing.");
    }
}
