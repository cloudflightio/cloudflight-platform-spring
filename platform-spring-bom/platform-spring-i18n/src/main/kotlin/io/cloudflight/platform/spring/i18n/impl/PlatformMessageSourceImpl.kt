package io.cloudflight.platform.spring.i18n.impl

import io.cloudflight.platform.spring.i18n.ListResourceBundleMessageSource
import org.slf4j.LoggerFactory
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.IOException
import java.util.*


internal class PlatformMessageSourceImpl : ReloadableResourceBundleMessageSource(), ListResourceBundleMessageSource {

    private val resourcePatternResolver = PathMatchingResourcePatternResolver()

    override fun getAllMessages(locale: Locale): Map<String, String> {
        val propertiesHolder = getMergedProperties(locale)
        var result = propertiesToStringMap(propertiesHolder.properties)

        val parentListMessageSource = parentMessageSource as? ListResourceBundleMessageSource
        if (parentListMessageSource != null) {
            val parentMessages = parentListMessageSource.getAllMessages(locale)
            result = parentMessages.plus(result)
        }

        return result
    }

    override fun refreshProperties(filename: String, propHolder: PropertiesHolder?): PropertiesHolder {
        if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
            return refreshClassPathProperties(filename, propHolder);
        } else {
            return super.refreshProperties(filename, propHolder);
        }
    }

    private fun refreshClassPathProperties(filename: String, propHolder: PropertiesHolder?): PropertiesHolder {
        val properties = Properties()
        var lastModified: Long = -1
        try {
            val resources = resourcePatternResolver.getResources(filename + PROPERTIES_SUFFIX)
            for (resource in resources) {
                val sourcePath = resource.getURI()
                    .toString()
                    .replace(PROPERTIES_SUFFIX, "")
                val holder = super.refreshProperties(sourcePath, propHolder)
                properties.putAll(propertiesToStringMap(holder.properties))
                if (lastModified < resource.lastModified()) {
                    lastModified = resource.lastModified()
                }
            }
        } catch (e: IOException) {
            LOG.warn("Failed to load property file", e)
        }

        return PropertiesHolder(properties, lastModified)
    }

    private fun propertiesToStringMap(properties: Properties?): Map<String, String> {
        return requireNotNull(properties) { "No properties defined" }
            .map { it.key.toString() to it.value.toString() }
            .toMap()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PlatformMessageSourceImpl::class.java)
        private const val PROPERTIES_SUFFIX = ".properties"
    }
}
