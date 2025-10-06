package com.example.sidecar.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecretKeyCache {

    private static final Logger log = LoggerFactory.getLogger(SecretKeyCache.class);

    private final Map<String, SecretKey> cache = new ConcurrentHashMap<>();
    private final KmsClient kmsClient;

    public SecretKeyCache(KmsClient kmsClient) {
        this.kmsClient = kmsClient;
    }

    public SecretKey getSecretKey(String keyId) {
        return cache.computeIfAbsent(keyId, id -> {
            log.info("Cache miss for keyId={}, retrieving from KMS", id);
            byte[] secret = kmsClient.fetchSecretKey(id);
            return new SecretKeySpec(secret, 0, secret.length, "AES");
        });
    }
}
