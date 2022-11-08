package com.svj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.svj.dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> {
    public HttpStatus status;
    public T response;
    public List<ErrorDto> errors;
}
