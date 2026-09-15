package com.xe.ratealerts.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import com.xe.ratealerts.helper.RestHelper;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.service.cache.CacheBaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest
class CurrencyServiceTest {
    @InjectMocks
    private CurrencyService currencyService;
    @MockitoBean
    private RestHelper restHelper;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CacheBaseService cacheBaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService = spy(new CurrencyService(cacheBaseService, restHelper));
    }

    @AfterEach
    void tearDown() {
        currencyService = null;
    }

    @Test
    void testGetAllCurrencies_success() {
        List<String> expectedResponse = List.of("USD", "EUR", "GBP");

        when(currencyService.getAllCurrencies()).thenReturn(expectedResponse);

        List<String> allCurrencies = currencyService.getAllCurrencies();

        assertNotNull(allCurrencies);
        assertEquals(expectedResponse, allCurrencies);
    }

    @Test
    void testGetAllCurrencies_failure() {
        List<String> expectedResponse = List.of();
        when(currencyService.getAllCurrencies()).thenReturn(expectedResponse);

        List<String> allCurrencies = currencyService.getAllCurrencies();

        assertNotNull(allCurrencies);
        assertIterableEquals(expectedResponse, allCurrencies);
    }

    @Test
    void testRefreshCurrenciesCache_success() {
        ResponseDto responseDto = new ResponseDto(204);
        when(currencyService.refreshCurrenciesCache()).thenReturn(responseDto);

        ResponseDto result = currencyService.refreshCurrenciesCache();

        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    void testRefreshCurrenciesCache_failure() {
        ResponseDto responseDto = new ResponseDto(500);
        when(currencyService.refreshCurrenciesCache()).thenReturn(responseDto);

        ResponseDto result = currencyService.refreshCurrenciesCache();

        assertNotNull(result);
        assertEquals(responseDto, result);
    }
}
