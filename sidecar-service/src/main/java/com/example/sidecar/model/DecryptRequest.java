package com.example.sidecar.model;

import jakarta.validation.constraints.NotBlank;

public record DecryptRequest(
        @NotBlank(message = "keyId is required") String keyId,
        @NotBlank(message = "cipherText is required") String cipherText
) {
}
