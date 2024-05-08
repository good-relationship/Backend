docker compose down
docker system prune -a --force
docker volume prune -a --force
docker ps -a
docker system df
docker compose up