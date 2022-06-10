PROJECT_NAME=azure-eh

start:
	@docker stop resurface || true
	@docker build -t azure-eh-consumer:1.0.1 --no-cache .
	@docker-compose up --detach

stop:
	@docker-compose stop
	@docker-compose down --volumes --remove-orphans
	@docker image rmi -f azure-eh-consumer

bash:
	@docker exec -it azure-eh bash

logs:
	@docker logs -f azure-eh

restart:
	@docker-compose stop
	@docker-compose up --detach
