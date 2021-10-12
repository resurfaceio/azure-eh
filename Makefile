PROJECT_NAME=azure-eh

start:
	@docker stop resurface || true
	@docker build -t azure-eh --no-cache .
	@docker-compose up --detach

stop:
	@docker-compose stop
	@docker-compose down --volumes --remove-orphans
	@docker image rmi -f azure-eh

bash:
	@docker exec -it listener-azure-eh bash

logs:
	@docker logs -f listener-azure-eh

restart:
	@docker-compose stop
	@docker-compose up --detach
