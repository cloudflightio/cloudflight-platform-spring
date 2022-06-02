package io.cloudflight.platform.spring.context

/**
 * Use the constants in this class in conjunction with Spring's Environment profiles
 *
 * @see [org.springframework.context.annotation.Profile]
 * @see [org.springframework.test.context.ActiveProfiles]
 * @see [org.springframework.core.env.Environment]
 */
object ApplicationContextProfiles {

    /**
     * to be used for local development inside the IDE
     */
    const val DEVELOPMENT = "development"

    /**
     * Staging environment
     */
    const val STAGING = "staging"

    /**
     * Production environment
     */
    const val PRODUCTION = "production"

    /**
     * default profile to be used in spring application tests
     */
    const val TEST = "test"

    /**
     * To be used in test cases when using [org.testcontainers.Testcontainers], those tests run reasonabily slower
     * and it should be possible to not run them explicitely
     */
    const val TEST_CONTAINER = "testcontainer"
}