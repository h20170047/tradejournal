package com.svj.exceptionHandling;

import com.svj.dto.ErrorDto;
import com.svj.dto.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class BussinessExceptionHandling {
    @ExceptionHandler(TradeProcessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceResponse handleTradeProcessException(TradeProcessException exception){
        ServiceResponse response= new ServiceResponse();
        response.setResponse(HttpStatus.BAD_REQUEST);
        response.setErrors(Arrays.asList(new ErrorDto(exception.getMessage())));
        return response;
    }
}
