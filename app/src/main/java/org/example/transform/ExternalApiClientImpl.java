package org.example.transform;

public class ExternalApiClientImpl implements ExternalApiClient {

    @Override
    public String callExternalApi(String input) throws InterruptedException {
        // Simula latenza API
        Thread.sleep(100);
        return ",additional data";
    }
}