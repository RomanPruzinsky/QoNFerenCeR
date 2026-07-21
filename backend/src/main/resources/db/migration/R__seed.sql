-- Repeatable dev seed: languages + demo content so the app has something to render.
-- Idempotent (re-runs whenever this file's checksum changes).

INSERT INTO language (code, name, is_default) VALUES
	('en', 'English', true)
ON CONFLICT (code) DO NOTHING;

INSERT INTO translation (key, lang_code, text) VALUES
	('screen.home.title',   'en', 'Home'),
	('screen.agenda.title', 'en', 'Agenda'),
	('home.welcome',        'en', 'Welcome to the conference'),
	('meal.lunch1.name',    'en', 'Lunch — Day 1')
ON CONFLICT (key, lang_code) DO NOTHING;

-- Meal window (reservations are imported per-attendee, not seeded here).
-- No explicit id: GENERATED ALWAYS AS IDENTITY rejects one anyway.
INSERT INTO meal_window (name_key, starts_at, ends_at)
SELECT 'meal.lunch1.name', '2026-09-01T11:30:00Z', '2026-09-01T13:00:00Z'
WHERE NOT EXISTS (SELECT 1 FROM meal_window WHERE name_key = 'meal.lunch1.name');

INSERT INTO custom_screen (id, title_key, min_role, body) VALUES
	('home', 'screen.home.title', 'GUEST', '[
		{"type":"TEXT","source":{"kind":"REF","key":"home.welcome"},"size":"LARGE"}
	]'::jsonb),
	('agenda', 'screen.agenda.title', 'VISITOR', '[
		{"type":"TEXT","source":{"kind":"REF","key":"screen.agenda.title"},"size":"MEDIUM"}
	]'::jsonb)
ON CONFLICT (id) DO NOTHING;
