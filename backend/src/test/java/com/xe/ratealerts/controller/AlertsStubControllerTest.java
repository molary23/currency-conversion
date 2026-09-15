package com.xe.ratealerts.controller;

import static com.xe.ratealerts.utils.TestUtils.RATE_URL;
import static com.xe.ratealerts.utils.TestUtils.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import com.xe.ratealerts.model.dto.Alert;
import com.xe.ratealerts.model.dto.response.AlertResponseDto;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.service.AlertsStubService;
import com.xe.ratealerts.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
class AlertsStubControllerTest {
    @MockitoBean
    private WebTestClient webTestClient;
    @MockitoBean
    private AlertsStubService alertsStubService;
    @InjectMocks
    private AlertsStubController alertsStubController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(new AlertsStubController(alertsStubService)).build();
        alertsStubController = spy(new AlertsStubController(alertsStubService));
    }

    @AfterEach
    void tearDown() {
        alertsStubController = null;
        webTestClient = null;
    }

    @Test
    void testGetRates_success() {
        AlertResponseDto expected = TestUtils.createTestAlertResponseDto();
        ResponseDto responseDto = new ResponseDto(200, Flux.just(expected));
        when(alertsStubService.getAlerts(anyString())).thenReturn(responseDto);

        WebTestClient.ResponseSpec responseSpec = webTestClient
            .get()
            .uri(RATE_URL)
            .headers(headers -> headers.set("X-User-ID", USER_ID))
            .exchange();

        responseSpec.expectStatus().isOk();

        FluxExchangeResult<AlertResponseDto> responseExchange = responseSpec.returnResult(AlertResponseDto.class);

        StepVerifier.create(responseExchange.getResponseBody())
            .expectNext(expected)
            .verifyComplete();
    }

    @Test
    void testGetRates_failure() {
        when(alertsStubService.getAlerts(anyString())).thenReturn(new ResponseDto(404, "Alerts not found"));
        webTestClient.get()
            .uri(RATE_URL)
            .headers(headers -> headers.set("X-User-ID", USER_ID))
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void testCreateAlert_success() {
        Alert expected = TestUtils.createTestAlert();
        ResponseDto responseDto = new ResponseDto(200, expected);
        when(alertsStubService.createAlert(anyString(), any())).thenReturn(responseDto); // Mock the response

        webTestClient.post()
            .uri(RATE_URL)
            .headers(headers -> headers.set("X-User-ID", USER_ID))
            .bodyValue(TestUtils.createTestCreateAlertRequestDto())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Alert.class)
            .isEqualTo(expected);
    }

    @Test
    void testCreateAlert_failure() {
        ResponseDto responseDto = new ResponseDto(400, "Invalid alert threshold");
        when(alertsStubService.createAlert(anyString(), any())).thenReturn(responseDto); // Mock the response

        webTestClient.post()
            .uri(RATE_URL)
            .headers(headers -> headers.set("X-User-ID", USER_ID))
            .bodyValue(TestUtils.createTestCreateAlertRequestDto())
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void testDeleteAlert_success() {
        ResponseDto responseDto = new ResponseDto(204);
        when(alertsStubService.deleteAlert(anyString(), anyString())).thenReturn(responseDto); // Mock the response

        webTestClient.delete()
            .uri(RATE_URL + "/" + TestUtils.ALERT_ID)
            .headers(headers -> headers.set("X-User-ID", USER_ID))
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    void testDeleteAlert_failure() {
        ResponseDto responseDto = new ResponseDto(404);
        when(alertsStubService.deleteAlert(anyString(), anyString())).thenReturn(responseDto); // Mock the response

        webTestClient.delete()
            .uri(RATE_URL + "/" + TestUtils.ALERT_ID)
            .headers(headers -> headers.set("X-User-ID", USER_ID))
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}



