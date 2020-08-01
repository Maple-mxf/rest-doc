package restdoc.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@Deprecated("")
@ConfigurationProperties(prefix = "restdoc")
class RestDocProperties{
    lateinit var workDirOfClasspath: String
    lateinit var metaDirClasspath: String
}