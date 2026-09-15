package com.xe.ratealerts.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Constants {
    public static final String RATE_CLIENT = "rateClient";
    public static final String REDIS_CACHE_MANAGER = "redisCacheManager";
    public static final String ALERT_CACHE = "alertCache";
    public static final int ALERT_REDIS_TTL_DAYS = 1; // 1 day
    public static final String ABOVE = "above";
    public static final String BELOW = "below";
    public static final String CURRENCY_CACHE = "currencyCache";
    public static final int CURRENCY_REDIS_TTL_DAYS = 30; // 30 Days
    public static final String CURRENCY_CACHE_KEY = "currencies";
    public static final String ZONE_ID = "America/Toronto";


    private Constants() {
    }

    public static Executor getVirtualThread() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
