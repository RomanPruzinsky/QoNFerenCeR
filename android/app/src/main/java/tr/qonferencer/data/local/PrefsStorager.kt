package tr.qonferencer.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** Manages **storing** and **saving** SharedPreferences */
class PrefsStorager(
	context: Context,
) {
	private object PrefsFilenames {
		const val PLAIN = "QoNFerenCeRPrefsPlain"
		const val ENCRYPTED = "QoNFerenCeRPrefsEncrypted"
	}
	
	private val plainPrefs = context.getSharedPreferences(PrefsFilenames.PLAIN, Context.MODE_PRIVATE)
	
	private val encryptedPrefs = EncryptedSharedPreferences.create(
		context,
		PrefsFilenames.ENCRYPTED,
		MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
		EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
		EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
	)
	
	private fun prefsFrom(key: PrefKey): SharedPreferences = if (key.encrypted) encryptedPrefs else plainPrefs

/////////////////////// SPLIT //////////////////////
////////////////////////////////////////////////////
////////////////////// STRING //////////////////////
	
	fun getString(key: PrefKey): String? = prefsFrom(key).getString(key.name, null)
	
	fun putString(key: PrefKey, value: String) = prefsFrom(key).edit { putString(key.name, value) }

////////////////////// STRING //////////////////////
////////////////////////////////////////////////////
//////////////////////// INT ///////////////////////
	
	fun getInt(key: PrefKey): Int? {
		val prefs = prefsFrom(key)
		return if (prefs.contains(key.name)) prefs.getInt(key.name, 0) else null
	}
	
	fun putInt(key: PrefKey, value: Int) = prefsFrom(key).edit { putInt(key.name, value) }

//////////////////////// INT ///////////////////////
////////////////////////////////////////////////////
///////////////////// DELETE ///////////////////////
	
	fun remove(key: PrefKey) = prefsFrom(key).edit { remove(key.name) }
	
	fun clearPlain() = plainPrefs.edit { clear() }
	
	fun clearEncrypted() = encryptedPrefs.edit { clear() }

///////////////////// DELETE ///////////////////////
////////////////////////////////////////////////////
}
