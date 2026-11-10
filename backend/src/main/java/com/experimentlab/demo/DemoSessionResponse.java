package com.experimentlab.demo;

import java.time.LocalDateTime;

public record DemoSessionResponse(
        boolean active,
        LocalDateTime expiresAt
) {
}
