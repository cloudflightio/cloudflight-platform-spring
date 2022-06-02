package io.cloudflight.platform.spring.server

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.context.ApplicationListener
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PlatformServerModuleStartupListener(
    @Autowired(required = false) private val gitProperties: GitProperties?,
    @Autowired(required = false) private val buildProperties: BuildProperties?,
    environment: Environment
) : ServerModuleIdentification, ApplicationListener<ApplicationStartedEvent> {

    private val group: String
    private val id: String
    private val idShort: String?
    private val time: Instant?
    private val name: String
    private val version: String

    init {
        if (gitProperties != null && buildProperties != null) {
            group = buildProperties.group
            id = gitProperties.commitId
            idShort = gitProperties.shortCommitId
            time = gitProperties.commitTime
            name = buildProperties.name
            version = buildProperties.version
        } else {
            val devProperties = ClassPathResource("development.properties")
            if (devProperties.exists()) {
                val properties = PropertiesLoaderUtils.loadProperties(devProperties)
                group = properties.getProperty("development.group")
                name = properties.getProperty("development.name")
                version = properties.getProperty("development.version")
                id = UUID.randomUUID().toString()
                idShort = null
                time = null

                val acceptableDevProfiles = Profiles.of(
                    ApplicationContextProfiles.DEVELOPMENT,
                    ApplicationContextProfiles.TEST,
                    ApplicationContextProfiles.TEST_CONTAINER
                )
                if (!environment.acceptsProfiles(acceptableDevProfiles)) {
                    LOG.warn("development.properties exists but none of the profiles $acceptableDevProfiles is applied.")
                }

            } else {
                LOG.warn("Neither GitProperties & BuildProperties nor development.properties are available. " +
                        "Are you using the Cloudflight Gradle Plugin in a -server module?")
                group = buildProperties?.group ?: "unknown"
                id = gitProperties?.commitId ?: "unknown"
                idShort = gitProperties?.shortCommitId
                time = gitProperties?.commitTime
                name = buildProperties?.name ?: "unknown"
                version = buildProperties?.version ?: "unknown"
            }
        }
    }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        LOG.info("Module $group:$name started up with version $version and id $id")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PlatformServerModuleStartupListener::class.java)
    }

    override fun getGroup(): String {
        return this.group
    }

    override fun getName(): String {
        return this.name
    }

    override fun getId(): String {
        return this.id
    }

    override fun getIdShort(): String? =
        this.idShort

    override fun getTime(): Instant? =
        this.time

    override fun getVersion(): String {
        return this.version
    }
}

