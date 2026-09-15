package com.xe.ratealerts.service;

import static com.xe.ratealerts.utils.Constants.ABOVE;
import static com.xe.ratealerts.utils.Constants.BELOW;
import com.xe.ratealerts.helper.RestHelper;
import com.xe.ratealerts.model.dto.Alert;
import com.xe.ratealerts.model.dto.AlertDto;
import com.xe.ratealerts.model.dto.request.CreateAlertRequestDto;
import com.xe.ratealerts.model.dto.response.AlertResponseDto;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.model.dto.response.rates.ToItem;
import com.xe.ratealerts.service.cache.CacheBaseService;
import com.xe.ratealerts.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
@Log4j2
@Service
public class AlertsStubService {
    private static final String NO_ALERT_MESSAGE = "No alerts found for user with id {}";
    private final CacheBaseService cacheBaseService;
    private final CurrencyService currencyService;
    private final RestHelper restHelper;

    @NotNull
    private List<Alert> getUpdatedAlerts(CreateAlertRequestDto alertDto, AlertDto alert, Alert alertToSave) {
        List<Alert> alerts = alert.getAlerts();
        List<Alert> updatedAlerts = new ArrayList<>();
        alerts.forEach(a -> {
            if (a.getPair().equalsIgnoreCase(alertDto.pair()) && a.getDirection().equalsIgnoreCase(alertDto.direction())) {
                log.info("Alert for this pair and direction already exists. Changing its threshold to the new value.");
                return;
            }
            updatedAlerts.add(a);
        });
        updatedAlerts.add(alertToSave);
        return updatedAlerts;
    }

    public ResponseDto createAlert(String id, CreateAlertRequestDto alertDto) {
        log.info("Creating {} alert for {}", alertDto, id);
        if (alertDto.threshold() == null) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "Invalid alert threshold");
        }
        if (!StringUtils.hasText(alertDto.direction()) ||
            (!alertDto.direction().equalsIgnoreCase(ABOVE) && !alertDto.direction().equalsIgnoreCase(BELOW))) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "Invalid alert direction");
        }

        ResponseDto pairValidation = isValidPair(alertDto.pair());
        if (pairValidation != null) {
            return pairValidation;
        }

        Optional<AlertDto> alert = cacheBaseService.getAlert(id);
        Alert alertToSave = new Alert(
            UUID.randomUUID().toString(),
            alertDto.pair(),
            alertDto.threshold().setScale(4, RoundingMode.HALF_UP),
            alertDto.direction()
        );
        String now = LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)).toString();
        if (alert.isEmpty()) {
            AlertDto newAlert = new AlertDto(List.of(alertToSave), now);
            cacheBaseService.saveAlert(id, newAlert);
            return new ResponseDto(HttpStatus.CREATED.value(), alertToSave);
        } else {
            List<Alert> updatedAlerts = getUpdatedAlerts(alertDto, alert.get(), alertToSave);
            AlertDto updatedAlert = new AlertDto(updatedAlerts, now);
            cacheBaseService.saveAlert(id, updatedAlert);
        }
        return new ResponseDto(HttpStatus.CREATED.value(), alertToSave);
    }

    private ResponseDto isValidPair(String pair) {
        if (!StringUtils.hasText(pair)) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "Invalid pair format");
        }

        String[] parts = pair.split("/");
        if (parts.length != 2) {
            log.error("Invalid pair format: {}", pair);
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "Invalid pair format");
        }
        List<String> currencies = currencyService.getAllCurrencies();
        if (currencies == null || currencies.isEmpty()) {
            return new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to fetch supported currencies");
        }
        String baseCurrency = parts[0].strip();
        String quoteCurrency = parts[1].strip();
        if (!currencies.contains(baseCurrency) || !currencies.contains(quoteCurrency)) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "Invalid pair: one or both currencies are not supported");
        }
        return null;
    }

    private List<Alert> getAlertsFromCache(String userId) {
        Optional<AlertDto> alertDtoOptional = cacheBaseService.getAlert(userId);
        if (alertDtoOptional.isEmpty()) {
            log.info(NO_ALERT_MESSAGE, userId);
            return Collections.emptyList();
        }
        AlertDto alertDto = alertDtoOptional.get();
        if (alertDto.getAlerts().isEmpty()) {
            log.info(NO_ALERT_MESSAGE, userId);
            return Collections.emptyList();
        }
        return alertDto.getAlerts();
    }

    public ResponseDto deleteAlert(String userId, String alertId) {
        log.info("Delete alert {}", alertId);

        List<Alert> alerts = getAlertsFromCache(userId);
        if (alerts.isEmpty()) {
            log.info(NO_ALERT_MESSAGE, userId);
            return new ResponseDto(HttpStatus.NOT_FOUND.value());
        }
        boolean isFound = false;
        List<Alert> updatedAlerts = new ArrayList<>();
        for (Alert a : alerts) {
            if (a.getId().equals(alertId)) {
                isFound = true;
                continue;
            }
            updatedAlerts.add(a);
        }
        if (!isFound) {
            log.info("Alert with id: {} not found for user with id {}", alertId, userId);
            return new ResponseDto(HttpStatus.NOT_FOUND.value());
        }
        String now = LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)).toString();
        AlertDto updatedAlertDto = new AlertDto(updatedAlerts, now);
        cacheBaseService.saveAlert(userId, updatedAlertDto);
        log.info("Alert with id: {} deleted successfully for user with id {}", alertId, userId);
        return new ResponseDto(HttpStatus.NO_CONTENT.value());
    }

    public ResponseDto getAlerts(String userId) {
        log.info("Get alerts for user with id {}", userId);
        List<Alert> alerts = getAlertsFromCache(userId);
        if (alerts.isEmpty()) {
            log.info(NO_ALERT_MESSAGE, userId);
            return new ResponseDto(HttpStatus.NOT_FOUND.value());
        }

        Flux<AlertResponseDto> alertResponseDtoFlux = Flux.fromIterable(alerts)
            .flatMap(alert -> {
                String pair = alert.getPair();
                String[] parts = pair.split("/");
                String from = parts[0].strip();
                String to = parts[1].strip();
                return restHelper.getRates(from, to).filter(Objects::nonNull).map(r -> {

                    boolean isTriggered;
                    BigDecimal mid = getMid(r.getTo(), pair);
                    log.info("Getting trigger status for alert: {}, Threshold: {} and current rate: {}", alert.getId(), alert.getThreshold(), mid);
                    if (alert.getDirection().equalsIgnoreCase(ABOVE)) {
                        isTriggered = mid.compareTo(alert.getThreshold()) > 0;
                    } else {
                        isTriggered = mid.compareTo(alert.getThreshold()) < 0;
                    }
                    return new AlertResponseDto(alert.getId(), pair, alert.getThreshold(), alert.getDirection(), isTriggered);
                });
            });
        return new ResponseDto(HttpStatus.OK.value(), alertResponseDtoFlux);
    }

    private BigDecimal getMid(List<ToItem> tos, String pair) {
        if (tos == null || tos.isEmpty()) {
            log.info("Mid rate not found for pair: {}", pair);
            return BigDecimal.ZERO;
        }
        if (tos.getFirst() == null || tos.getFirst().getMid() == null) {
            log.info("Mid rate not found for pair: {}", pair);
            return BigDecimal.ZERO;
        }
        return tos.getFirst().getMid();
    }

    public ResponseDto getAllAlerts(String systemId) {
        log.info("Fetching all alerts for user with id {}", systemId);
        List<Alert> alerts = getAlertsFromCache(systemId);
        if (alerts.isEmpty()) {
            log.info(NO_ALERT_MESSAGE, systemId);
            return new ResponseDto(HttpStatus.NOT_FOUND.value());
        }
        return new ResponseDto(HttpStatus.OK.value(), alerts);
    }
}
