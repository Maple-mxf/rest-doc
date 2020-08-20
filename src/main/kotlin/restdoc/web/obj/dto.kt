package restdoc.web.obj

data class CreateProjectDto(val name: String, val desc: String)
data class UpdateProjectDto(val id: String, val name: String, val desc: String)