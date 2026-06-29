.PHONY: LOC


##################### ______ #####################
##################################################
##################### DEPLOY #####################

infra-up:
	docker compose -f deploy/docker-compose.yml up -d

infra-down:
	docker compose -f deploy/docker-compose.yml down

infra-logs:
	docker compose -f deploy/docker-compose.yml logs -f

infra-status:
	docker compose -f deploy/docker-compose.yml ps

##################### DEPLOY #####################
##################################################
##################### BACKEND ####################

backend-up:
	docker compose -f deploy/docker-compose.yml up -d backend

backend-down:
	docker compose -f deploy/docker-compose.yml stop backend

backend-logs:
	docker compose -f deploy/docker-compose.yml logs -f backend

backend-build:
	docker compose -f deploy/docker-compose.yml build backend

##################### BACKEND ####################
##################################################
