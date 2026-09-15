package com.xe.ratealerts.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyCacheDto {
    private List<String> currencies;
    private String lastUpdated;
}
