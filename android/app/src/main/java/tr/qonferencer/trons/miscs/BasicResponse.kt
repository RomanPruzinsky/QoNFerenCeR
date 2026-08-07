package tr.qonferencer.trons.miscs

/**
 * Default response for info requests
 * @property SUCCESS All good
 * @property FAILURE Not good
 * @property ERROR Something went wrong
 */
enum class BasicResponse {
	SUCCESS,
	FAILURE,
	ERROR,
	;

	/** Whether [BasicResponse] is [SUCCESS] */
	fun isSucc(): Boolean = (this == SUCCESS)

	/** Whether [BasicResponse] is [FAILURE] */
	fun isFail(): Boolean = (this == FAILURE)

	/** Whether [BasicResponse] is [ERROR] */
	fun isEror(): Boolean = (this == ERROR)

	/**
	 * Whether [BasicResponse] is [SUCCESS] or else
	 * @receiver [BasicResponse] to compare
	 * @return `true` if [BasicResponse] is [SUCCESS], `false` otherwise
	 */
	fun BasicResponse.isBool(): Boolean = this.isSucc()

	/** [SUCCESS] response */
	fun succResponse() = SUCCESS

	/** [FAILURE] response */
	fun failResponse() = FAILURE

	/** [ERROR] response */
	fun erorResponse() = ERROR

	/**
	 * [SUCCESS] or [FAILURE] response, depends on [cond]
	 * @param cond Condition to process
	 * @return [SUCCESS] if [cond] is `true`, [FAILURE] otherwise
	 */
	fun boolResponse(cond: () -> Boolean) = if (cond()) succResponse() else failResponse()

	/**
	 * Executes [action] and returns [SUCCESS] or [ERROR] in case of exception.
	 *
	 * Also prints stackTrace.
	 * @param action Action to execute and catch
	 * @return [SUCCESS] if [action] executed successfully, [ERROR] otherwise
	 */
	inline fun safeBasicResponse(action: () -> Unit): BasicResponse = try {
		action()
		succResponse()
	} catch (e: Exception) {
		e.printStackTrace()
		erorResponse()
	}
}
