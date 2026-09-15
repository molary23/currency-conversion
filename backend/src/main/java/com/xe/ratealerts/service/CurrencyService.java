package com.xe.ratealerts.service;

import com.xe.ratealerts.helper.RestHelper;
import com.xe.ratealerts.model.dto.CurrencyCacheDto;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.service.cache.CacheBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
public class CurrencyService {
    private final CacheBaseService cacheBaseService;
    private final RestHelper restHelper;

    private CurrencyCacheDto getCurrenciesFromCacheOrFetch() {
        log.info("Getting currencies");
        return cacheBaseService.getCurrenciesFromCacheOrFetch(restHelper::getCurrencies);
    }

    public List<String> getAllCurrencies() {
        log.info("Getting currencies list");
        return getCurrenciesFromCacheOrFetch().getCurrencies();
    }

    public ResponseDto refreshCurrenciesCache() {
        try {
            cacheBaseService.refreshCurrenciesCache(restHelper::getCurrencies);
            return new ResponseDto(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            return new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
