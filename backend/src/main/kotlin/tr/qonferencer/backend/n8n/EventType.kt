package tr.qonferencer.backend.n8n

/** Catalog of outbound events; the name is also the webhook path segment, additive only */
enum class EventType {
	/** Somebody opened the app and fetched the splash */
	APP_LAUNCHED,

	/** A slot was provisioned (Keycloak user + app anchor) */
	SLOT_CREATED,

	/** A fresh password was issued for a slot; the password itself is never sent */
	SLOT_LOGIN_ISSUED,

	/** An attendee's details, role or meals changed */
	SLOT_UPDATED,

	/** A lost phone was cut off: scan secret rotated and sessions killed */
	SLOT_REVOKED,

	/** An attendee was erased; nothing of theirs survives to be reported later */
	SLOT_DELETED,

	/** A meal was handed out */
	MEAL_APPROVED,

	/** A scan was refused; `data.reason` carries which verdict */
	MEAL_DENIED,
}
