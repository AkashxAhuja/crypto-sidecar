package com.example.sidecar.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CryptoServiceTest {

    private static final String KEY_ID = "orders";

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private SecretKeyCache secretKeyCache;

    @Autowired
    private MockKmsClient mockKmsClient;

    @BeforeEach
    void setUp() {
        mockKmsClient.resetCounter();
    }

    @Test
    void decryptShouldReturnOriginalPlaintext() {
        SecretKey key = secretKeyCache.getSecretKey(KEY_ID);
        String cipherText = TestEncryptionUtils.encrypt(key, "classified-order");
        mockKmsClient.resetCounter();

        String decrypted = cryptoService.decrypt(KEY_ID, cipherText);

        assertThat(decrypted).isEqualTo("classified-order");
    }

    @Test
    void decryptShouldReuseCachedSecretKey() {
        SecretKey key = secretKeyCache.getSecretKey(KEY_ID);
        String cipherText = TestEncryptionUtils.encrypt(key, "repeatable-secret");
        mockKmsClient.resetCounter();

        String first = cryptoService.decrypt(KEY_ID, cipherText);
        String second = cryptoService.decrypt(KEY_ID, cipherText);

        assertThat(first).isEqualTo(second).isEqualTo("repeatable-secret");
        assertThat(mockKmsClient.getLookupCount()).isEqualTo(1);
    }

    @Test
    void encryptShouldProduceDecryptableCiphertext() {
        String cipherText = cryptoService.encrypt(KEY_ID, "top-secret-order");

        String decrypted = cryptoService.decrypt(KEY_ID, cipherText);

        assertThat(decrypted).isEqualTo("top-secret-order");
    }

    @Test
    void encryptShouldReuseCachedSecretKey() {
        mockKmsClient.resetCounter();

        cryptoService.encrypt(KEY_ID, "order-1");
        cryptoService.encrypt(KEY_ID, "order-2");

        assertThat(mockKmsClient.getLookupCount()).isEqualTo(1);
    }
}
