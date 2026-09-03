#!/usr/bin/env bash
# Shared helpers for scripts

####################################################
####################### ENV ########################

ENV_FILE="config/QoNFerenCeR.env"

env_value() {
	grep -m1 "^$1=" "$ENV_FILE" 2>/dev/null | cut -d= -f2-
}

####################### ENV ########################
####################################################
###################### OUTPUT ######################

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BOLD='\033[1m'
NO_COLOR='\033[0m'

PRINT_INDENT="   "

ok() {
	echo -e "${PRINT_INDENT}${GREEN}✓${NO_COLOR} $1"
}

err() {
	echo -e "${PRINT_INDENT}${RED}✗${NO_COLOR} $1"
}

banner_success() {
	echo -e "${GREEN}"
	echo "${PRINT_INDENT}███ █ █ ███ ███ ███ ███ ███"
	echo "${PRINT_INDENT}█   █ █ █   █   █   █   █  "
	echo "${PRINT_INDENT}███ █ █ █   █   ██  ███ ███"
	echo "${PRINT_INDENT}  █ █ █ █   █   █     █   █"
	echo "${PRINT_INDENT}███ ███ ███ ███ ███ ███ ███"
	echo -e "${NO_COLOR}"
}

banner_error() {
	echo -e "${RED}"
	echo "${PRINT_INDENT}███ ██  ██  ███ ██ "
	echo "${PRINT_INDENT}█   █ █ █ █ █ █ █ █"
	echo "${PRINT_INDENT}██  ██  ██  █ █ ██ "
	echo "${PRINT_INDENT}█   █ █ █ █ █ █ █ █"
	echo "${PRINT_INDENT}███ █ █ █ █ ███ █ █"
	echo -e "${NO_COLOR}"
}

###################### OUTPUT ######################
####################################################