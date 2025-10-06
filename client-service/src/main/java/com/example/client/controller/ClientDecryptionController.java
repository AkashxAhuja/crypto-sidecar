package com.example.client.controller;

import com.example.client.model.DecryptRequest;
import com.example.client.model.DecryptResponse;
import com.example.client.service.DecryptionClientService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
public class ClientDecryptionController {

    private static final Logger log = LoggerFactory.getLogger(ClientDecryptionController.class);
    private final DecryptionClientService decryptionClientService;

    public ClientDecryptionController(DecryptionClientService decryptionClientService) {
        this.decryptionClientService = decryptionClientService;
    }

    @PostMapping("/decrypt")
    public ResponseEntity<DecryptResponse> decrypt(@Valid @RequestBody DecryptRequest request) {
        log.info("Client service received decrypt request for keyId={} via {}", request.keyId(), request.protocolOrDefault());
        DecryptResponse response = decryptionClientService.decrypt(request);
        return ResponseEntity.ok(response);
    }
}
