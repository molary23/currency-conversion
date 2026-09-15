package com.xe.ratealerts.service.cache;

import static com.xe.ratealerts.utils.Constants.*;
import com.xe.ratealerts.model.dto.AlertDto;
import com.xe.ratealerts.model.dto.CurrencyCacheDto;
import com.xe.ratealerts.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Log4j2
public class CacheBaseService {
    private static final String CACHE_NOT_FOUND = "Cache '{}' not found.";
    private static final String CACHE_PUT_SUCCESS = "Value stored in cache under key '{}' in cache '{}'";

    private final CacheManager redisCacheManager;

    protected CacheBaseService(@Qualifier(REDIS_CACHE_MANAGER) CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    private CurrencyCacheDto saveCurrencies(Supplier<List<String>> supplier, Cache cache) {
        List<String> newCurrencies = supplier.get();
        String now = LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)).toString();
        var currencyToSave = new CurrencyCacheDto(newCurrencies, now);
        if (newCurrencies != null) {
            cache.put(CURRENCY_CACHE_KEY, new CurrencyCacheDto(newCurrencies, now));
            log.info(CACHE_PUT_SUCCESS, CURRENCY_CACHE_KEY, CURRENCY_CACHE);
        } else {
            log.info("No currencies fetched to store in cache under key '{}' in cache '{}'", CURRENCY_CACHE_KEY, CURRENCY_CACHE);
        }
        return currencyToSave;
    }

    public Optional<AlertDto> getAlert(String id) {
        log.info("Fetching alert for id: {}", id);
        var cache = redisCacheManager.getCache(ALERT_CACHE);
        if (cache == null) {
            log.info(CACHE_NOT_FOUND, ALERT_CACHE);
            return Optional.empty();
        }
        var cachedAlert = cache.get(id, AlertDto.class);
        if (cachedAlert == null) {
            log.info("Cache miss for key '{}' in cache '{}'.", id, ALERT_CACHE);
            return Optional.empty();
        }

        log.info("Cache hit for key '{}' in cache '{}'", id, ALERT_CACHE);
        return Optional.of(cachedAlert);
    }

    public void saveAlert(String userId, AlertDto alertDto) {
        log.info("Saving alert for id: {}", userId);
        var cache = redisCacheManager.getCache(ALERT_CACHE);
        if (cache == null) {
            log.info(CACHE_NOT_FOUND, ALERT_CACHE);
            return;
        }
        cache.put(userId, alertDto);
        log.info(CACHE_PUT_SUCCESS, userId, ALERT_CACHE);
    }

    public CurrencyCacheDto getCurrenciesFromCacheOrFetch(Supplier<List<String>> supplier) {
        log.info("Fetching currencies from cache");
        var cache = redisCacheManager.getCache(CURRENCY_CACHE);
        if (cache == null) {
            log.info(CACHE_NOT_FOUND, CURRENCY_CACHE);
            return new CurrencyCacheDto(Collections.emptyList(), null);
        }
        var cachedCurrencies = cache.get(CURRENCY_CACHE_KEY, CurrencyCacheDto.class);
        if (cachedCurrencies != null) {
            log.info("Cache hit for key '{}' in cache '{}'", CURRENCY_CACHE_KEY, CURRENCY_CACHE);
            return cachedCurrencies;
        }
        log.info("Cache miss for key '{}' in cache '{}'.", CURRENCY_CACHE_KEY, CURRENCY_CACHE);

        return saveCurrencies(supplier, cache);
    }

    public void refreshCurrenciesCache(Supplier<List<String>> supplier) {
        var cache = redisCacheManager.getCache(CURRENCY_CACHE);
        if (cache == null) {
            log.info(CACHE_NOT_FOUND, CURRENCY_CACHE);
            return;
        }
        saveCurrencies(supplier, cache);
    }
}
