package com.xe.ratealerts.model.dto.response;

import com.xe.ratealerts.model.dto.Currency;

import java.util.List;

public record CurrencyResponseDto(String terms, String privacy, List<Currency> currencies) {
}
