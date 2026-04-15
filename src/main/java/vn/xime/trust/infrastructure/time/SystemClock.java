package vn.xime.trust.infrastructure.time;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.out.Clock;

import java.time.Instant;

@Component
public class SystemClock implements Clock {

    @Override
    public Instant now() {
        return Instant.now();
    }
}