package smartdoc.dashboard.schedule

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


/**
 * Schedule server config properties
 *
 * @author Maple
 * @since 1.0.RELEASE
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "restdoc.schedule")
data
class ScheduleProperties(val port: Int)