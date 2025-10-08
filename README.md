# Crypto Sidecar Microservice

This repository contains a pair of Spring Boot microservices that demonstrate a client/sidecar arrangement for cryptographic decryption inside a Kubernetes pod.

* **client-service** – exposes a REST API for application code. It delegates decryption work to the sidecar over REST (default) or gRPC.
* **sidecar-service** – performs decryption using a mocked Key Management Service (KMS) that supports key caching. Both REST and gRPC interfaces are available and every call is logged for traceability.
* **crypto-proto** – shared module that publishes the gRPC contract used by both services.

## Features

* AES/GCM decryption with deterministic mock KMS keys.
* Local in-memory caching of decrypted secret keys inside the sidecar. The cache avoids redundant key retrievals and is covered by unit tests.
* Dual interface support: REST and gRPC endpoints are provided by the sidecar and consumed by the client.
* Extensive structured logging in both services to capture cross-container calls.
* Dockerfiles for both services designed to run the microservices as individual containers within the same pod.

## Project layout

```
.
├── client-service
│   ├── Dockerfile
│   └── src
├── crypto-proto
│   └── src
├── sidecar-service
│   ├── Dockerfile
│   └── src
└── pom.xml
```

## Building the project

Use Maven to build all modules:

```bash
mvn clean package
```

To build a single service:

```bash
mvn -pl sidecar-service -am clean package
mvn -pl client-service -am clean package
```

## Running locally

Start the sidecar first so the client can connect to it.

```bash
cd sidecar-service
mvn spring-boot:run
```

In a separate shell:

```bash
cd client-service
mvn spring-boot:run
```

The client listens on `http://localhost:8080` while the sidecar exposes REST on `http://localhost:8081` and gRPC on port `9090`.

### Sample REST decryption flow

```bash
# encrypt a sample payload using the test helper (optional)
# then call the client REST API
curl -X POST http://localhost:8080/api/v1/decrypt \
  -H 'Content-Type: application/json' \
  -d '{
        "keyId": "orders",
        "cipherText": "<base64 payload>",
        "protocol": "REST"
      }'
```

To send the same request over gRPC, change `"protocol": "GRPC"`. The client forwards the call to the matching sidecar endpoint and returns the decrypted plaintext.

Logs in both containers will show the call path and whether cached keys were used.

## Container images

Build the sidecar image:

```bash
docker build -t crypto-sidecar/sidecar-service:latest -f sidecar-service/Dockerfile .
```

Build the client image:

```bash
docker build -t crypto-sidecar/client-service:latest -f client-service/Dockerfile .
```

When deployed to Kubernetes, place both containers in the same pod and expose ports `8080` (client), `8081` (sidecar REST) and `9090` (sidecar gRPC).

## Running with Docker Compose

The `deploy/docker` folder contains a compose file and environment definitions that build the images and run both services as coordinated containers on a shared Docker network:

```bash
cd deploy/docker
docker compose up --build
```

The sidecar stays reachable on `localhost:8081`/`9090` and the client on `localhost:8080`.

## Kubernetes (Minikube) deployment

Manifests under `deploy/k8s` provision a pod that hosts both containers side-by-side so they can communicate over the pod network. To deploy to Minikube:

```bash
minikube start
eval "$(minikube -p minikube docker-env)"
docker build -t crypto-sidecar/sidecar-service:latest -f sidecar-service/Dockerfile .
docker build -t crypto-sidecar/client-service:latest -f client-service/Dockerfile .
kubectl apply -f deploy/k8s/crypto-sidecar-pod.yaml
kubectl apply -f deploy/k8s/crypto-sidecar-service.yaml
```

Use `minikube service crypto-sidecar-service --url` to obtain reachable URLs for the exposed ports. Detailed step-by-step instructions live in `deploy/k8s/README.md`.

## Testing

Run the full test suite:

```bash
mvn test
```

The tests cover AES/GCM decryption with caching guarantees as well as protocol selection inside the client service.
