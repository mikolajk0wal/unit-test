package com.mikolajk0wal.unittests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
class TestExchangeRateConfiguration {
    @Bean
    @Primary
    public ExchangeRateProvider testExchangeRateProvider() {
        return () -> new ExchangeRates("PLN",
                Map.of(
                        "EUR", new BigDecimal("0.25"),
                        "GBP", new BigDecimal("0.20"),
                        "CHF", new BigDecimal("0.22")
                )
        );
    }
}