package org.example.transform;


public interface RateLimiter {
    public void acquire() throws InterruptedException;
}
