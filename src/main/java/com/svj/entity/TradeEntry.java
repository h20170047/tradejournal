package com.svj.entity;

import com.svj.utilities.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Document(collection = "TBL_TradeEntries")
@AllArgsConstructor
@NoArgsConstructor
@Builder
// All mandatory fields are validated as not blank. Other derivable fields will be computed in service. Remaining fields can be saved once journal is finished
public class TradeEntry {
    @Id
    private String id;
    private String traderName;
    private Double capital;
    private String symbol;
    private Constants.POSITION position;
    private Constants.PRODUCT product;
    private int quantity;
    private LocalDate entryDate;
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
