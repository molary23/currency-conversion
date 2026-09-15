package com.xe.ratealerts.controller;

import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currencies")
@Log4j2
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<String>> getCurrencies() {
        log.info("Getting list of currencies");

        List<String> currenciesList = currencyService.getAllCurrencies();
        if (currenciesList == null || currenciesList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(currenciesList);
    }

    @PutMapping("/refresh")
    public ResponseEntity<Object> refreshCurrenciesCache() {
        ResponseDto responseDto = currencyService.refreshCurrenciesCache();
        return ResponseEntity.status(responseDto.getStatus()).build();
    }
}
