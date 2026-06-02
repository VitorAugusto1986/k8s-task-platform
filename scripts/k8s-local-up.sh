#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CLUSTER_NAME="task-platform"
IMAGE_NAME="devcontainer-task-platform:latest"
INGRESS_MANIFEST="https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "[ERROR] Missing required command: $1"
    exit 1
  fi
}

echo "[INFO] Checking prerequisites..."
require_cmd docker
require_cmd kubectl
require_cmd kind

if ! docker info >/dev/null 2>&1; then
  echo "[ERROR] Docker daemon is not running. Start Docker Desktop and retry."
  exit 1
fi

echo "[INFO] Ensuring Kind cluster '$CLUSTER_NAME' exists..."
if ! kind get clusters | grep -qx "$CLUSTER_NAME"; then
  kind create cluster --name "$CLUSTER_NAME" --config "$ROOT_DIR/k8s/kind-config.yaml"
else
  echo "[INFO] Cluster '$CLUSTER_NAME' already exists. Reusing it."
fi

echo "[INFO] Waiting for nodes to become Ready..."
kubectl wait --for=condition=Ready node --all --timeout=180s

echo "[INFO] Installing/updating ingress-nginx..."
kubectl apply -f "$INGRESS_MANIFEST"
kubectl wait --namespace ingress-nginx --for=condition=Ready pod \
  --selector=app.kubernetes.io/component=controller --timeout=180s

echo "[INFO] Building application image ($IMAGE_NAME)..."
(
  cd "$ROOT_DIR/.devcontainer"
  docker compose build task-platform
)

echo "[INFO] Loading image into Kind..."
kind load docker-image "$IMAGE_NAME" --name "$CLUSTER_NAME"

echo "[INFO] Applying Kubernetes manifests..."
kubectl apply -f "$ROOT_DIR/k8s/namespace.yaml"
kubectl apply -f "$ROOT_DIR/k8s/configmap.yaml"
kubectl apply -f "$ROOT_DIR/k8s/secret.yaml"
kubectl apply -f "$ROOT_DIR/k8s/postgres-pvc.yaml"
kubectl apply -f "$ROOT_DIR/k8s/postgres-deployment.yaml"
kubectl apply -f "$ROOT_DIR/k8s/postgres-service.yaml"
kubectl apply -f "$ROOT_DIR/k8s/app-deployment.yaml"
kubectl apply -f "$ROOT_DIR/k8s/app-service.yaml"
kubectl apply -f "$ROOT_DIR/k8s/ingress.yaml"

echo "[INFO] Waiting for deployments in namespace task-platform..."
kubectl wait -n task-platform --for=condition=available deployment/postgres --timeout=240s
kubectl wait -n task-platform --for=condition=available deployment/task-platform --timeout=240s

echo

echo "[INFO] Current status:"
kubectl get pods -n task-platform
kubectl get svc -n task-platform
kubectl get ingress -n task-platform

echo

echo "[OK] Local Kubernetes stack is ready."
echo "[TIP] Test without hosts file:"
echo "      curl -s -H 'Host: task-platform.local' http://127.0.0.1/health"
echo "[TIP] Or add host entry once:"
echo "      echo \"127.0.0.1 task-platform.local\" | sudo tee -a /etc/hosts"

