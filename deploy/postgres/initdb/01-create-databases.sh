#!/bin/sh
# Runs once, on Postgres' first start
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE keycloak;
	CREATE DATABASE n8n;
EOSQL
