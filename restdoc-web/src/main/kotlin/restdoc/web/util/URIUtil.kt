package restdoc.web.util

val regex = Regex("^[\\{][a-zA-Z]+[0-9a-zA-Z]*[\\}]$")

fun uriVariables(path: String): List<String> {
    val snippet = path.split("/")

    val uriVariableFields = snippet.filter { regex.matches(it) }
            .map { it.replaceFirst("{", "") }
            .map { it.replace("}", "") }

    return uriVariableFields
}