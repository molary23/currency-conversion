package com.xe.ratealerts.config.cache;

import static com.xe.ratealerts.utils.Constants.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xe.ratealerts.config.AppConfig;
import com.xe.ratealerts.exceptions.CustomCacheErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class RedisCacheManagerConfig implements CachingConfigurer {

    private final RedisConfig redisConfig;
    private final AppConfig appConfig;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("Configuring Redis connection factory...");
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisConfig.getHost());
        redisStandaloneConfiguration.setPort(redisConfig.getPort());
        redisStandaloneConfiguration.setPassword(redisConfig.getPassword());

        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();

        LettuceClientConfiguration lettuceClientConfiguration = lettuceClientConfigurationBuilder.build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean(REDIS_CACHE_MANAGER)
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory factory, RedisCacheConfiguration cacheConfig,
                                               RedisCacheManagerBuilderCustomizer customizer) {
        log.info("Configuring RedisCacheManagerConfig with custom cache configuration...");
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
            .builder(factory)
            .cacheDefaults(cacheConfig);
        customizer.customize(builder);
        return builder.build();
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        log.info("Configuring unified RedisCacheConfiguration with custom serialization...");
        ObjectMapper redisMapper = new ObjectMapper();
        redisMapper.registerModule(new JavaTimeModule());
        redisMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        redisMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        redisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer(redisMapper)));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheConfiguration cacheConfig) {
        log.info("Customizing RedisCacheManagerConfig with TTL={} days for cache '{}'", ALERT_REDIS_TTL_DAYS, ALERT_CACHE);
        log.info("Customizing RedisCacheManagerConfig with TTL={} days for cache '{}'", CURRENCY_REDIS_TTL_DAYS, CURRENCY_CACHE);

        return builder -> builder
            .withCacheConfiguration(ALERT_CACHE,
                cacheConfig.entryTtl(Duration.ofDays(ALERT_REDIS_TTL_DAYS))
            )
            .withCacheConfiguration(CURRENCY_CACHE,
                cacheConfig.entryTtl(Duration.ofDays(CURRENCY_REDIS_TTL_DAYS))

            );
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
}
