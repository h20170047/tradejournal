package com.svj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "TBL_TraderPreference")
@AllArgsConstructor
@NoArgsConstructor
@Builder
// all desired values for a particular trader will be set as default, if not mentioned in TradeEntry.class
public class TraderPreference {
    @Id
    private String id;
    private String traderName;
    private Double capital;
    private String product;
    private String position;
}
