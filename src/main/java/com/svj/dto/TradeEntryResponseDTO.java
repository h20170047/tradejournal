package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.svj.utilities.Constants.POSITION;
import com.svj.utilities.Constants.PRODUCT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Double SL;
    private double SLPercent;
    private double riskPercent; // if capital is not provided, assume 3K or take from absolute
    private Double T1;
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
