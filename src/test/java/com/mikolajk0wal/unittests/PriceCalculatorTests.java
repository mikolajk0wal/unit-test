package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;

import static com.mikolajk0wal.unittests.Fixtures.eurToPln;
import static com.mikolajk0wal.unittests.Fixtures.productPricedAt;
import static com.mikolajk0wal.unittests.PriceBreakdownAssert.assertThat;
import static com.mikolajk0wal.unittests.PricingContextBuilder.aPricingContext;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PriceCalculatorTests {
	private final PriceCalculator calculator = new PriceCalculator();

	@Test
	void shouldCalculatePriceBreakdownForMixedCurrencyProducts() {
		// Given
		Product socks = productPricedAt(new Money("10.00", "EUR"));
		Product tShirt = productPricedAt(new Money("90.00", "PLN"));

		// And
		PricingContext context = aPricingContext()
				.withItem(socks, 3)
				.withItem(tShirt, 2)
				.usingRates(eurToPln("4.00"))
				.in("PLN")
				.build();

		// When
		PriceBreakdown result = calculator.calculate(context);

		// Then
		assertThat(result)
				.hasTotal("300.00", "PLN")
				.hasLinesCount(2)
				.hasLine(socks, "120.00", "PLN")
				.hasLine(tShirt, "180.00", "PLN");
	}

	@Test
	void shouldCalculatePriceForSingleProductCart() {
		// Given
		Product tShirt = productPricedAt(new Money("90.00", "PLN"));
		PricingContext context = aPricingContext()
                .withItem(tShirt, 1)
                .in("PLN")
                .build();

		// When
		PriceBreakdown result = calculator.calculate(context);

		// Then
		assertThat(result)
                .hasTotal("90.00", "PLN")
                .hasLine(tShirt, "90.00", "PLN");
	}

	@Test
	void shouldResultInZeroPriceWhenNoProductsAreProvided() {
		// Given
		PricingContext context = aPricingContext().build();

		// When
		PriceBreakdown result = calculator.calculate(context);

		// Then
		assertThat(result).hasTotal("0.00", "PLN");
	}

	@Test
	void shouldResultInZeroPriceWhenProductQuantityIsZero() {
		// Given
		Product tShirt = productPricedAt(new Money("90.00", "PLN"));
		PricingContext context = aPricingContext().withItem(tShirt, 0).in("PLN").build();

		// When
		PriceBreakdown result = calculator.calculate(context);

		// Then
		assertThat(result)
                .hasTotal("0.00", "PLN")
                .hasLine(tShirt, "0.00", "PLN");
	}

	@Test
	void shouldThrowExceptionWhenProductQuantityIsNegative() {
		// Given
		Product tShirt = productPricedAt(new Money("90.00", "PLN"));
		PricingContext context = aPricingContext().withItem(tShirt, -1).in("PLN").build();

		// When & Then
		assertThatThrownBy(() -> calculator.calculate(context)).isInstanceOf(IllegalArgumentException.class);
	}
}
