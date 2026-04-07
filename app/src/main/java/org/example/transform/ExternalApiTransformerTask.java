package org.example.transform;

import java.util.concurrent.BlockingQueue;

public class ExternalApiTransformerTask implements Runnable {


    private final BlockingQueue<String> loadQueue;
    private final BlockingQueue<String> writeQueue;
    private final BlockingQueue<String> deadLetterQueue;
    private final ExternalApiClient apiClient;
    private final RateLimiter rateLimiter;
    private final String POISON_PILL;

    public ExternalApiTransformerTask(BlockingQueue<String> loadQueue,
                       BlockingQueue<String> writeQueue,
                       BlockingQueue<String> deadLetterQueue,
                       ExternalApiClient apiClient,
                       RateLimiter rateLimiter,
                       String poisonPill) {
        this.loadQueue = loadQueue;
        this.writeQueue = writeQueue;
        this.deadLetterQueue = deadLetterQueue;
        this.apiClient = apiClient;
        this.rateLimiter = rateLimiter;
        this.POISON_PILL = poisonPill;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String line = loadQueue.take();

                if (line.equals(POISON_PILL)) {
                    loadQueue.put(POISON_PILL);
                    break;
                }
                try {
                rateLimiter.acquire();
                String response = apiClient.callExternalApi(line);
                String enriched = line + response + ",enriched=true";
                writeQueue.put(enriched);                

                } catch (Exception e) {
                    deadLetterQueue.put(line);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}