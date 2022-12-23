package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.svj.utilities.Constants.POSITION;
import com.svj.utilities.Constants.PRODUCT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class TradeEntryRequestDTO {
    @NotBlank
    private String traderName;
    private Double capital;
    @NotBlank
    private String symbol;
    // provision to override
    private POSITION position;
    private PRODUCT product;
    @NotNull
    private Integer quantity;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="d-M-yyyy")
    private LocalDate entryDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="d-M-yyyy")
    private LocalDate exitDate;
    @NotNull
    private Double entryPrice;
    @NotNull
    private Double SL;
    @NotNull
    private Double T1;
    private Double T2;
    private Double exitPrice;
    private Double profit;
    private String entryComments;
    private String exitComments;
    private String remarks;
}
