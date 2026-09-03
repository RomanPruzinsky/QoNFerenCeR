#!/usr/bin/env bash
# QoNFerenCeR config check

##################################################
#################### HELPERS #####################

. scripts/lib.sh

KEEP_QUIET="${KEEP_QUIET:-false}"
[ "$KEEP_QUIET" = "true" ] && exec >/dev/null 2>/dev/null

echo -e "${BOLD}QoNFerenCeR config check${NO_COLOR}"

RESULTS_ARR=()   # each entry: "PASS|<message>" or "FAIL|<message>|<fix hint>"

intro() {
	echo
	echo -e "${BOLD}→ $1${NO_COLOR}"
}

pass() {
	ok "$1"
	RESULTS_ARR+=("PASS|$1")
}

fail() {
	err "$1"
	RESULTS_ARR+=("FAIL|$1|$2")
}

APP_YML="backend/src/main/resources/application.yml"

yml_value() {
	local key="$1"
	grep -m1 -E "^[[:space:]]*$key:" "$APP_YML" | sed -E "s/^[[:space:]]*$key:[[:space:]]*//; s/[[:space:]]*$//"
}

#################### HELPERS #####################
##################################################
################# REQUIRED FILES #################

intro "Checking required files"

LOGO_FILE="config/logo.png"
REALM_FILE="deploy/keycloak/realm-export.json"

if [ -f "$ENV_FILE" ]; then
	pass "$ENV_FILE exists"
else
	fail "$ENV_FILE missing" "Can reuse QoNFerenCeR.env from official QoNFerenCeR repo"
fi

if [ -f "$LOGO_FILE" ]; then
	pass "$LOGO_FILE exists"
else
	fail "$LOGO_FILE missing" "Add square PNG logo image at $LOGO_FILE"
fi

if [ -f "$REALM_FILE" ]; then
	pass "$REALM_FILE exists"
else
	fail "$REALM_FILE missing" "Can reuse realm-export.json from official QoNFerenCeR repo"
fi

################# REQUIRED FILES #################
##################################################
################ REQUIRED ENTRIES ################

intro "Checking required entries in $ENV_FILE"

check_required_key() {
	local key="$1"
	local fix_hint="$2"
	local value
	value="$(env_value "$key")"
	if [ -n "$value" ]; then
		pass "$key is set"
	else
		fail "$key is missing or empty" "$fix_hint"
	fi
}

if [ -f "$ENV_FILE" ]; then
	REQUIRED_KEYS=(
		API_VERSION
		EVENT_ID
		BACKEND_BASE_URL
		KEYCLOAK_BASE_URL
		KEYCLOAK_HOSTNAME
		POSTGRES_USER
		POSTGRES_PASSWORD
		KC_CONSOLEADMIN_USERNAME
		KC_CONSOLEADMIN_PASSWORD
		KC_BEADMIN_CLIENT_SECRET
		N8N_ENABLED
		N8N_ENCRYPTION_KEY
		RELEASE_KEYSTORE_PATH
		RELEASE_KEYSTORE_PASSWORD
		RELEASE_KEY_ALIAS
		RELEASE_KEY_PASSWORD
	)

	for key in "${REQUIRED_KEYS[@]}"; do
		check_required_key "$key" "Add '$key=<value>' to $ENV_FILE"
	done

	if [ "$(env_value N8N_ENABLED)" = "true" ]; then
		N8N_KEYS=(
			N8N_PATH_PREFIX
			N8N_TIMEOUT_MS
			BE_N8N_COMMS__AUTH_TOKEN
		)

		for key in "${N8N_KEYS[@]}"; do
			check_required_key "$key" "Add '$key=<value>' to $ENV_FILE, or set N8N_ENABLED=false"
		done
	fi
else
	echo "${PRINT_INDENT}(skipped — $ENV_FILE missing, see above)"
fi

################ REQUIRED ENTRIES ################
##################################################
############### CROSS-FILE MATCHES ###############

intro "Checking config matches deploy files"

if [ -f "$ENV_FILE" ] && [ -f "$REALM_FILE" ] && [ -f "$APP_YML" ]; then
	env_secret="$(env_value KC_BEADMIN_CLIENT_SECRET)"
	yml_client_id="$(yml_value client-id)"
	realm_secret="$(jq -r --arg cid "$yml_client_id" '.clients[] | select(.clientId==$cid).secret' "$REALM_FILE")"
	if [ -n "$env_secret" ] && [ "$env_secret" = "$realm_secret" ]; then
		pass "KC_BEADMIN_CLIENT_SECRET in QoNFerenCeR.env matches '$yml_client_id' client secret in realm-export.json"
	else
		fail "KC_BEADMIN_CLIENT_SECRET in QoNFerenCeR.env ('$env_secret') != realm-export.json client secret ('$realm_secret')" "Make both equal"
	fi

	yml_realm="$(yml_value realm)"
	realm_name="$(jq -r '.realm' "$REALM_FILE")"
	if [ "$realm_name" = "$yml_realm" ]; then
		pass "realm-export.json realm is same as application.yml"
	else
		fail "realm in realm-export.json ('$realm_name') != realm in application.yml ('$yml_realm')" "Make both equal"
	fi

	yml_username="$(yml_value bootstrap-username)"
	admin_user="$(jq -r --arg u "$yml_username" '.users[] | select(.username==$u).username' "$REALM_FILE")"
	if [ "$admin_user" = "$yml_username" ]; then
		pass "realm-export.json bootstrap user is same as application.yml"
	else
		fail "bootstrap user in realm-export.json ('$admin_user') != bootstrap-username in application.yml ('$yml_username')" "Make both equal"
	fi
else
	echo "${PRINT_INDENT}(skipped — $ENV_FILE, $REALM_FILE or $APP_YML missing)"
fi

if [ -f "$ENV_FILE" ] && [ -f "Makefile" ]; then
	makefile_env="$(grep -m1 '^ENV_FILE' Makefile | cut -d= -f2- | sed -E 's/^[[:space:]]*//; s/[[:space:]]*$//')"
	if [ "$makefile_env" = "$ENV_FILE" ]; then
		pass "Makefile's ENV_FILE points at $ENV_FILE"
	else
		fail "Makefile's ENV_FILE is '$makefile_env', expected '$ENV_FILE'" "Sync ENV_FILE in Makefile with $ENV_FILE"
	fi
fi

############### CROSS-FILE MATCHES ###############
##################################################
############## RELEASE SIGNING KEY ###############

intro "Checking release signing key"

if [ -f "$ENV_FILE" ]; then
	keystore_path="$(env_value RELEASE_KEYSTORE_PATH)"
	if [ -n "$keystore_path" ]; then
		if [ -f "$keystore_path" ]; then
			pass "$keystore_path exists"
		else
			fail "config/key missing" "Generate keystore"
		fi
	else
		echo "${PRINT_INDENT}(skipped — RELEASE_KEYSTORE_PATH missing, see above)"
	fi
else
	echo "${PRINT_INDENT}(skipped — $ENV_FILE missing)"
fi

############## RELEASE SIGNING KEY ###############
##################################################
############# TODO_CHANGEME MARKERS ##############

intro "Checking pre-production TODO_CHANGEME markers"

matches="$(grep -rn --exclude-dir=.git \
	--include="*.env" --include="*.yml" --include="*.yaml" --include="*.kt" --include="*.kts" \
	"TODO_CHANGEME" . | cut -d: -f1,2 | sed "s/^/${PRINT_INDENT}${PRINT_INDENT}/")"

if [ -n "$matches" ]; then
	fail "Pending TODO_CHANGEME markers found:
${matches}" "Replace placeholders before real deploy"
else
	pass "No pending TODO_CHANGEME markers found"
fi

############# TODO_CHANGEME MARKERS ##############
##################################################
#################### SUMMARY #####################

HAS_FAIL=false
printf '%s\n' "${RESULTS_ARR[@]}" | grep -q '^FAIL' && HAS_FAIL=true

echo
if [ "$HAS_FAIL" = "true" ]; then
	banner_error
else
	banner_success
fi

for c in "${RESULTS_ARR[@]}"; do
	status="${c%%|*}"
	rest="${c#*|}"
	if [ "$status" = "PASS" ]; then
		echo -e "${PRINT_INDENT}${GREEN}✓${NO_COLOR} $rest"
	else
		msg="${rest%%|*}"
		fix="${rest#*|}"
		echo -e "${PRINT_INDENT}${RED}✗${NO_COLOR} $msg"
		echo -e "${PRINT_INDENT}${PRINT_INDENT}${YELLOW}→${NO_COLOR} $fix"
	fi
done

echo

if [ "$HAS_FAIL" = "true" ]; then
	exit 1
fi

#################### SUMMARY #####################
##################################################
