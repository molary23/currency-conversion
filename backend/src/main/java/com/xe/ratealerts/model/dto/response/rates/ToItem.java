package com.xe.ratealerts.model.dto.response.rates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToItem {
    private BigDecimal mid;
    @JsonProperty("quotecurrency")
    private String quoteCurrency;
}