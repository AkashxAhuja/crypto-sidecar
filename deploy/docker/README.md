# Local Docker deployment

Use the compose file and environment definitions in this folder to build and run the services as Docker containers on the same network. The sidecar runs on ports 8081/9090 and the client runs on port 8080.

## Build and run

```bash
cd deploy/docker
docker compose up --build
```

Docker Compose builds both images, starts the sidecar container first, and injects the environment variables from the provided `.env` files. Once the containers are running, the client is reachable at `http://localhost:8080`.

Stop the stack with `Ctrl+C` or by running `docker compose down` in the same directory.
