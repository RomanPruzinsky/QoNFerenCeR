#!/usr/bin/env bash
# Fills a running dev stack with a believable "DevConf 2026": content + ~50 attendees + a few
# already-eaten meals. Everything real — attendees go through the provisioning API, so they get
# Keycloak users, anchors, roles and reservations exactly as production would.
#
# Prereqs: the stack is up (`make inf-start`), plus curl, jq and docker compose on PATH.
# Idempotent-ish: re-running creates a *second* batch of slots (the sequence keeps counting), but
# content is upserted. Wipe with `make inf-reset` for a clean slate.
set -euo pipefail

BACKEND="${BACKEND:-http://localhost:8081}"
KC="${KC:-http://localhost:8080}"
REALM="${REALM:-qonferencer}"
CLIENT="${CLIENT:-qonferencer-android}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-changeme}"
# Keycloak master admin (config/QoNFerenCeR.env: KC_ADMIN / KC_ADMIN_PASSWORD) — needed once to enable
# the custom user-profile attributes, which realm import silently drops (see below).
MASTER_USER="${MASTER_USER:-admin}"
MASTER_PASS="${MASTER_PASS:-admin}"
COMPOSE="docker compose -f $(dirname "$0")/../../deploy/docker-compose.yml --env-file $(dirname "$0")/../../config/QoNFerenCeR.env"

say() { printf '\n\033[1;36m▶ %s\033[0m\n' "$*"; }

token_for() { # username password -> access_token
	curl -fsS -X POST "$KC/realms/$REALM/protocol/openid-connect/token" \
		-d grant_type=password -d "client_id=$CLIENT" \
		-d "username=$1" -d "password=$2" | jq -r .access_token
}

say "Waiting for the backend to be healthy"
for _ in $(seq 1 60); do
	curl -fsS "$BACKEND/actuator/health" >/dev/null 2>&1 && break
	sleep 2
done

say "Enabling the custom user-profile attributes in Keycloak"
# Realm import ignores kc.user.profile.config, so isSpeaker / canCheckByName get dropped on every
# fresh stack and their JWT claims come out null. The Admin API honours it; PUT is idempotent.
# A production installer must run this same PUT once after first start.
MASTER_TOKEN="$(curl -fsS -X POST "$KC/realms/master/protocol/openid-connect/token" \
	-d grant_type=password -d client_id=admin-cli -d "username=$MASTER_USER" -d "password=$MASTER_PASS" | jq -r .access_token)"
curl -fsS "$KC/admin/realms/$REALM/users/profile" -H "Authorization: Bearer $MASTER_TOKEN" \
	| jq '.unmanagedAttributePolicy="ENABLED"
		| .attributes |= (map(select(.name|IN("isSpeaker","canCheckByName")|not)) + [
			{name:"isSpeaker",displayName:"isSpeaker",permissions:{view:["admin","user"],edit:["admin"]},multivalued:false},
			{name:"canCheckByName",displayName:"canCheckByName",permissions:{view:["admin","user"],edit:["admin"]},multivalued:false}
		])' \
	| curl -fsS -X PUT "$KC/admin/realms/$REALM/users/profile" \
		-H "Authorization: Bearer $MASTER_TOKEN" -H 'Content-Type: application/json' -d @- -o /dev/null -w "  profile → %{http_code}\n"

say "Seeding content (languages, translations, meal windows, screens)"
$COMPOSE exec -T postgres psql -U app -d qonferencer -v ON_ERROR_STOP=1 < "$(dirname "$0")/content.sql"

say "Logging in as $ADMIN_USER"
ADMIN_TOKEN="$(token_for "$ADMIN_USER" "$ADMIN_PASS")"
[ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "null" ] || { echo "no admin token — is the realm imported?"; exit 1; }

say "Reading meal window ids from /splash"
SPLASH="$(curl -fsS "$BACKEND/api/v1/splash")"
w() { jq -r --arg k "$1" '.mealWindows[] | select(.nameKey==$k) | .id' <<<"$SPLASH" | head -n1; }
W1="$(w meal.day1.lunch)"; W2="$(w meal.day1.dinner)"; W3="$(w meal.day2.lunch)"
[ -n "$W1" ] && [ -n "$W2" ] && [ -n "$W3" ] || { echo "meal windows missing — did content.sql run?"; exit 1; }
echo "windows: lunch=$W1 dinner=$W2 saturday=$W3"

# name | ROLE | isSpeaker | canCheckByName | company | tshirt | diet(->variant)
ATTENDEES=(
	"Zuzana Krajčíová|ORGANISER|false|true|InfoDesk|M|none"
	"Marek Dubovský|ORGANISER|false|true|InfoDesk|L|vegetarian"
	"Peter Horváth|VOLUNTEER|false|false|Crew|L|none"
	"Lucia Nagyová|VOLUNTEER|false|false|Crew|S|vegan"
	"Tomáš Baláž|VOLUNTEER|false|false|Crew|XL|none"
	"Karolína Šťastná|LEADER|false|false|Crew|M|gluten-free"
	"Roman Pružinský|VISITOR|true|false|Acme|L|none"
	"Jana Kováčová|VISITOR|true|false|Globex|M|vegan"
	"Ondřej Němec|VISITOR|true|false|Initech|L|vegetarian"
	"Petra Svobodová|VISITOR|true|false|Umbrella|S|none"
	"Michal Král|VISITOR|false|false|Acme|M|none"
	"Veronika Horáková|VISITOR|false|false|Globex|S|vegan"
	"Martin Varga|VISITOR|false|false|Hooli|L|none"
	"Simona Tóthová|VISITOR|false|false|Piedpiper|M|vegetarian"
	"Jakub Novotný|VISITOR|false|false|Acme|XL|none"
	"Katarína Baránková|VISITOR|false|false|Globex|S|gluten-free"
	"Filip Procházka|VISITOR|false|false|Initech|L|none"
	"Nikola Danišová|VISITOR|false|false|Umbrella|M|vegan"
	"Dávid Kollár|VISITOR|false|false|Hooli|L|none"
	"Barbora Marková|VISITOR|false|false|Piedpiper|S|vegetarian"
	"Adam Sedláček|VISITOR|false|false|Acme|M|none"
	"Kristína Bartošová|VISITOR|false|false|Globex|S|none"
	"Matej Polák|VISITOR|false|false|Initech|XL|vegan"
	"Dominika Urbanová|VISITOR|false|false|Umbrella|M|none"
	"Lukáš Beneš|VISITOR|false|false|Hooli|L|vegetarian"
	"Natália Machová|VISITOR|false|false|Piedpiper|S|none"
	"Samuel Fischer|VISITOR|false|false|Acme|L|gluten-free"
	"Alexandra Kučerová|VISITOR|false|false|Globex|M|vegan"
	"Patrik Blažek|VISITOR|false|false|Initech|L|none"
	"Denisa Holubová|VISITOR|false|false|Umbrella|S|none"
	"Richard Konečný|VISITOR|false|false|Hooli|XL|none"
	"Monika Sýkorová|VISITOR|false|false|Piedpiper|M|vegetarian"
	"Erik Doležal|VISITOR|false|false|Acme|L|none"
	"Tereza Malá|VISITOR|false|false|Globex|S|vegan"
	"Marián Zeman|VISITOR|false|false|Initech|L|none"
	"Ivana Pospíšilová|VISITOR|false|false|Umbrella|M|none"
	"Róbert Slávik|VISITOR|false|false|Hooli|L|gluten-free"
	"Gabriela Ružičková|VISITOR|false|false|Piedpiper|S|vegetarian"
	"Vladimír Hájek|VISITOR|false|false|Acme|XL|none"
	"Andrea Jandová|VISITOR|false|false|Globex|M|vegan"
	"Juraj Melicher|VISITOR|false|false|Initech|L|none"
	"Silvia Benčíková|VISITOR|false|false|Umbrella|S|none"
	"Pavol Kubiš|VISITOR|false|false|Hooli|L|none"
	"Renáta Vávrová|VISITOR|false|false|Piedpiper|M|vegetarian"
	"Miroslav Antol|VISITOR|false|false|Acme|L|none"
	"Ľubica Gajdošová|VISITOR|false|false|Globex|S|vegan"
	"Štefan Mráz|VISITOR|false|false|Initech|XL|none"
	"Emília Klimentová|VISITOR|false|false|Umbrella|M|gluten-free"
	"Rastislav Chovanec|VISITOR|false|false|Hooli|L|none"
	"Daniela Šimková|VISITOR|false|false|Piedpiper|S|vegetarian"
)

variant_for() {
	case "$1" in
		vegan) echo "meal.variant.vegan" ;;
		vegetarian) echo "meal.variant.veggie" ;;
		gluten-free) echo "meal.variant.gf" ;;
		*) echo "meal.variant.meat" ;;
	esac
}

declare -a VISITOR_IDS=()
VOL_USER=""; VOL_ID=""

say "Provisioning ${#ATTENDEES[@]} attendees via POST /admin/add-user"
for row in "${ATTENDEES[@]}"; do
	IFS='|' read -r name role sp cc company tshirt diet <<<"$row"
	variant="$(variant_for "$diet")"
	payload="$(jq -n \
		--arg fn "$name" --arg role "$role" --argjson sp "$sp" --argjson cc "$cc" \
		--arg comp "$company" --arg ts "$tshirt" --arg diet "$diet" --arg variant "$variant" \
		--argjson w1 "$W1" --argjson w2 "$W2" --argjson w3 "$W3" \
		'{fullName:$fn, role:$role, isSpeaker:$sp, canCheckByName:$cc,
		  customData:{company:$comp, tshirt:$ts, dietary:$diet},
		  meals:[{windowId:$w1,variantKey:$variant},{windowId:$w2,variantKey:$variant},{windowId:$w3,variantKey:$variant}]}')"
	resp="$(curl -fsS -X POST "$BACKEND/api/v1/admin/add-user" \
		-H "Authorization: Bearer $ADMIN_TOKEN" -H 'Content-Type: application/json' -d "$payload")"
	uid="$(jq -r .userId <<<"$resp")"; uname="$(jq -r .username <<<"$resp")"
	printf '  %-24s %-9s %s\n' "$name" "$role" "$uname (#$uid)"
	[ "$role" = "VISITOR" ] && VISITOR_IDS+=("$uid")
	if [ "$role" = "VOLUNTEER" ] && [ -z "$VOL_USER" ]; then VOL_USER="$uname"; VOL_ID="$uid"; fi
done

say "Issuing a password for the demo volunteer $VOL_USER and recording ~15 lunches"
VOL_PASS="$(curl -fsS -X POST "$BACKEND/api/v1/admin/login/$VOL_ID" \
	-H "Authorization: Bearer $ADMIN_TOKEN" | jq -r .password)"
VOL_TOKEN="$(token_for "$VOL_USER" "$VOL_PASS")"
served=0
for uid in "${VISITOR_IDS[@]:0:15}"; do
	scan="$(jq -n --arg t "$uid" --argjson w "$W1" \
		'{token:$t, mealWindowId:$w, idempotencyKey:"'"$(cat /proc/sys/kernel/random/uuid)"'", scannerType:"MANUAL"}')"
	res="$(curl -fsS -X POST "$BACKEND/api/v1/meal-scan" \
		-H "Authorization: Bearer $VOL_TOKEN" -H 'Content-Type: application/json' -d "$scan" | jq -r .result)"
	[ "$res" = "APPROVED" ] && served=$((served + 1))
done

cat <<EOF

$(say "Done")
  content:      4 screens, 3 meal windows, en+sk
  attendees:    ${#ATTENDEES[@]} provisioned
  lunches out:  $served / 15 scanned as consumed
  demo login:   admin / changeme                          (ADMIN)
  demo login:   $VOL_USER / $VOL_PASS   (VOLUNTEER — scanner)

  Log in as any slot after issuing its password:
    curl -X POST $BACKEND/api/v1/admin/login/<userId> -H "Authorization: Bearer <adminToken>"
EOF
