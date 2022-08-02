package io.cloudflight.platform.spring.test.archunit

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchTests
import io.cloudflight.cleancode.archunit.CleanCodeRuleSets

/**
 * Extend from this test case and create your own ArchitectureTest
 * in order to automatically get all rules from https://github.com/cloudflightio/archunit-cleancode-verifier
 */
abstract class AbstractCleanCodeTest {
    @ArchTest
    val cleancode = ArchTests.`in`(CleanCodeRuleSets::class.java)
}