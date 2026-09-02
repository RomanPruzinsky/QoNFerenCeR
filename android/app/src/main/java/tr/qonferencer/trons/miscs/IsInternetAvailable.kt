package tr.qonferencer.trons.miscs

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission

/**
 * Check if internet is available
 * @param ctx Context
 * @return True if internet is available, false otherwise
 */
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isInternetAvailable(ctx: Context): Boolean {
	val connManager =
		ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
	val activeNetwork = connManager.activeNetwork ?: return false
	val capabilities = connManager.getNetworkCapabilities(activeNetwork) ?: return false

	return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
