package io.cloudflight.platform.spring.test.archunit

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses

@AnalyzeClasses(packagesOf = [ArchitectureTest::class], importOptions = [DoNotIncludeTests::class])
class ArchitectureTest : AbstractCleanCodeTest() {
    // your ArchUnit tests go here
}