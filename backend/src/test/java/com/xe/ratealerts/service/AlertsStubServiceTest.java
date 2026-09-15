package com.xe.ratealerts.service;

import static com.xe.ratealerts.utils.TestUtils.ALERT_ID;
import static com.xe.ratealerts.utils.TestUtils.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import com.xe.ratealerts.helper.RestHelper;
import com.xe.ratealerts.model.dto.Alert;
import com.xe.ratealerts.model.dto.response.AlertResponseDto;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.service.cache.CacheBaseService;
import com.xe.ratealerts.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
class AlertsStubServiceTest {
    @MockitoBean
    private CurrencyService currencyService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CacheBaseService cacheBaseService;
    @MockitoBean
    private RestHelper restHelper;
    @InjectMocks
    private AlertsStubService alertsStubService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alertsStubService = spy(new AlertsStubService(cacheBaseService, currencyService, restHelper));
    }

    @AfterEach
    void tearDown() {
        alertsStubService = null;
    }

    @Test
    void testGetAlerts_success() {
        AlertResponseDto alertResponseDto = TestUtils.createTestAlertResponseDto();
        Flux<AlertResponseDto> alertResponseDtoFlux = Flux.just(alertResponseDto);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK.value(), alertResponseDtoFlux);

        when(alertsStubService.getAlerts(anyString())).thenReturn(responseDto);

        ResponseDto result = alertsStubService.getAlerts(USER_ID);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    void testGetAlerts_failure() {
        ResponseDto responseDto = new ResponseDto(HttpStatus.NOT_FOUND.value());

        when(alertsStubService.getAlerts(anyString())).thenReturn(responseDto);

        ResponseDto result = alertsStubService.getAlerts(USER_ID);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    void testCreateAlert_success() {
        ResponseDto expectedResponse = new ResponseDto(HttpStatus.CREATED.value(), TestUtils.createTestAlert());

        when(alertsStubService.createAlert(USER_ID, TestUtils.createTestCreateAlertRequestDto())).thenReturn(expectedResponse);

        ResponseDto result = alertsStubService.createAlert(USER_ID, TestUtils.createTestCreateAlertRequestDto());

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testCreateAlert_failure() {
        ResponseDto expectedResponse = new ResponseDto(HttpStatus.BAD_REQUEST.value());

        when(alertsStubService.createAlert(USER_ID, TestUtils.createTestCreateAlertRequestDto())).thenReturn(expectedResponse);

        ResponseDto result = alertsStubService.createAlert(USER_ID, TestUtils.createTestCreateAlertRequestDto());

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testDeleteAlert_success() {
        ResponseDto expectedResponse = new ResponseDto(HttpStatus.NO_CONTENT.value());

        when(alertsStubService.deleteAlert(anyString(), anyString())).thenReturn(expectedResponse);

        ResponseDto result = alertsStubService.deleteAlert(USER_ID, ALERT_ID);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testDeleteAlert_failure() {
        ResponseDto expectedResponse = new ResponseDto(HttpStatus.NOT_FOUND.value());

        when(alertsStubService.deleteAlert(anyString(), anyString())).thenReturn(expectedResponse);

        ResponseDto result = alertsStubService.deleteAlert(USER_ID, ALERT_ID);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetAllAlerts_success() {
        List<Alert> alerts = List.of(TestUtils.createTestAlert());
        ResponseDto expectedResponse = new ResponseDto(HttpStatus.OK.value(), alerts);

        when(alertsStubService.getAllAlerts(anyString())).thenReturn(expectedResponse);

        ResponseDto result = alertsStubService.getAllAlerts(USER_ID);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetAllAlerts_failure() {
        ResponseDto expectedResponse = new ResponseDto(HttpStatus.NOT_FOUND.value());

        when(alertsStubService.getAllAlerts(anyString())).thenReturn(expectedResponse);

        ResponseDto result = alertsStubService.getAllAlerts(USER_ID);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }
}
