package com.example.sidecar.controller;

import com.example.sidecar.model.DecryptRequest;
import com.example.sidecar.model.DecryptResponse;
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
public class DecryptionController {

    private static final Logger log = LoggerFactory.getLogger(DecryptionController.class);
    private final CryptoService cryptoService;

    public DecryptionController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/decrypt")
    public ResponseEntity<DecryptResponse> decrypt(@Valid @RequestBody DecryptRequest request) {
        log.info("Received REST decryption request for keyId={}", request.keyId());
        String plaintext = cryptoService.decrypt(request.keyId(), request.cipherText());
        return ResponseEntity.ok(new DecryptResponse(plaintext));
    }
}
