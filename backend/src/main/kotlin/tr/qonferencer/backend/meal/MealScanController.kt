package tr.qonferencer.backend.meal

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.MealCountDto
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto

@RestController
class MealScanController(
	private val scanService: MealScanService,
	private val countsService: MealCountsService,
) {
	@PostMapping(ApiPaths.Meal.MEAL_SCAN)
	fun scan(@RequestBody request: MealScanRequestDto): MealScanResultDto = scanService.scan(request)
	
	@GetMapping(ApiPaths.Meal.MEAL_COUNTS)
	fun counts(@PathVariable windowId: Long): List<MealCountDto> = countsService.counts(windowId)
}
