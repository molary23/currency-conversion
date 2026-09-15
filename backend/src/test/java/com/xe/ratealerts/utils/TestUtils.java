package com.xe.ratealerts.utils;

import com.xe.ratealerts.model.dto.Alert;
import com.xe.ratealerts.model.dto.request.CreateAlertRequestDto;
import com.xe.ratealerts.model.dto.response.AlertResponseDto;

import java.math.BigDecimal;

public class TestUtils {
    public static final String USER_ID = "user-id";
    public static final String ALERT_ID = "alert-id";
    public static final String DIRECTION = "above";
    public static final BigDecimal THRESHOLD = new BigDecimal("1.2");
    public static final String PAIR = "USD/EUR";
    public static final String RATE_URL = "/api/alerts";

    public static AlertResponseDto createTestAlertResponseDto() {
        return new AlertResponseDto(USER_ID, PAIR, THRESHOLD, DIRECTION, true);
    }

    public static Alert createTestAlert() {
        return new Alert(ALERT_ID, PAIR, THRESHOLD, DIRECTION);
    }

    public static CreateAlertRequestDto createTestCreateAlertRequestDto() {
        return new CreateAlertRequestDto(PAIR, THRESHOLD, DIRECTION);
    }


}
