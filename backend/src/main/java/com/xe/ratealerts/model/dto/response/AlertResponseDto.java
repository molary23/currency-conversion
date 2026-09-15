package com.xe.ratealerts.model.dto.response;

import java.math.BigDecimal;

public record AlertResponseDto(String id, String pair, BigDecimal threshold, String direction, boolean triggered) {
}
