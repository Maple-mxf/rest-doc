package restdoc.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "restdoc")
class RestDocProperties{
    lateinit var workDirOfClasspath: String
    lateinit var metaDirClasspath: String
}