package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.svj.utilities.Constants.POSITION;
import com.svj.utilities.Constants.PRODUCT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PreferenceResponseDTO {
    private String id;
    private String traderName;
    private Double capital;
    private String position;
    private String product;
}
