-- Repeatable dev seed: languages + demo content so the app has something to render.
-- Idempotent (re-runs whenever this file's checksum changes).

INSERT INTO language (code, name, is_default) VALUES
	('en', 'English', true)
ON CONFLICT (code) DO NOTHING;

INSERT INTO translation (key, lang_code, text) VALUES
	('screen.home.title',   'en', 'Home'),
	('screen.agenda.title', 'en', 'Agenda'),
	('home.welcome',        'en', 'Welcome to the conference')
ON CONFLICT (key, lang_code) DO NOTHING;

INSERT INTO custom_screen (id, title_key, min_role, body) VALUES
	('home', 'screen.home.title', 'GUEST', '[
		{"type":"TEXT","source":{"kind":"REF","key":"home.welcome"},"size":"LARGE"}
	]'::jsonb),
	('agenda', 'screen.agenda.title', 'VISITOR', '[
		{"type":"TEXT","source":{"kind":"REF","key":"screen.agenda.title"},"size":"MEDIUM"}
	]'::jsonb)
ON CONFLICT (id) DO NOTHING;
