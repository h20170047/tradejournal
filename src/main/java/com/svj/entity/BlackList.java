package com.svj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "TBL_BlackList")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackList {
    @Id
    private String traderName;
    private Set<String> blackListedStocks;
}
