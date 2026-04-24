package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PriceCalculatorTests {
    private final PriceCalculator calculator = new PriceCalculator();

    @Test
    void shouldCalculatePriceBreakdownForMixedCurrencyProducts() {
        ExchangeRates rates = new ExchangeRates("EUR", Map.of("PLN", new BigDecimal("4.00")));
        Product p1 = new Product("Product 1", new Money("10.00", "EUR"));
        Product p2 = new Product("Product 2", new Money("20.00", "PLN"));
        PricingContext context = new PricingContext(Map.of(p1, 2, p2, 3), rates, "PLN");

        PriceBreakdown result = calculator.calculate(context);

        assertThat(result.total()).isEqualTo(new Money("140.00", "PLN"));
        assertThat(result.pricingLines()).hasSize(2).containsEntry(p1.id(), new Money("80.00", "PLN"))
                .containsEntry(p2.id(), new Money("60.00", "PLN"));
    }

    @Test
    void shouldResultInZeroPriceWhenNoProductsAreProvided() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of());
        PricingContext context = new PricingContext(Map.of(), rates, "PLN");

        PriceBreakdown result = calculator.calculate(context);

        assertThat(result.total()).isEqualTo(Money.zero("PLN"));
        assertThat(result.pricingLines()).isEmpty();
    }

    @Test
    void shouldResultInZeroPriceWhenProductQuantityIsZero() {
        ExchangeRates rates = new ExchangeRates("USD", Map.of());
        Product p1 = new Product("Product 1", new Money("100.00", "USD"));
        PricingContext context = new PricingContext(Map.of(p1, 0), rates, "USD");

        PriceBreakdown result = calculator.calculate(context);

        assertThat(result.total()).isEqualTo(Money.zero("USD"));
        assertThat(result.pricingLines()).hasSize(1).containsEntry(p1.id(), Money.zero("USD"));
    }
}
