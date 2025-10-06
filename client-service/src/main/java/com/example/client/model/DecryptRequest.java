package com.example.client.model;

import jakarta.validation.constraints.NotBlank;

public record DecryptRequest(
        @NotBlank(message = "keyId is required") String keyId,
        @NotBlank(message = "cipherText is required") String cipherText,
        DecryptionProtocol protocol
) {
    public DecryptionProtocol protocolOrDefault() {
        return protocol == null ? DecryptionProtocol.REST : protocol;
    }
}
