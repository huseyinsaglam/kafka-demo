package com.example.kafkademo.domain.exception;

public class MyCustomException extends Exception {
    public MyCustomException(String message) {
        super(message);
    }
}