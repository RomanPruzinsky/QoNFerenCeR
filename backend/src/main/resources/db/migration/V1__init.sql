-- Conference attendee, holds what Keycloak can't
CREATE TABLE app_user (
	id          BIGSERIAL PRIMARY KEY,
	kc_sub      UUID        NOT NULL UNIQUE,			-- User's Keycloak identity
	qr_secret   BYTEA       NOT NULL,					-- HMAC secret for QR/NFC tokens
	qr_secret_v SMALLINT    NOT NULL DEFAULT 0,		-- Version of used qr_secret
	consented   BOOLEAN     NOT NULL DEFAULT false,	-- Whether user accepted GDPR
	custom_data JSONB       NOT NULL DEFAULT '{}',	-- Custom data
	created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Monotonic source of slot_NNN usernames
CREATE SEQUENCE slot_seq START 1;

-- Available UI language
CREATE TABLE language (
	code       VARCHAR(8)  PRIMARY KEY,					-- "en", "sk", "en-US", ...
	name       VARCHAR(32) NOT NULL,						-- readable name
	is_default BOOLEAN     NOT NULL DEFAULT false	-- fallback when a key has no translation
);

-- Translation entry per key + language
CREATE TABLE translation (
	key       VARCHAR(128) NOT NULL,
	lang_code VARCHAR(8)   NOT NULL REFERENCES language(code),
	text      TEXT         NOT NULL,
	PRIMARY KEY (key, lang_code)
);

-- Runtime-added screen; body = List<CustomElement> as ordered JSON array
CREATE TABLE custom_screen (
	id        VARCHAR(64)  PRIMARY KEY,
	title_key VARCHAR(128) NOT NULL,							-- translation key
	min_role  VARCHAR(16)  NOT NULL DEFAULT 'VISITOR',	-- min role to see
	body      JSONB        NOT NULL DEFAULT '[]'			-- elements, render order = array order
);
