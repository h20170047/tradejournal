package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.svj.utilities.Constants.POSITION;
import com.svj.utilities.Constants.PRODUCT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PreferenceRequestDTO {
    @NotNull
    private String traderName;
    @NotNull
    private Double capital;
    @NotNull
    private POSITION position;
    @NotNull
    private PRODUCT product;
}
