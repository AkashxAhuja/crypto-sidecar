package com.example.client.service;

import com.example.client.model.DecryptRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SidecarRestClient {

    private static final Logger log = LoggerFactory.getLogger(SidecarRestClient.class);

    private final WebClient webClient;

    public SidecarRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String decrypt(DecryptRequest request) {
        log.info("Invoking sidecar REST endpoint for keyId={}", request.keyId());
        return webClient.post()
                .uri("/api/v1/decrypt")
                .bodyValue(new RestDecryptRequest(request.keyId(), request.cipherText()))
                .retrieve()
                .bodyToMono(RestDecryptResponse.class)
                .map(RestDecryptResponse::plainText)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Sidecar REST response did not contain a body"));
    }

    private record RestDecryptRequest(String keyId, String cipherText) {
    }

    private record RestDecryptResponse(String plainText) {
    }
}
