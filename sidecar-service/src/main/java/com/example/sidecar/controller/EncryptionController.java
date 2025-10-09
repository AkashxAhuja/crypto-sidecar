package com.example.sidecar.controller;

import com.example.sidecar.model.EncryptRequest;
import com.example.sidecar.model.EncryptResponse;
import com.example.sidecar.service.CryptoService;

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
public class EncryptionController {

    private static final Logger log = LoggerFactory.getLogger(EncryptionController.class);
    private final CryptoService cryptoService;

    public EncryptionController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/encrypt")
    public ResponseEntity<EncryptResponse> encrypt(@Valid @RequestBody EncryptRequest request) {
        log.info("Received REST encryption request for keyId={}", request.keyId());
        String cipherText = cryptoService.encrypt(request.keyId(), request.plainText());
        return ResponseEntity.ok(new EncryptResponse(cipherText));
    }
}
