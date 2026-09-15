package com.xe.ratealerts.model.dto.response.rates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateResponse {
    private int amount;
    private String terms;
    private String privacy;
    private String from;
    @JsonProperty("to")
    private List<ToItem> to;
    private String timestamp;
}