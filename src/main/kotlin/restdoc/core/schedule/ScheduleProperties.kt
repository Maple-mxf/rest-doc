package restdoc.core.schedule

import org.springframework.boot.context.properties.ConfigurationProperties


/**
 * Schedule server config properties
 *
 * @author ubuntu-m
 */
@ConfigurationProperties(prefix = "restdoc.schedule")
data
class ScheduleProperties(val port: Int)