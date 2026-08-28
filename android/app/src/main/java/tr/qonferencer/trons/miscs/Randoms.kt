package tr.qonferencer.trons.miscs

import kotlin.random.Random

/** Random generator */
val randomGenerator: Random = Random.Default

/**
 * Gets next random `Int` from random number generator in specified range.
 *
 * Generates `Int` random value uniformly distributed between specified [from] (inclusive) and [until] (exclusive) bounds.
 *
 * @throws IllegalArgumentException if [from] is greater than or equal to [until].
 */
fun getRandomInt(
	from: Int,
	until: Int,
) = randomGenerator.nextInt(from, until)

/** Gets next random [Boolean] value */
fun getRandomBool() = randomGenerator.nextBoolean()
