package com.svj.exceptionHandling;

public class FileException extends RuntimeException {
    public FileException(String message) {
        super(message);
    }
}
