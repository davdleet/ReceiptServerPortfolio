package com.example.ReceiptServer.validation;

public interface Validator<T> {
    boolean isValid(T input);
}
