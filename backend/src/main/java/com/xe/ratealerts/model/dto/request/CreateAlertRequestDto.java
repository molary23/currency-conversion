package com.xe.ratealerts.model.dto.request;

import java.math.BigDecimal;

public record CreateAlertRequestDto(String pair, BigDecimal threshold, String direction) {
}
