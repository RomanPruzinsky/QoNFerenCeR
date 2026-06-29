.PHONY: inf-pause inf-start inf-reset inf-logs be-pause be-start be-logs


##################### ______ #####################
##################################################
##################### DEPLOY #####################

inf-pause:
	docker compose -f deploy/docker-compose.yml stop

inf-start:
	docker compose -f deploy/docker-compose.yml up -d --build

inf-reset:
	docker compose -f deploy/docker-compose.yml down -v

inf-logs:
	docker compose -f deploy/docker-compose.yml logs -f

##################### DEPLOY #####################
##################################################
##################### BACKEND ####################
# TODO: delete for prod, only for development

be-pause:
	docker compose -f deploy/docker-compose.yml stop backend

be-start:
	docker compose -f deploy/docker-compose.yml up -d --build backend

be-logs:
	docker compose -f deploy/docker-compose.yml logs -f backend

##################### BACKEND ####################
##################################################
