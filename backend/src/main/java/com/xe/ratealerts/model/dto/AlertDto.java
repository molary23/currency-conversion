package com.xe.ratealerts.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertDto {
    private List<Alert> alerts;
    private String lastUpdated;

}
