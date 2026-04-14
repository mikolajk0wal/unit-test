package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

record PriceBreakdown(
        BigDecimal total,
        Map<UUID, BigDecimal> pricingLines
) {}