-- Repeatable dev seed: languages + demo content so the app has something to render.
-- Idempotent (re-runs whenever this file's checksum changes).

INSERT INTO language (code, name, is_default) VALUES
	('en', 'English', true)
ON CONFLICT (code) DO NOTHING;

INSERT INTO translation (key, lang_code, text) VALUES
	('screen.home.title',   'en', 'Home'),
	('screen.agenda.title', 'en', 'Agenda'),
	('home.welcome',        'en', 'Welcome to the conference'),
	('meal.lunch1.name',     'en', 'Lunch — Day 1'),
	('meal.dinner1.name',    'en', 'Dinner — Day 1'),
	('meal.breakfast2.name', 'en', 'Breakfast — Day 2'),
	('meal.lunch2.name',     'en', 'Lunch — Day 2'),
	('meal.dinner2.name',    'en', 'Dinner — Day 2'),
	('meal.variant.standard', 'en', 'Standard'),
	-- app chrome (system labels — also translation-keyed, not compiled resources)
	('settings.appColors.intro', 'en', 'App colors'),
	('settings.language.intro',  'en', 'Language')
ON CONFLICT (key, lang_code) DO NOTHING;

-- Meal windows (reservations are imported per-attendee, not seeded here).
-- No explicit id: GENERATED ALWAYS AS IDENTITY rejects one anyway.
INSERT INTO meal_window (name_key, starts_at, ends_at)
SELECT v.name_key, v.starts_at::timestamptz, v.ends_at::timestamptz
FROM (VALUES
	('meal.lunch1.name',     '2026-09-01T11:30:00Z', '2026-09-01T13:00:00Z'),
	('meal.dinner1.name',    '2026-09-01T18:30:00Z', '2026-09-01T20:00:00Z'),
	('meal.breakfast2.name', '2026-09-02T08:00:00Z', '2026-09-02T09:30:00Z'),
	('meal.lunch2.name',     '2026-09-02T11:30:00Z', '2026-09-02T13:00:00Z'),
	('meal.dinner2.name',    '2026-09-02T18:30:00Z', '2026-09-02T20:00:00Z')
) AS v(name_key, starts_at, ends_at)
WHERE NOT EXISTS (SELECT 1 FROM meal_window w WHERE w.name_key = v.name_key);

-- Custom data + meals for the bootstrap admin (anchored by BootstrapAdminAnchor after Flyway
-- runs, so no-op on the very first boot of a fresh DB — takes effect on the next restart).
UPDATE app_user
SET custom_data = custom_data || '{"note": "R__seed test fixture", "company": "QoNFerenCeR"}'::jsonb
WHERE full_name = 'First Admin';

INSERT INTO meal_reservation (user_id, window_id, variant_key)
SELECT u.id, w.id, 'meal.variant.standard'
FROM app_user u
JOIN meal_window w ON w.name_key IN (
	'meal.lunch1.name', 'meal.dinner1.name', 'meal.breakfast2.name', 'meal.lunch2.name', 'meal.dinner2.name'
)
WHERE u.full_name = 'First Admin'
ON CONFLICT (user_id, window_id) DO NOTHING;

-- Half already scanned as eaten, half still pending, for realistic test data.
INSERT INTO meal_consumption (user_id, window_id, scanned_by, scanned_at, idempotency_key)
SELECT u.id, w.id, u.id, now(), gen_random_uuid()
FROM app_user u
JOIN meal_window w ON w.name_key IN ('meal.lunch1.name', 'meal.dinner1.name', 'meal.lunch2.name')
WHERE u.full_name = 'First Admin'
ON CONFLICT (user_id, window_id) DO NOTHING;

INSERT INTO custom_screen (id, title_key, min_role, icon, body) VALUES
	('home', 'screen.home.title', 'ANONYM', 'home', '[
		{"type":"TEXT","source":{"kind":"REF","key":"home.welcome"},"size":"LARGE"}
	]'::jsonb),
	('agenda', 'screen.agenda.title', 'VISITOR', 'schedule', '[
		{"type":"TEXT","source":{"kind":"REF","key":"screen.agenda.title"},"size":"MEDIUM"}
	]'::jsonb)
ON CONFLICT (id) DO NOTHING;
