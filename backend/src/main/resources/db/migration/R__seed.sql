-- Repeatable dev seed: languages + demo content so the app has something to render.
-- Idempotent (re-runs whenever this file's checksum changes).

INSERT INTO language (code, name, is_default) VALUES
	('en', 'English', true),
	('sk', 'Slovenčina', false)
ON CONFLICT (code) DO NOTHING;

-- Keys dropped from docs/translations.md — purge on every repeatable-migration re-run,
-- since INSERT ... ON CONFLICT below can't remove rows a prior run already seeded.
DELETE FROM translation WHERE key IN (
	'screen.home.title', 'screen.agenda.title', 'settings.appColors.intro', 'settings.language.intro',
	'home.welcome', 'login.useManual', 'login.useCamera',
	'login.method.qr', 'login.method.nfc', 'login.method.manual',
	'login.intro', 'login.byQr', 'login.byNfc', 'login.byManual',
	'login.cameraDenied', 'login.grantCamera', 'login.scanningNfc',
	'login.username', 'login.password', 'login.submit',
	'login.by.qr', 'login.by.nfc', 'login.by.manual',
	'userCheck.by.qr', 'userCheck.by.nfc', 'userCheck.by.manual',
	'user.detail.canCheckByName'
);

-- Kept in sync with docs/translations.md — edit that file, then reapply here.
INSERT INTO translation (key, lang_code, text) VALUES
	('destination.custom.home',   'en', 'Home'),
	('destination.custom.agenda', 'en', 'Agenda'),
	('meal.lunch1.name',     'en', 'Lunch — Day 1'),
	('meal.dinner1.name',    'en', 'Dinner — Day 1'),
	('meal.breakfast2.name', 'en', 'Breakfast — Day 2'),
	('meal.lunch2.name',     'en', 'Lunch — Day 2'),
	('meal.dinner2.name',    'en', 'Dinner — Day 2'),
	('meal.variant.standard', 'en', 'Standard'),
	('settings.appColors',  'en', 'App colors'),
	('settings.language',   'en', 'Language'),
	('settings.fontFamily', 'en', 'Font family'),
	('settings.fontSize',   'en', 'Text size'),
	('user.detail.logout',  'en', 'Logout'),
	-- app chrome (system labels — also translation-keyed, not compiled resources)
	('destination.home',      'en', 'Home'),
	('destination.login',     'en', 'Login'),
	('destination.settings',  'en', 'Settings'),
	('destination.myProfile', 'en', 'My profile'),
	('destination.aboutApp',  'en', 'About App'),
	('app.name',            'en', 'QoNFerenCeR demo'),
	('aboutApp.licence',    'en', 'licences'),
	('aboutApp.github',     'en', 'Github'),
	('aboutApp.appVersion', 'en', 'version'),
	('aboutApp.developerContact', 'en', 'Contact to developer'),
	('misc.copied',               'en', 'copied'),
	('misc.cannotBeEmpty',        'en', 'can''t be empty'),
	('login.by.intro',        'en', 'login with'),
	('login.back',            'en', 'back'),
	('login.state.cameraDenied', 'en', 'no camera permission'),
	('login.state.grantCamera',  'en', 'allow camera'),
	('login.nfc.scanning',    'en', 'scanning NFC'),
	('login.manual.username', 'en', 'username'),
	('login.manual.password', 'en', 'password'),
	('login.manual.submit',   'en', 'submit'),
	('user.detail.role',            'en', 'role'),
	('user.detail.userId',          'en', 'user id'),
	('user.detail.isSpeaker',       'en', 'guest'),
	('user.detail.canCheckUsers',   'en', 'can check others'),
	('user.detail.emittingNfc',     'en', 'emitting NFC'),
	('user.detail.mealsIntro',      'en', 'ordered meals'),
	('destination.userCheck',        'en', 'User check'),
	('userCheck.by.intro',           'en', 'check user with'),
	('keyInputMethod.qr_bar',        'en', 'QR/Bar code'),
	('keyInputMethod.nfc',           'en', 'NFC'),
	('keyInputMethod.manual',        'en', 'manual'),
	('userCheck.manual.searchLabel', 'en', 'search by name'),
	('userCheck.manual.submit',      'en', 'submit'),
	('userCheck.detail.fullName',    'en', 'full name'),
	('userCheck.detail.save',        'en', 'save'),
	-- sk
	('destination.custom.home',   'sk', 'Domov'),
	('destination.custom.agenda', 'sk', 'Program'),
	('meal.lunch1.name',     'sk', 'Obed — deň 1'),
	('meal.dinner1.name',    'sk', 'Večera — deň 1'),
	('meal.breakfast2.name', 'sk', 'Raňajky — deň 2'),
	('meal.lunch2.name',     'sk', 'Obed — deň 2'),
	('meal.dinner2.name',    'sk', 'Večera — deň 2'),
	('meal.variant.standard', 'sk', 'Štandardná'),
	('settings.appColors',  'sk', 'Farby aplikácie'),
	('settings.language',   'sk', 'Jazyk'),
	('settings.fontFamily', 'sk', 'Rodina písma'),
	('settings.fontSize',   'sk', 'Veľkosť textu'),
	('user.detail.logout',  'sk', 'Odhlásiť sa'),
	('destination.home',      'sk', 'Domov'),
	('destination.login',     'sk', 'Prihlásenie'),
	('destination.settings',  'sk', 'Nastavenia'),
	('destination.myProfile', 'sk', 'Môj profil'),
	('destination.aboutApp',  'sk', 'O aplikácii'),
	('app.name',            'sk', 'QoNFerenCeR demo'),
	('aboutApp.licence',    'sk', 'licencie'),
	('aboutApp.github',     'sk', 'Github'),
	('aboutApp.appVersion', 'sk', 'verzia'),
	('aboutApp.developerContact', 'sk', 'Kontakt na vývojára'),
	('misc.copied',               'sk', 'skopírované'),
	('misc.cannotBeEmpty',        'sk', 'nemôže ostať prázdne'),
	('login.by.intro',        'sk', 'prihlásiť sa cez'),
	('login.back',            'sk', 'späť'),
	('login.state.cameraDenied', 'sk', 'chýba povolenie kamery'),
	('login.state.grantCamera',  'sk', 'povoliť kameru'),
	('login.nfc.scanning',    'sk', 'skenovanie NFC'),
	('login.manual.username', 'sk', 'používateľské meno'),
	('login.manual.password', 'sk', 'heslo'),
	('login.manual.submit',   'sk', 'potvrdiť'),
	('user.detail.role',            'sk', 'rola'),
	('user.detail.userId',          'sk', 'ID používateľa'),
	('user.detail.isSpeaker',       'sk', 'hosť'),
	('user.detail.canCheckUsers',   'sk', 'môže kontrolovať ostatných'),
	('user.detail.emittingNfc',     'sk', 'vysiela NFC'),
	('user.detail.mealsIntro',      'sk', 'objednané jedlá'),
	('destination.userCheck',        'sk', 'Kontrola používateľa'),
	('userCheck.by.intro',           'sk', 'skontrolovať používateľa cez'),
	('keyInputMethod.qr_bar',        'sk', 'QR/Čiarový kód'),
	('keyInputMethod.nfc',           'sk', 'NFC'),
	('keyInputMethod.manual',        'sk', 'ručne'),
	('userCheck.manual.searchLabel', 'sk', 'hľadať podľa mena'),
	('userCheck.manual.submit',      'sk', 'potvrdiť'),
	('userCheck.detail.fullName',    'sk', 'celé meno'),
	('userCheck.detail.save',        'sk', 'uložiť')
ON CONFLICT (key, lang_code) DO UPDATE SET text = EXCLUDED.text;

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
	('home', 'destination.custom.home', 'ANONYM', 'home', '[
		{"type":"TEXT","source":{"kind":"REF","key":"destination.custom.home"},"size":"LARGE"}
	]'::jsonb),
	('agenda', 'destination.custom.agenda', 'VISITOR', 'schedule', '[
		{"type":"TEXT","source":{"kind":"REF","key":"destination.custom.agenda"},"size":"MEDIUM"}
	]'::jsonb)
ON CONFLICT (id) DO UPDATE SET
	title_key = EXCLUDED.title_key,
	min_role  = EXCLUDED.min_role,
	icon      = EXCLUDED.icon,
	body      = EXCLUDED.body;
