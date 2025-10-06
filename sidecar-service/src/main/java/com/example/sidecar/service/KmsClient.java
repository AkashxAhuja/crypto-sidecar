package com.example.sidecar.service;

public interface KmsClient {

    byte[] fetchSecretKey(String keyId);
}
