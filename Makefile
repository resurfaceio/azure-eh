PROJECT_NAME=azure-eh

start:
	@docker stop resurface || true
	@docker build -t resurfaceio-eventhub-consumer --no-cache .
	@docker-compose up --detach

stop:
	@docker-compose stop
	@docker-compose down --volumes --remove-orphans
	@docker image rmi -f resurfaceio-eventhub-consumer

bash:
	@docker exec -it azure-eh-listener bash

logs:
	@docker logs -f azure-eh-listener

restart:
	@docker-compose stop
	@docker-compose up --detach
