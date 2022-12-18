package com.svj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeStats {
    private LocalDate fromDate;
    private LocalDate toDate;
    private int totalTrades;
    private int winCount;
    private int lossCount;
    private double winProbability;
    private double totalProfit;
    private List<String> successComments;
    private List<String> cautionComments;
}
