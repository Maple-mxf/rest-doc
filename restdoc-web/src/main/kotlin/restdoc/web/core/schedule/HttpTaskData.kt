package restdoc.web.core.schedule

data class HttpTaskData(val status: Int, val httpResponseHeader: MutableMap<String, Any>,
                        val value: Any?, val taskId: String
)