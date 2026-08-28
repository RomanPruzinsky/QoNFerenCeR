package tr.qonferencer.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.dtos.MealSecretDto
import tr.qonferencer.shared.dtos.MealSecretRequestDto
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.PageDto
import tr.qonferencer.shared.dtos.SlotProvisionedDto
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.dtos.UserDisplayDto

/** API connect to backend */
interface QoNFerenCeRApi {
	
	interface Splash {
		@GET(ApiPaths.Splash.ALL)
		suspend fun all(): SplashDto
	}
	
	interface User {
		@GET(ApiPaths.User.BY_ID)
		suspend fun byId(@Path("userId") userId: Long): UserDetailDto
		
		@GET(ApiPaths.User.BY_NAME)
		suspend fun searchByName(
			@Query("searchFor") searchFor: String,
			@Query("page") page: Int?,
		): PageDto<UserDisplayDto>
		
		@POST(ApiPaths.User.MEAL_SECRET)
		suspend fun mealSecret(@Body request: MealSecretRequestDto): MealSecretDto
	}
	
	interface CustomScreens {
		@GET(ApiPaths.CustomScreens.BY_ID)
		suspend fun customScreenBody(@Path("id") id: String): List<CustomElement>
	}
	
	interface Admin {
		@POST(ApiPaths.Admin.ADD_USER)
		suspend fun addUser(@Body request: ModifyableUserDataDto): SlotProvisionedDto
		
		@PUT(ApiPaths.Admin.UPDATE_USER)
		suspend fun updateUser(
			@Path("userId") userId: Long,
			@Body request: ModifyableUserDataDto,
		): UserDetailDto
		
		@POST(ApiPaths.Admin.LOGIN)
		suspend fun login(@Path("userId") userId: Long): LoginCredentialsDto
		
		@POST(ApiPaths.Admin.REVOKE)
		suspend fun revoke(@Path("userId") userId: Long): Response<Unit>
		
		@DELETE(ApiPaths.Admin.DELETE_USER)
		suspend fun deleteUser(@Path("userId") userId: Long): Response<Unit>
	}
	
	interface Meal {
		@POST(ApiPaths.Meal.MEAL_SCAN)
		suspend fun scan(@Body request: MealScanRequestDto): MealScanResultDto
	}
}

/** Retrofit-BackEnd client */
class QoNFerenCeRApiClient(
	retrofit: Retrofit,
) {
	val splash: QoNFerenCeRApi.Splash = retrofit.create()
	val user: QoNFerenCeRApi.User = retrofit.create()
	val customScreens: QoNFerenCeRApi.CustomScreens = retrofit.create()
	val admin: QoNFerenCeRApi.Admin = retrofit.create()
	val meal: QoNFerenCeRApi.Meal = retrofit.create()
	
	private companion object {
		private inline fun <reified T> Retrofit.create(): T = create(T::class.java)
	}
}
