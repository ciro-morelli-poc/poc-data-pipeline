package org.example.transform;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

public class SlidingWindowRateLimiter implements RateLimiter {

    private final long maxRequests;
    private final long windowSizeNs;

    private final Deque<Long> timestamps = new ArrayDeque<>();

    public SlidingWindowRateLimiter(long maxRequestsPerSecond) {
        if (maxRequestsPerSecond <= 0) {
            throw new IllegalArgumentException("maxRequestsPerSecond must be > 0");
        }

        this.maxRequests = maxRequestsPerSecond;
        this.windowSizeNs = 1_000_000_000L;
    }

    @Override
    public synchronized void acquire() throws InterruptedException {
        long now = System.nanoTime();

        // rimuovi richieste fuori finestra
        while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= windowSizeNs) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < maxRequests) {
            timestamps.addLast(now);
            return;
        }

        long oldest = timestamps.peekFirst();
        long waitNs = (oldest + windowSizeNs) - now;

        if (waitNs > 0) {
            TimeUnit.NANOSECONDS.sleep(waitNs);
        }

        long newNow = System.nanoTime();
        timestamps.pollFirst();
        timestamps.addLast(newNow);
    }
}