package org.example.transform;

public interface ExternalApiClient {

    public String callExternalApi(String input) throws InterruptedException;

}