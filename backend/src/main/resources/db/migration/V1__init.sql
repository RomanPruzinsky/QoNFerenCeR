-- Accent-insensitive name search: Postgres has no accent-blind collation usable with LIKE,
-- so names are folded explicitly. unaccent() is not IMMUTABLE and a generated column demands one.
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE FUNCTION immutable_unaccent(TEXT) RETURNS TEXT
	LANGUAGE sql IMMUTABLE PARALLEL SAFE STRICT AS
	$$ SELECT public.unaccent('public.unaccent', $1) $$;

-- Conference attendee, holds what Keycloak can't
CREATE TABLE app_user (
	id          BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	kc_sub      UUID        NOT NULL UNIQUE,			-- User's Keycloak identity
	qr_secret   BYTEA       NOT NULL,					-- HMAC secret for QR/NFC tokens
	qr_secret_v SMALLINT    NOT NULL DEFAULT 0,		-- Version of used qr_secret
	full_name   TEXT        NOT NULL,					-- Always required, so it is a column and not a custom_data key
	custom_data JSONB       NOT NULL DEFAULT '{}',	-- Custom data
	created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Info-desk lookup over the folded name; indexes the expression instead of storing a second copy
CREATE INDEX index_app_user_full_name ON app_user
	USING GIN ((lower(immutable_unaccent(full_name))) gin_trgm_ops);

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
	icon      VARCHAR(32)  NOT NULL DEFAULT 'help',		-- key into client's icon options
	body      JSONB        NOT NULL DEFAULT '[]'			-- elements, render order = array order
);

-- Meal serving window (organizer-defined)
CREATE TABLE meal_window (
	id        BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name_key  VARCHAR(128) NOT NULL,	-- translation key of the window name
	starts_at TIMESTAMPTZ  NOT NULL,
	ends_at   TIMESTAMPTZ  NOT NULL
);

-- Per-person meal reservation, one meal per window
CREATE TABLE meal_reservation (
	user_id     BIGINT       NOT NULL REFERENCES app_user(id),
	window_id   BIGINT       NOT NULL REFERENCES meal_window(id),
	variant_key VARCHAR(128) NOT NULL,	-- translation key of the meal variant
	PRIMARY KEY (user_id, window_id)
);

-- Meal consumption record (scan); presence = consumed, insert-once
CREATE TABLE meal_consumption (
	user_id         BIGINT      NOT NULL REFERENCES app_user(id),
	window_id       BIGINT      NOT NULL REFERENCES meal_window(id),
	scanned_by      BIGINT      REFERENCES app_user(id),
	scanned_at      TIMESTAMPTZ NOT NULL,
	idempotency_key UUID        NOT NULL,
	PRIMARY KEY (user_id, window_id)
);

-- Per-window stats ("how many ate in window X"); PK leads with user_id, so it can't serve those
CREATE INDEX index_meal_consumption_window_id ON meal_consumption (window_id);
