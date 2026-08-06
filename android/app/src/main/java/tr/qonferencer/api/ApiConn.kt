package tr.qonferencer.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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

private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
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

/** Bearer token manager */
private val authedClient by lazy {
	OkHttpClient.Builder()
		.addInterceptor(AuthInterceptor(authTokenHelper, keycloakApi))
		.addInterceptor(logger)
		.build()
}

//////////////////// HELPERS /////////////////////
//////////////////////////////////////////////////
/////////////////// API ACCESS ///////////////////

val authRepository: AuthRepository by lazy { AuthRepository(keycloakApi, authTokenHelper) }

val QoNFerenCerApi: QoNFerenCeRApiClient by lazy {
	QoNFerenCeRApiClient(buildRetrofit(BuildConfig.BACKEND_BASE_URL, authedClient))
}

/////////////////// API ACCESS ///////////////////
//////////////////////////////////////////////////
