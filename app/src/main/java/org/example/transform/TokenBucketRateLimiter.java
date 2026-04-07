package org.example.transform;

import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiter implements RateLimiter {

    private final long capacity;          // massimo numero di token (burst)
    private final long refillTokens;      // token aggiunti per intervallo
    private final long refillIntervalNs;  // intervallo di refill

    private long availableTokens;
    private long lastRefillTime;

    /**
     * @param capacity massimo numero di richieste burst
     * @param refillRate numero di token aggiunti al secondo
     */
    public TokenBucketRateLimiter(long capacity, long refillRate) {
        if (capacity <= 0 || refillRate <= 0) {
            throw new IllegalArgumentException("capacity and refillRate must be > 0");
        }

        this.capacity = capacity;
        this.refillTokens = refillRate;
        this.refillIntervalNs = 1_000_000_000L;

        this.availableTokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    @Override
    public synchronized void acquire() throws InterruptedException {
        refill();

        while (availableTokens == 0) {
            long now = System.nanoTime();
            long waitNs = (lastRefillTime + refillIntervalNs) - now;

            if (waitNs > 0) {
                TimeUnit.NANOSECONDS.sleep(waitNs);
            }

            refill();
        }

        availableTokens--;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillTime;

        if (elapsed <= 0) return;

        long tokensToAdd = (elapsed * refillTokens) / refillIntervalNs;

        if (tokensToAdd > 0) {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}