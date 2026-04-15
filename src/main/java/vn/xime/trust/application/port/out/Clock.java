package vn.xime.trust.application.port.out;

import java.time.Instant;

public interface Clock {
    Instant now();
}