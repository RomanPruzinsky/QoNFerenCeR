package tr.qonferencer.api.auth

import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager

class AuthTokenHelper(
	private val prefs: PrefsStorager,
) {
////////////////////////////////////////////////////
//////////////////////// GET ////////////////////////
	
	fun refreshToken(): String? = prefs.getString(PrefKey.REFRESH_TOKEN)
	
	fun accessToken(): String? = prefs.getString(PrefKey.ACCESS_TOKEN)
	
	fun isLoggedIn(): Boolean = accessToken() != null
	
	fun mealSecret(): String? = prefs.getString(PrefKey.MEAL_SECRET)

//////////////////////// GET ////////////////////////
////////////////////////////////////////////////////
/////////////////////// MODIFY //////////////////////
	
	fun updateTokens(
		access: String,
		refresh: String?,
	) {
		prefs.putString(PrefKey.ACCESS_TOKEN, access)
		
		if (refresh != null) prefs.putString(PrefKey.REFRESH_TOKEN, refresh)
		else prefs.remove(PrefKey.REFRESH_TOKEN)
	}
	
	fun updateMealSecret(secret: String) = prefs.putString(PrefKey.MEAL_SECRET, secret)
	
	fun clearTokens() = prefs.clearEncrypted()

/////////////////////// MODIFY //////////////////////
////////////////////////////////////////////////////
}
