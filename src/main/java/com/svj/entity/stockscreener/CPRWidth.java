package com.svj.entity.stockscreener;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CPRWidth {
    private String name;
    private String sector;
    private String cprWidth;
    private boolean narrowCPR;
}
