package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Id;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;

public final class IdFactory {

    private static final int LENGTH = 20;
    private static final int TIMESTAMP_LENGTH = 4;
    private static final int RANDOM_LENGTH = 16;

    private static final long KSUID_EPOCH = 1400000000L;

    private static final SecureRandom RANDOM = new SecureRandom();

    private IdFactory() {}

    public static Id generate() {
        byte[] bytes = new byte[LENGTH];

        // timestamp
        long now = Instant.now().getEpochSecond() - KSUID_EPOCH;
        ByteBuffer.wrap(bytes, 0, TIMESTAMP_LENGTH).putInt((int) now);

        // random
        byte[] random = new byte[RANDOM_LENGTH];
        RANDOM.nextBytes(random);
        System.arraycopy(random, 0, bytes, TIMESTAMP_LENGTH, RANDOM_LENGTH);

        return new Id(bytes);
    }
}