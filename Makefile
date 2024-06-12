COMPOSE_DOWN = docker compose down
SYSTEM_PRUNE = docker system prune -a --force
VOLUME_PRUNE = docker volume prune -a --force
CHECK_PS = docker ps -a
CHECK_DF = docker system df
COMPOSE_UP = docker compose up
up : 
	@echo "COMPOSE UP!!"
	$(COMPOSE_UP)

clean:
	@echo "Stopping containers and cleaning up..."
	$(COMPOSE_DOWN)
	$(SYSTEM_PRUNE)
	$(VOLUME_PRUNE)
	@echo "Done!"
	@echo "Checking the status of the containers..."
	$(CHECK_PS)
	@echo "Checking the disk usage..."
	$(CHECK_DF)
spring :
	@echo "Spring Boot Application Start!!"
	sh spring-restart.sh

# 기본 명령
all: up

re : clean up
