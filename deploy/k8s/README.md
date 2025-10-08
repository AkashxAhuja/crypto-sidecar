# Kubernetes deployment (Minikube)

These manifests deploy the client and sidecar containers inside the same pod so they share the loopback network. The pod exposes the client on port 8080, the sidecar REST interface on port 8081 and the sidecar gRPC endpoint on port 9090.

## Prerequisites

* [Minikube](https://minikube.sigs.k8s.io/docs/start/)
* Docker installed locally (Minikube will reuse the Docker daemon)
* This repository cloned locally

## Build the container images inside Minikube

Minikube keeps its own Docker daemon. Execute the following commands **from the project root** so the images become available to the cluster:

```bash
minikube start
# Point your shell at the Minikube Docker daemon
eval "$(minikube -p minikube docker-env)"
# Build both images
mvn -pl sidecar-service -am package -DskipTests
mvn -pl client-service -am package -DskipTests
docker build -t crypto-sidecar/sidecar-service:latest -f sidecar-service/Dockerfile .
docker build -t crypto-sidecar/client-service:latest -f client-service/Dockerfile .
```

> If you prefer using Docker BuildKit or a different tag, remember to update the manifests accordingly.

## Deploy the pod and service

```bash
kubectl apply -f deploy/k8s/crypto-sidecar-pod.yaml
kubectl apply -f deploy/k8s/crypto-sidecar-service.yaml
```

Verify that the pod is running:

```bash
kubectl get pods -l app=crypto-sidecar
```

## Accessing the services

To hit the client REST API from your workstation you can either use `minikube service` or establish a port-forward:

```bash
# Using NodePort forwarding provided by Minikube
minikube service crypto-sidecar-service --url
# Or port-forward the pod
kubectl port-forward pod/crypto-sidecar-pod 8080:8080
```

Once forwarded, call the client API as usual:

```bash
curl -X POST http://localhost:8080/api/v1/decrypt \
  -H 'Content-Type: application/json' \
  -d '{
        "keyId": "orders",
        "cipherText": "<base64 payload>",
        "protocol": "REST"
      }'
```

Because both containers live in the same pod, the client can continue to reach the sidecar using `http://localhost:8081` for REST and `static://localhost:9090` for gRPC.

To remove the resources:

```bash
kubectl delete -f deploy/k8s/crypto-sidecar-service.yaml
kubectl delete -f deploy/k8s/crypto-sidecar-pod.yaml
```
