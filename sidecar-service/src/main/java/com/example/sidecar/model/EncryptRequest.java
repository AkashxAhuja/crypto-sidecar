package com.example.sidecar.model;

import jakarta.validation.constraints.NotBlank;

public record EncryptRequest(
        @NotBlank(message = "keyId is required") String keyId,
        @NotBlank(message = "plainText is required") String plainText
) {
}
