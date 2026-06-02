# Kubernetes Manifests

This folder contains all Kubernetes manifests needed to run the project locally with **Kind** + **NGINX Ingress**.

## Files

- `namespace.yaml`
- `configmap.yaml`
- `secret.yaml`
- `postgres-pvc.yaml`
- `postgres-deployment.yaml`
- `postgres-service.yaml`
- `app-deployment.yaml`
- `app-service.yaml`
- `ingress.yaml`
- `kind-config.yaml`

## Prerequisites

- Docker running
- `kubectl` installed
- `kind` installed

Install `kind` (macOS/Homebrew):

```bash
brew install kind
```

Quick checks:

```bash
docker --version
kubectl version --client --output=yaml | head -20
kind version
```

## Fast path (recommended)

Use the automation script from project root:

```bash
./scripts/k8s-local-up.sh
```

The script does all steps below:

- creates/reuses Kind cluster `task-platform` using `k8s/kind-config.yaml`
- installs `ingress-nginx`
- builds app image from `.devcontainer/docker-compose.yml`
- loads image into Kind
- applies all manifests from `k8s/`
- waits for `postgres` and `task-platform` deployments

## Manual step-by-step

### 1) Create Kind cluster with host ports 80/443

`k8s/kind-config.yaml` already includes:

- node label `ingress-ready=true`
- host port mappings `80 -> 80` and `443 -> 443`

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform
kind create cluster --name task-platform --config k8s/kind-config.yaml
kubectl config current-context
kubectl get nodes
```

### 2) Install ingress-nginx (Kind provider)

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
kubectl wait --namespace ingress-nginx --for=condition=Ready pod --selector=app.kubernetes.io/component=controller --timeout=180s
```

### 3) Build app image

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose build task-platform
```

### 4) Load image into Kind

```bash
kind load docker-image devcontainer-task-platform:latest --name task-platform
```

### 5) Deploy manifests

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
kubectl apply -f k8s/ingress.yaml
```

### 6) Verify resources

```bash
kubectl get pods -n task-platform
kubectl get svc -n task-platform
kubectl get ingress -n task-platform
```

## Access the app

### Option A: Port-forward (no host file change)

```bash
kubectl port-forward -n task-platform svc/task-platform 8080:80
```

```bash
curl -s http://localhost:8080/health
```

### Option B: Ingress host `task-platform.local`

Add host entry once:

```bash
echo "127.0.0.1 task-platform.local" | sudo tee -a /etc/hosts
```

Then test:

```bash
curl -s http://task-platform.local/health
```

Quick test without editing `/etc/hosts`:

```bash
curl -s -H 'Host: task-platform.local' http://127.0.0.1/health
```

## Troubleshooting

### App restarts at startup

The app may restart once while waiting for PostgreSQL readiness.

```bash
kubectl get pods -n task-platform -w
kubectl logs -n task-platform deployment/task-platform --tail=100
kubectl logs -n task-platform deployment/postgres --tail=100
```

### Port 80/443 already in use

If cluster creation fails, free ports 80/443 on your host (nginx/httpd/Traefik/etc.).

### New app build is not picked up

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose build task-platform
kind load docker-image devcontainer-task-platform:latest --name task-platform
kubectl rollout restart deployment/task-platform -n task-platform
```

## Cleanup

Delete only app resources:

```bash
kubectl delete namespace task-platform
```

Delete the full local cluster:

```bash
kind delete cluster --name task-platform
```

