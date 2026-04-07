package org.example.transform;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {

    private final long intervalNanos;      // tempo minimo tra due chiamate
    private final AtomicLong nextAllowed;  // timestamp della prossima chiamata consentita

    /**
     * @param callsPerSecond numero massimo di chiamate al secondo
     */
    public RateLimiter(long callsPerSecond) {
        if (callsPerSecond <= 0) {
            throw new IllegalArgumentException("callsPerSecond must be > 0");
        }
        this.intervalNanos = 1_000_000_000L / callsPerSecond;
        this.nextAllowed = new AtomicLong(System.nanoTime());
    }

    /**
     * Blocca il thread finché non è passato abbastanza tempo
     * per rispettare il rate limit.
     */
    public void acquire() throws InterruptedException {
        while (true) {
            long now = System.nanoTime();
            long allowed = nextAllowed.get();

            // Se siamo in ritardo, possiamo partire subito
            long startTime = Math.max(now, allowed);

            // Calcola il prossimo slot disponibile
            long newAllowed = startTime + intervalNanos;

            // CAS: solo un thread vince e aggiorna il timestamp
            if (nextAllowed.compareAndSet(allowed, newAllowed)) {

                long waitNanos = startTime - now;
                if (waitNanos > 0) {
                    TimeUnit.NANOSECONDS.sleep(waitNanos);
                }

                return;
            }
        }
    }
}