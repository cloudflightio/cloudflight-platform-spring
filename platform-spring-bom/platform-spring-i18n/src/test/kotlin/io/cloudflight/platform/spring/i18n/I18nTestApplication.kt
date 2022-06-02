package io.cloudflight.platform.spring.i18n

import io.cloudflight.platform.spring.i18n.autoconfigure.PlatformI18nAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackageClasses = [PlatformI18nAutoConfiguration::class])
class I18nTestApplication
