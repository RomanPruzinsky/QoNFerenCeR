package tr.qonferencer.trons.miscs

import android.content.Context

/**
 * Obtain version code from `build.gradle.kts:app`
 * @param c Context
 * @return Single number of version
 */
fun getVersionCode(c: Context) = c.packageManager.getPackageInfo(c.packageName, 0).longVersionCode.toInt()

/**
 * Obtain version name from `build.gradle.kts:app`
 * @param c Context
 * @return Version name or null
 */
fun getVersionName(c: Context): String? = c.packageManager.getPackageInfo(c.packageName, 0).versionName

/** @return Event id from `build.gradle.kts:app` applicationId (`tr.qonferencer.$eventId`) */
fun getEventId(c: Context): String = c.packageName.substringAfterLast('.')
