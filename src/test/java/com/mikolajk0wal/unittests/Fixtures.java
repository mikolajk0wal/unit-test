package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.util.Map;

class Fixtures {
	static ExchangeRates eurToPln(String rate) {
		return new ExchangeRates("EUR", Map.of("PLN", new BigDecimal(rate)));
	}

	static Product productPricedAt(Money price) {
		return new Product("Product", price);
	}
}
