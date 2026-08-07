package tr.qonferencer.trons.miscs

import kotlin.random.Random

/** Random generator */
val randomGenerator: Random = Random.Default

/**
 * Gets the next random `Int` from the random number generator in the specified range.
 *
 * Generates an `Int` random value uniformly distributed between the specified [from] (inclusive) and [until] (exclusive) bounds.
 *
 * @throws IllegalArgumentException if [from] is greater than or equal to [until].
 */
fun getRandomInt(from: Int, until: Int) = randomGenerator.nextInt(from, until)

/** Gets the next random [Boolean] value */
fun getRandomBool() = randomGenerator.nextBoolean()
