package com.svj.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svj.exceptionHandling.TradeProcessException;

public class JsonParser {
    public static ObjectMapper objectMapper= new ObjectMapper();
    static{
        objectMapper.registerModule(new JavaTimeModule());
    }
    public static String jsonToString(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new TradeProcessException(e.getMessage());
        }
    }
}
