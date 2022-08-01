package io.cloudflight.platform.spring.test.archunit

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchTests
import io.cloudflight.cleancode.archunit.CleanCodeRuleSets

abstract class AbstractCleanCodeTest {
    @ArchTest
    val cleancode = ArchTests.`in`(CleanCodeRuleSets::class.java)
}