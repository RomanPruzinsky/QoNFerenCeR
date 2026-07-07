-- Conference attendee, holds what Keycloak can't
CREATE TABLE app_user (
	id          BIGSERIAL PRIMARY KEY,
	kc_sub      UUID        NOT NULL UNIQUE,        -- User's Keycloak identity
	qr_secret   BYTEA       NOT NULL,               -- HMAC secret for QR/NFC tokens
	qr_secret_v SMALLINT    NOT NULL DEFAULT 0,     -- Version of used qr_secret
	consented   BOOLEAN     NOT NULL DEFAULT false, -- Whether user accepted GDPR
	custom_json JSONB       NOT NULL DEFAULT '{}',  -- Custom data, keys in CustomElementDef
	created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
