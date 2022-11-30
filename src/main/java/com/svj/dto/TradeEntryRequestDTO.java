package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sun.awt.image.PixelConverter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeEntryRequestDTO {
    private String symbol;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="d-M-yyyy[ [H][:m][:s][.S]]")
    private LocalDateTime buy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="d-M-yyyy[ [H][:m][:s][.S]]")
    private LocalDateTime sell;
    @DecimalMin("0.0")
    private double stopLoss;
    @DecimalMin("0.0")
    private double target;
    private double profit;
    private double buyPrice;
    private double sellPrice;
    private String comments;
}
