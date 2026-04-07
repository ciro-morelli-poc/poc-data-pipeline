package org.example.transform;

import java.util.concurrent.TimeUnit;

public class FixedWindowRateLimiter implements RateLimiter {

    private final long maxRequests;
    private final long windowSizeNs;

    private long windowStart;
    private long requestCount;

    public FixedWindowRateLimiter(long maxRequestsPerSecond) {
        if (maxRequestsPerSecond <= 0) {
            throw new IllegalArgumentException("maxRequestsPerSecond must be > 0");
        }

        this.maxRequests = maxRequestsPerSecond;
        this.windowSizeNs = 1_000_000_000L;
        this.windowStart = System.nanoTime();
        this.requestCount = 0;
    }

    @Override
    public synchronized void acquire() throws InterruptedException {
        long now = System.nanoTime();

        if (now - windowStart >= windowSizeNs) {
            windowStart = now;
            requestCount = 0;
        }

        if (requestCount < maxRequests) {
            requestCount++;
            return;
        }

        long waitNs = (windowStart + windowSizeNs) - now;
        if (waitNs > 0) {
            TimeUnit.NANOSECONDS.sleep(waitNs);
        }

        windowStart = System.nanoTime();
        requestCount = 1;
    }
}