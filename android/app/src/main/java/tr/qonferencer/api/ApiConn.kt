package tr.qonferencer.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import tr.qonferencer.BuildConfig
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.api.auth.AuthInterceptor
import tr.qonferencer.api.auth.AuthRepository
import tr.qonferencer.api.auth.AuthTokenHelper
import tr.qonferencer.data.remote.KeycloakApi
import tr.qonferencer.data.remote.QoNFerenCeRApiClient
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import java.io.File

//////////////////////////////////////////////////
//////////////////// HELPERS /////////////////////

/** Jackson's (de)serializer */
private val objectMapper: ObjectMapper = ObjectMapper()
	.registerKotlinModule() // Let Jackson know its kotlin (not java)
	.registerModule(JavaTimeModule()) // Support for java.time types
	.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unknown

/** Logging requests/responses */
private val logger = HttpLoggingInterceptor().apply {
	level =
		if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
		else HttpLoggingInterceptor.Level.NONE
}

private fun buildRetrofit(
	baseUrl: String,
	client: OkHttpClient,
): Retrofit = Retrofit.Builder()
	.baseUrl(baseUrl)
	.client(client)
	.addConverterFactory(JacksonConverterFactory.create(objectMapper))
	.build()

/** Keycloak calls */
private val keycloakApi: KeycloakApi = buildRetrofit(
	BuildConfig.KEYCLOAK_BASE_URL,
	OkHttpClient.Builder().addInterceptor(logger).build(),
).create(KeycloakApi::class.java)

/** Calls for **auth tokens** */
private val authTokenHelper by lazy { AuthTokenHelper(QoNFerenCeRApp.tokenStore) }

/** MB reserved for ETag */
private const val HTTP_CACHE_MAX_FILE_SIZE = 5L * 1024 * 1024

/** Bearer token manager */
private val authedClient by lazy {
	OkHttpClient.Builder()
		.cache(Cache(File(QoNFerenCeRApp.appContext.cacheDir, "http"), HTTP_CACHE_MAX_FILE_SIZE))
		.addInterceptor(AuthInterceptor(authTokenHelper, keycloakApi))
		.addInterceptor(logger)
		.build()
}

//////////////////// HELPERS /////////////////////
//////////////////////////////////////////////////
/////////////////// API ACCESS ///////////////////

val authRepository: AuthRepository by lazy { AuthRepository(keycloakApi, QoNFerenCerApi.user, authTokenHelper) }

val QoNFerenCerApi: QoNFerenCeRApiClient by lazy {
	QoNFerenCeRApiClient(buildRetrofit(BuildConfig.BACKEND_BASE_URL, authedClient))
}

/** @return Decoded login-QR/NFC payload, or `null` if [json] isn't a valid [LoginCredentialsDto] */
fun parseLoginCredentials(json: String): LoginCredentialsDto? =
	runCatching { objectMapper.readValue<LoginCredentialsDto>(json) }.getOrNull()

/////////////////// API ACCESS ///////////////////
//////////////////////////////////////////////////
