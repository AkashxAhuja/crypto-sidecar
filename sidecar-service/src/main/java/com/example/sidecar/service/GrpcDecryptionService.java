package com.example.sidecar.service;

import com.example.crypto.proto.CryptoDecryptionServiceGrpc;
import com.example.crypto.proto.DecryptRequest;
import com.example.crypto.proto.DecryptResponse;

import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GrpcDecryptionService extends CryptoDecryptionServiceGrpc.CryptoDecryptionServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GrpcDecryptionService.class);
    private final CryptoService cryptoService;

    public GrpcDecryptionService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public void decrypt(DecryptRequest request, StreamObserver<DecryptResponse> responseObserver) {
        log.info("Received gRPC decryption request for keyId={}", request.getKeyId());
        String plainText = cryptoService.decrypt(request.getKeyId(), request.getCipherText());
        DecryptResponse response = DecryptResponse.newBuilder()
                .setPlainText(plainText)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
