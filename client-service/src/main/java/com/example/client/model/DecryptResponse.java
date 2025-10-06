package com.example.client.model;

public record DecryptResponse(String plainText, DecryptionProtocol protocol) {
}
