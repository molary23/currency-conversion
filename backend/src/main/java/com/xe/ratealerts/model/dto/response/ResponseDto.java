package com.xe.ratealerts.model.dto.response;

import com.xe.ratealerts.model.dto.Alert;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

@Data
public class ResponseDto {
    private int status;
    private String error;
    private Alert savedAlert;
    private boolean success;
    private AlertResponseDto alertResponseDto;
    private Flux<AlertResponseDto> alertResponseDtoFlux;
    private List<Alert> alerts;

    public ResponseDto(int status, String error) {
        this.status = status;
        this.error = error;
        this.success = false;
    }

    public ResponseDto(int status, Alert savedAlert) {
        this.status = status;
        this.savedAlert = savedAlert;
        this.success = true;
    }

    public ResponseDto(int status, Flux<AlertResponseDto> alertResponseDtoFlux) {
        this.status = status;
        this.alertResponseDtoFlux = alertResponseDtoFlux;
        this.success = true;
    }

    public ResponseDto(int status) {
        this.status = status;
    }

    public ResponseDto(int status, List<Alert> alerts) {
        this.status = status;
        this.alerts = alerts;
        this.success = true;
    }
}
