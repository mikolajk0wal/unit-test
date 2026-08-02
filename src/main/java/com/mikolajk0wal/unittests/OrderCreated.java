package com.mikolajk0wal.unittests;

import java.util.UUID;

record OrderCreated(UUID orderId, String customerEmail) implements Event {
}
