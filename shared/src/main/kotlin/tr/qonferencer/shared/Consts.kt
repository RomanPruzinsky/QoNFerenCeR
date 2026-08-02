package tr.qonferencer.shared

const val DEFAULT_PAGING_SIZE = 20

/** Below this, a name search would just be an unfiltered browse of everyone */
const val MIN_QUERY_LENGTH = 2

/** I don't understand why any is not nullable if its any */
typealias LiterallyAny = Any?
typealias CustomDataType = Map<String, LiterallyAny>
