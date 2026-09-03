.PHONY: first-setup check inf-pause inf-start inf-reset inf-logs release-android

ENV_FILE=config/QoNFerenCeR.env
COMPOSE=docker compose -f deploy/docker-compose.yml --env-file $(ENV_FILE)

##################################################
################## FIRST SETUP ###################

first-setup:
	@./scripts/installGitHooks.sh

################## FIRST SETUP ###################
##################################################
##################### CHECK ######################

check:
	@./scripts/check.sh

##################### CHECK ######################
##################################################
##################### DEPLOY #####################

inf-pause:
	$(COMPOSE) stop

inf-start: check
	$(COMPOSE) up -d --build

inf-reset:
	$(COMPOSE) down -v

inf-logs:
	$(COMPOSE) logs -f

##################### DEPLOY #####################
##################################################
#################### RELEASE #####################

release-android:
	@./scripts/releaseAndroid.sh

#################### RELEASE #####################
##################################################
##################### BACKEND ####################
# TODO: delete for prod, only for development

be-pause:
	$(COMPOSE) stop backend

be-start: check
	$(COMPOSE) up -d --build backend

be-logs:
	$(COMPOSE) logs -f backend

##################### BACKEND ####################
##################################################
