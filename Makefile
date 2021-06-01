PROJECT_NAME=azure-eh

start:
	@docker stop resurface || true
	@docker build -t test-azure-eh --no-cache .
	@docker-compose up --detach

stop:
	@docker-compose stop
	@docker-compose down --volumes --remove-orphans
	@docker image rmi -f test-azure-eh

bash:
	@docker exec -it azure-eh bash

logs:
	@docker logs -f azure-eh

restart:
	@docker-compose stop
	@docker-compose up --detach