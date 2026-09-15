package com.xe.ratealerts.helper;

import static com.xe.ratealerts.utils.Constants.RATE_CLIENT;
import static com.xe.ratealerts.utils.Constants.getVirtualThread;
import com.xe.ratealerts.model.dto.Currency;
import com.xe.ratealerts.model.dto.response.CurrencyResponseDto;
import com.xe.ratealerts.model.dto.response.rates.RateResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Component
public class RestHelper {
    private final WebClient rateWebClient;

    public RestHelper(@Qualifier(RATE_CLIENT) WebClient rateWebClient) {
        this.rateWebClient = rateWebClient;
    }

    public List<String> getCurrencies() {
        log.info("Fetching currencies from external service");
        return rateWebClient.get()
            .uri("/currencies")
            .retrieve()
            .toEntity(CurrencyResponseDto.class)
            .toFuture()
            .handleAsync((responseEntity, throwable) -> {
                if (throwable != null) {
                    log.error("Error fetching currencies: {}", throwable.getMessage());
                    return null;
                }
                CurrencyResponseDto body = responseEntity.getBody();
                if (body == null) {
                    log.error("Error fetching currencies: response body is null");
                    return null;
                }
                return body.currencies().stream().map(Currency::iso).toList();
            }, getVirtualThread())
            .join();
    }

    public Mono<RateResponse> getRates(String from, String to) {
        log.info("Fetching rates for pair {} from external service", from + "/" + to);
        return rateWebClient.get()
            .uri(uriBuilder -> uriBuilder.path("/convert_from")
                .queryParam("from", from)
                .queryParam("to", to)
                .build()
            )
            .retrieve()
            .bodyToMono(RateResponse.class)
            .onErrorResume(throwable -> {
                log.error("Error fetching rates: {}", throwable.getMessage());
                return Mono.empty();
            });
    }
}
