package com.svj.exceptionHandling;

public class StockProcessingException extends RuntimeException{
    public StockProcessingException(String message){
        super(message);
    }
}
