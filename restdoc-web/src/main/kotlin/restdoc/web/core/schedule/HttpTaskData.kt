package restdoc.web.core.schedule

@Deprecated(message = "")
data class HttpTaskData(val status: Int, val httpResponseHeader: MutableMap<String, String>,
                        val value: Any?, val taskId: String
)