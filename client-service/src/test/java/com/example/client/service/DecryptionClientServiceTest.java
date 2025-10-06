package com.example.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.client.model.DecryptRequest;
import com.example.client.model.DecryptionProtocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DecryptionClientServiceTest {

    @Mock
    private SidecarRestClient restClient;

    @Mock
    private SidecarGrpcClient grpcClient;

    @InjectMocks
    private DecryptionClientService clientService;

    private DecryptRequest request;

    @BeforeEach
    void setUp() {
        request = new DecryptRequest("orders", "cipher", null);
    }

    @Test
    void shouldDelegateToRestClientByDefault() {
        when(restClient.decrypt(request)).thenReturn("plain");

        var response = clientService.decrypt(request);

        assertThat(response.plainText()).isEqualTo("plain");
        assertThat(response.protocol()).isEqualTo(DecryptionProtocol.REST);
        verify(restClient).decrypt(request);
    }

    @Test
    void shouldDelegateToGrpcClientWhenRequested() {
        DecryptRequest grpcRequest = new DecryptRequest("orders", "cipher", DecryptionProtocol.GRPC);
        when(grpcClient.decrypt(grpcRequest)).thenReturn("plain");

        var response = clientService.decrypt(grpcRequest);

        assertThat(response.plainText()).isEqualTo("plain");
        assertThat(response.protocol()).isEqualTo(DecryptionProtocol.GRPC);
        verify(grpcClient).decrypt(grpcRequest);
    }
}
