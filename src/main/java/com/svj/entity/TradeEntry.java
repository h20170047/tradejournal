package com.svj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "TradeEntries")
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntry {
    @Id
    private String id;
    private String symbol;
    private LocalDateTime buy;
    private LocalDateTime sell;
    private double stopLoss;
    private double target;
    private double profit;
    private double buyPrice;
    private double sellPrice;
    private String comments;
}
