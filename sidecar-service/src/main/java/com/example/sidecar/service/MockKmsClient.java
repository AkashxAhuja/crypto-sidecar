package com.example.sidecar.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockKmsClient implements KmsClient {

    private static final Logger log = LoggerFactory.getLogger(MockKmsClient.class);

    private final Map<String, String> secrets = new ConcurrentHashMap<>();
    private final AtomicInteger lookupCount = new AtomicInteger();

    public MockKmsClient() {
        secrets.put("default", "LocalSecretKey-Default");
        secrets.put("orders", "LocalSecretKey-Orders");
    }

    @Override
    public byte[] fetchSecretKey(String keyId) {
        lookupCount.incrementAndGet();
        log.info("MockKmsClient retrieving key for keyId={} (lookup #{})", keyId, lookupCount.get());
        String seed = secrets.getOrDefault(keyId, secrets.get("default"));
        return deriveKey(seed);
    }

    public int getLookupCount() {
        return lookupCount.get();
    }

    public void resetCounter() {
        lookupCount.set(0);
    }

    private byte[] deriveKey(String seed) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(seed.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm missing", e);
        }
    }
}
