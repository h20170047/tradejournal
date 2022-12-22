package com.svj.entity.stockscreener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// used to filter stocks based on open and close. H, L can be used for other strategies
public class Stock {
    private String symbol;
    private double close; // LTP of the day will be close price for the day.
    private double dayHigh;
    private double dayLow;
    private double open;
    private LocalDateTime lastUpdateTime;
}
