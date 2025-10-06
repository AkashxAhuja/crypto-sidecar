package com.example.sidecar.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    private static final Logger log = LoggerFactory.getLogger(CryptoService.class);
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;

    private final SecretKeyCache secretKeyCache;

    public CryptoService(SecretKeyCache secretKeyCache) {
        this.secretKeyCache = secretKeyCache;
    }

    public String decrypt(String keyId, String cipherTextBase64) {
        SecretKey secretKey = secretKeyCache.getSecretKey(keyId);
        byte[] cipherMessage = Base64.getDecoder().decode(cipherTextBase64);
        if (cipherMessage.length < IV_LENGTH) {
            throw new IllegalArgumentException("Cipher text is too short to contain an IV");
        }

        ByteBuffer buffer = ByteBuffer.wrap(cipherMessage);
        byte[] iv = new byte[IV_LENGTH];
        buffer.get(iv);
        byte[] cipherBytes = new byte[buffer.remaining()];
        buffer.get(cipherBytes);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            String plaintext = new String(plainBytes, StandardCharsets.UTF_8);
            log.info("Successfully decrypted payload for keyId={}", keyId);
            return plaintext;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to decrypt payload", e);
        }
    }
}
