-- Demo content for a fake "DevConf 2026" conference: languages, translations, meal windows, screens.
-- Idempotent and authoritative — re-running it resets the demo content to this state.
-- Attendees are NOT here; they go through the real provisioning API in seed.sh.

INSERT INTO language (code, name, is_default) VALUES
	('en', 'English',    true),
	('sk', 'Slovenčina', false)
ON CONFLICT (code) DO UPDATE SET name = EXCLUDED.name, is_default = EXCLUDED.is_default;

INSERT INTO translation (key, lang_code, text) VALUES
	('menu.home',            'en', 'Home'),            ('menu.home',            'sk', 'Domov'),
	('menu.agenda',          'en', 'Agenda'),          ('menu.agenda',          'sk', 'Program'),
	('menu.sponsors',        'en', 'Sponsors'),        ('menu.sponsors',        'sk', 'Partneri'),
	('menu.info',            'en', 'Info'),            ('menu.info',            'sk', 'Info'),
	('home.welcome',         'en', 'Welcome to DevConf 2026'),
	('home.welcome',         'sk', 'Vitajte na DevConf 2026'),
	('home.intro',           'en', 'Three days of talks, workshops and good food. Your rotating QR is your badge.'),
	('home.intro',           'sk', 'Tri dni prednášok, workshopov a dobrého jedla. Tvoj rotujúci QR je tvoja visačka.'),
	('agenda.note',          'en', 'The live schedule is pulled from the organizer automation.'),
	('agenda.note',          'sk', 'Živý program sa ťahá z organizátorovej automatizácie.'),
	('sponsors.title',       'en', 'Thanks to our partners'),
	('sponsors.title',       'sk', 'Ďakujeme našim partnerom'),
	('info.wifi',            'en', 'Wi-Fi: DevConf2026 / password: hallway-track'),
	('info.wifi',            'sk', 'Wi-Fi: DevConf2026 / heslo: hallway-track'),
	('info.help',            'en', 'Lost something? The info desk is by the main entrance.'),
	('info.help',            'sk', 'Stratil si niečo? Infopult je pri hlavnom vchode.'),
	('meal.day1.lunch',      'en', 'Friday lunch'),    ('meal.day1.lunch',      'sk', 'Piatok obed'),
	('meal.day1.dinner',     'en', 'Friday dinner'),   ('meal.day1.dinner',     'sk', 'Piatok večera'),
	('meal.day2.lunch',      'en', 'Saturday lunch'),  ('meal.day2.lunch',      'sk', 'Sobota obed'),
	('meal.variant.meat',    'en', 'Chicken & rice'),  ('meal.variant.meat',    'sk', 'Kura s ryžou'),
	('meal.variant.vegan',   'en', 'Vegan bowl'),      ('meal.variant.vegan',   'sk', 'Vegan bowl'),
	('meal.variant.veggie',  'en', 'Vegetarian pasta'),('meal.variant.veggie',  'sk', 'Vegetariánske cestoviny'),
	('meal.variant.gf',      'en', 'Gluten-free plate'),('meal.variant.gf',     'sk', 'Bezlepkový tanier')
ON CONFLICT (key, lang_code) DO UPDATE SET text = EXCLUDED.text;

-- Three meal windows across two days. No explicit id: IDENTITY rejects one; keyed by name_key.
INSERT INTO meal_window (name_key, starts_at, ends_at)
SELECT v.name_key, v.starts_at::timestamptz, v.ends_at::timestamptz
FROM (VALUES
	('meal.day1.lunch',  '2026-09-04T11:30:00Z', '2026-09-04T13:30:00Z'),
	('meal.day1.dinner', '2026-09-04T18:30:00Z', '2026-09-04T20:30:00Z'),
	('meal.day2.lunch',  '2026-09-05T11:30:00Z', '2026-09-05T13:30:00Z')
) AS v(name_key, starts_at, ends_at)
WHERE NOT EXISTS (SELECT 1 FROM meal_window w WHERE w.name_key = v.name_key);

-- Screens the app renders. Bodies are List<CustomElement>; render order = array order.
INSERT INTO custom_screen (id, title_key, min_role, body) VALUES
	('home', 'menu.home', 'GUEST', '[
		{"type":"TEXT","source":{"kind":"REF","key":"home.welcome"},"size":"LARGE"},
		{"type":"IMAGE","url":"https://picsum.photos/seed/devconf/800/300"},
		{"type":"TEXT","source":{"kind":"REF","key":"home.intro"},"size":"MEDIUM"}
	]'::jsonb),
	('agenda', 'menu.agenda', 'VISITOR', '[
		{"type":"TEXT","source":{"kind":"REF","key":"menu.agenda"},"size":"LARGE"},
		{"type":"TEXT","source":{"kind":"REF","key":"agenda.note"},"size":"SMALL"},
		{"type":"TEXT","source":{"kind":"LINK","url":"http://localhost:5678/webhook/agenda"},"size":"MEDIUM"}
	]'::jsonb),
	('sponsors', 'menu.sponsors', 'GUEST', '[
		{"type":"TEXT","source":{"kind":"REF","key":"sponsors.title"},"size":"LARGE"},
		{"type":"ROW","children":[
			{"type":"IMAGE","url":"https://picsum.photos/seed/acme/200/100"},
			{"type":"IMAGE","url":"https://picsum.photos/seed/globex/200/100"}
		]}
	]'::jsonb),
	('info', 'menu.info', 'GUEST', '[
		{"type":"TEXT","source":{"kind":"REF","key":"menu.info"},"size":"LARGE"},
		{"type":"TEXT","source":{"kind":"REF","key":"info.wifi"},"size":"MEDIUM"},
		{"type":"TEXT","source":{"kind":"REF","key":"info.help"},"size":"SMALL"}
	]'::jsonb)
ON CONFLICT (id) DO UPDATE
	SET title_key = EXCLUDED.title_key, min_role = EXCLUDED.min_role, body = EXCLUDED.body;
