package org.example.transform;

import java.util.concurrent.TimeUnit;

public class LeakyBucketRateLimiter implements RateLimiter {

    private final long capacity;          // dimensione massima del bucket
    private final long leakRate;          // richieste per secondo
    private final long leakIntervalNs;    // intervallo tra due "leak"

    private long waterLevel = 0;          // quante richieste sono in coda
    private long lastLeakTime;

    public LeakyBucketRateLimiter(long capacity, long leakRate) {
        if (capacity <= 0 || leakRate <= 0) {
            throw new IllegalArgumentException("capacity and leakRate must be > 0");
        }

        this.capacity = capacity;
        this.leakRate = leakRate;
        this.leakIntervalNs = 1_000_000_000L / leakRate;
        this.lastLeakTime = System.nanoTime();
    }

    @Override
    public synchronized void acquire() throws InterruptedException {
        leak();

        while (waterLevel >= capacity) {
            long now = System.nanoTime();
            long waitNs = (lastLeakTime + leakIntervalNs) - now;

            if (waitNs > 0) {
                TimeUnit.NANOSECONDS.sleep(waitNs);
            }

            leak();
        }

        waterLevel++;
    }

    private void leak() {
        long now = System.nanoTime();
        long elapsed = now - lastLeakTime;

        if (elapsed <= 0) return;

        long leaks = elapsed / leakIntervalNs;

        if (leaks > 0) {
            waterLevel = Math.max(0, waterLevel - leaks);
            lastLeakTime = now;
        }
    }
}