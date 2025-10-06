package com.example.client.service;

import com.example.client.model.DecryptRequest;
import com.example.crypto.proto.CryptoDecryptionServiceGrpc;
import com.example.crypto.proto.DecryptResponse;

import net.devh.boot.grpc.client.inject.GrpcClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SidecarGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(SidecarGrpcClient.class);

    @GrpcClient("cryptoSidecar")
    private CryptoDecryptionServiceGrpc.CryptoDecryptionServiceBlockingStub blockingStub;

    public String decrypt(DecryptRequest request) {
        log.info("Invoking sidecar gRPC endpoint for keyId={}", request.keyId());
        com.example.crypto.proto.DecryptRequest grpcRequest = com.example.crypto.proto.DecryptRequest.newBuilder()
                .setKeyId(request.keyId())
                .setCipherText(request.cipherText())
                .build();
        DecryptResponse response = blockingStub.decrypt(grpcRequest);
        return response.getPlainText();
    }
}
