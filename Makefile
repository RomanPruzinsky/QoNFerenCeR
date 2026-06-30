.PHONY: inf-pause inf-start inf-reset inf-logs be-pause be-start be-logs

ENV_FILE := config/example.env # TODO: config/env.env for prod
COMPOSE  := docker compose -f deploy/docker-compose.yml --env-file $(ENV_FILE)


##################### ______ #####################
##################################################
##################### DEPLOY #####################

inf-pause:
	$(COMPOSE) stop

inf-start:
	$(COMPOSE) up -d --build

inf-reset:
	$(COMPOSE) down -v

inf-logs:
	$(COMPOSE) logs -f

##################### DEPLOY #####################
##################################################
##################### BACKEND ####################
# TODO: delete for prod, only for development

be-pause:
	$(COMPOSE) stop backend

be-start:
	$(COMPOSE) up -d --build backend

be-logs:
	$(COMPOSE) logs -f backend

##################### BACKEND ####################
##################################################
