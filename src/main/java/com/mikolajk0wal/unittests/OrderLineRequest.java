package com.mikolajk0wal.unittests;

import java.util.UUID;

record OrderLineRequest(UUID productId, int quantity) {}
