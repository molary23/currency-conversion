package com.xe.ratealerts.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
    private String id;
    private String pair;
    private BigDecimal threshold;
    private String direction;

}
