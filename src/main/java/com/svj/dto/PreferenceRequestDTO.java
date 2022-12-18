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
public class PreferenceRequestDTO {
    @NotBlank
    private String traderName;
    @NotBlank
    private Double capital;
    @NotBlank
    private POSITION position;
    @NotBlank
    private PRODUCT product;
}