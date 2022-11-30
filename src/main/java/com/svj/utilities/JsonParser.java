package com.svj.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svj.exceptionHandling.TradeProcessException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class JsonParser {
    public static ObjectMapper objectMapper= new ObjectMapper();
    static{
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static DateTimeFormatter dateFormatter= new DateTimeFormatterBuilder()
                                                        .appendPattern("d-M-yyyy[ [H][:m][:s][.S]]")
                                                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                                                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                                                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                                                        .toFormatter();

    public static String jsonToString(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new TradeProcessException(e.getMessage());
        }
    }
}
