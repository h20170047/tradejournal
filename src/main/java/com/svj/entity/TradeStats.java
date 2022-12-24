package com.svj.entity;

import com.svj.dto.TradeEntryResponseDTO;
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
    private int openTradeCount;
    private int winCount;
    private int lossCount;
    private int totalPoints; //win- loss
    private double winProbability;
    private double totalProfit;
    private double totalCapitalGain;
    private List<String> successComments;
    private List<TradeEntryResponseDTO> openTrades;
    private List<String> cautionComments;
}
