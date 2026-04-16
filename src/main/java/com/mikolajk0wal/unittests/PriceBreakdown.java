package com.mikolajk0wal.unittests;

import java.util.Map;
import java.util.UUID;

record PriceBreakdown(Money total, Map<UUID, Money> pricingLines) {
}
