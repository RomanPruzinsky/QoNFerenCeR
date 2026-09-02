package tr.qonferencer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import tr.qonferencer.shared.dtos.CustomScreenDto

/** Icon shown when [CustomScreenDto.icon] key is unknown */
const val FALLBACK_ICON_KEY = "help"

/** All icons pickable for a custom screen */
val navIcons: Map<String, ImageVector> = mapOf(
	FALLBACK_ICON_KEY to Icons.AutoMirrored.Filled.Help,
	"home" to Icons.Default.Home,
	"settings" to Icons.Default.Settings,
	"info" to Icons.Default.Info,
	"map" to Icons.Default.Map,
	"restaurant" to Icons.Default.Restaurant,
	"event" to Icons.Default.Event,
	"group" to Icons.Default.Group,
	"chat" to Icons.AutoMirrored.Filled.Chat,
	"notifications" to Icons.Default.Notifications,
	"church" to Icons.Default.Church,
	"cloud" to Icons.Default.Cloud,
	"star" to Icons.Default.Star,
	"schedule" to Icons.Default.Schedule,
	"person" to Icons.Default.Person,
	"phone" to Icons.Default.Phone,
	"book" to Icons.Default.Book,
	"campaign" to Icons.Default.Campaign,
	"camera" to Icons.Default.CameraAlt,
	"music" to Icons.Default.MusicNote,
	"hotel" to Icons.Default.Hotel,
	"coffee" to Icons.Default.LocalCafe,
	"money" to Icons.Default.AttachMoney,
	"translate" to Icons.Default.Translate,
	"qr_code" to Icons.Default.QrCode,
	"wifi" to Icons.Default.Wifi,
	"car" to Icons.Default.DirectionsCar,
	"mic" to Icons.Default.Mic,
	"favorite" to Icons.Default.Favorite,
	"location" to Icons.Default.LocationOn,
)

/** Resolves icon [key] with [FALLBACK_ICON_KEY] as fallback */
fun iconFrom(key: String): ImageVector = navIcons[key] ?: navIcons.getValue(FALLBACK_ICON_KEY)
