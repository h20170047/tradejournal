package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeEntryResponseDTO {
    private String id;
    private String symbol;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd-MM-yyyy[ [HH][:mm][:ss][.SSS]]")
    private LocalDateTime buy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd-MM-yyyy[ [HH][:mm][:ss][.SSS]]")
    private LocalDateTime sell;
    private double stopLoss;
    private double target;
    private double profit;
    private double buyPrice;
    private double sellPrice;
    private String comments;
}
