# Devcontainer Docker Stack

All Docker-related files for local development are located in this folder:

- `.devcontainer/Dockerfile`
- `.devcontainer/docker-compose.yml`
- `.devcontainer/.dockerignore`

## Start

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose up --build -d
```

## Check status

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose ps
```

## Logs

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose logs -f task-platform
```

## Quick API checks

```bash
curl -s http://localhost:8080/health
curl -s http://localhost:8080/tasks
```

## Stop

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose down
```

## Stop and remove DB volume

```bash
cd /Users/ctw02864/Desktop/Pip-Other_Projects/cloud-native-task-platform/.devcontainer
docker compose down -v
```

