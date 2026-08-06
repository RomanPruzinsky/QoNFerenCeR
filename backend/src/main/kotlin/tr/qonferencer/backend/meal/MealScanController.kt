package tr.qonferencer.backend.meal

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto

@RestController
class MealScanController(
	private val scanService: MealScanService,
) {
	@PostMapping(ApiPaths.Meal.MEAL_SCAN)
	fun scan(@RequestBody request: MealScanRequestDto): MealScanResultDto = scanService.scan(request)
}
