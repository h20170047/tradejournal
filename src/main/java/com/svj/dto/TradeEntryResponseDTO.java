package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.svj.utilities.Constants.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
// TODO- If T1, T2, Sl % are 0 or -ve return error
public class TradeEntryResponseDTO {
    private String id;
    private String traderName;
    private Double capital;
    private String symbol;
    private POSITION position;
    private PRODUCT product;
    private int quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="d-M-yyyy")
    private LocalDate entryDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="d-M-yyyy")
    private LocalDate exitDate;
    private Double entryPrice;
    private double SL;
    private double SLPercent;
    private double riskPercent; // if capital is not provided, assume 3K or take from absolute
    private double T1;
    private double T1Percent;
    private Double T2;
    private Double T2Percent;
    private Double exitPrice;
    private Double profit;
    private Double profitPercent;
    private String entryComments;
    private String exitComments;
    private String remarks;
}
