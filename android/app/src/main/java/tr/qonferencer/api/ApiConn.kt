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
import tr.qonferencer.auth.AuthInterceptor
import tr.qonferencer.auth.AuthRepository
import tr.qonferencer.auth.AuthTokenHelper
import tr.qonferencer.data.remote.KeycloakApi
import tr.qonferencer.data.remote.QoNFerenCeRApiClient

/** Plain client (no bearer) hits Keycloak; authed client adds [AuthInterceptor] for the backend. */
private val objectMapper: ObjectMapper = ObjectMapper()
	.registerKotlinModule()
	.registerModule(JavaTimeModule())
	.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

private val loggingInterceptor = HttpLoggingInterceptor().apply {
	level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
}

val keycloakApi: KeycloakApi = Retrofit.Builder()
	.baseUrl(BuildConfig.KEYCLOAK_BASE_URL)
	.client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
	.addConverterFactory(JacksonConverterFactory.create(objectMapper))
	.build()
	.create(KeycloakApi::class.java)

// lazy: QoNFerenCeRApp.tokenStore only exists once Application.onCreate() has run.
private val authTokenHelper by lazy { AuthTokenHelper(QoNFerenCeRApp.tokenStore) }

val authRepository: AuthRepository by lazy { AuthRepository(keycloakApi, authTokenHelper) }

private val authedClient by lazy {
	OkHttpClient.Builder()
		.addInterceptor(AuthInterceptor(authTokenHelper, keycloakApi))
		.addInterceptor(loggingInterceptor)
		.build()
}

val QoNFerenCerApi: QoNFerenCeRApiClient by lazy {
	QoNFerenCeRApiClient(
		Retrofit.Builder()
			.baseUrl(BuildConfig.BACKEND_BASE_URL)
			.client(authedClient)
			.addConverterFactory(JacksonConverterFactory.create(objectMapper))
			.build(),
	)
}
