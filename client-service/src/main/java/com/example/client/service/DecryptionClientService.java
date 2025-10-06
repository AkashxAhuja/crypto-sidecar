package com.example.client.service;

import com.example.client.model.DecryptRequest;
import com.example.client.model.DecryptResponse;
import com.example.client.model.DecryptionProtocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DecryptionClientService {

    private static final Logger log = LoggerFactory.getLogger(DecryptionClientService.class);

    private final SidecarRestClient restClient;
    private final SidecarGrpcClient grpcClient;

    public DecryptionClientService(SidecarRestClient restClient, SidecarGrpcClient grpcClient) {
        this.restClient = restClient;
        this.grpcClient = grpcClient;
    }

    public DecryptResponse decrypt(DecryptRequest request) {
        DecryptionProtocol protocol = request.protocolOrDefault();
        log.info("Processing client request using {} protocol", protocol);
        String plainText = switch (protocol) {
            case REST -> restClient.decrypt(request);
            case GRPC -> grpcClient.decrypt(request);
        };
        return new DecryptResponse(plainText, protocol);
    }
}
