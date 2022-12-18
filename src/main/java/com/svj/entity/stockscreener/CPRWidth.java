package com.svj.entity.stockscreener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CPRWidth {
    private String name;
    private String cprWidth;
    private boolean narrowCPR;
}
