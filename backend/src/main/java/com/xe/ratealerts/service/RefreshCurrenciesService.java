package com.xe.ratealerts.service;

import static com.xe.ratealerts.utils.Constants.ZONE_ID;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class RefreshCurrenciesService {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${rate.scheduler}", zone = ZONE_ID)
    public void refreshCurrencies() {
        // Logic to refresh currencies from external service
        log.info("Refreshing currencies from job");
        ResponseDto response = currencyService.refreshCurrenciesCache();
        if (response.getStatus() == HttpStatus.OK.value()) {
            log.info("Currencies refreshed");
        } else {
            log.info("Currencies refresh failed");
        }
    }
}
