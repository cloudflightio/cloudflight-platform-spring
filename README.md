# Cloudflight Platform for Spring Boot

[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.cloudflight.platform.spring/platform-spring-bom.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.cloudflight.platform.spring/platform-spring-bom)

## Purpose

The Cloudflight Platform serves as foundation for all Cloudflight custom software projects running on the JVM. This contains
3 major parts:

- Unified **Dependency Management** on top of Spring Boot and Spring Cloud
- **Utility Modules** for cross-cutting-concerns like monitoring, JPA access, Elastic Search and much more that can (and should) be embedded into your production code

## Dependency Management & Usage

The Cloudflight Platform comes with two BOM packages (bill-of-materials) that provide dependency management for all
platform artifacts as well as third party libraries, one of them for application code, the other one for test-code.

You can utilize Gradle's dependency management and add both BOMs as platform-dependencies to your root project:

````groovy
dependencies {
    api platform("io.cloudflight.platform.spring:platform-spring-bom:$cloudflightPlatformVersion")
    testImplementation platform("io.cloudflight.platform.spring:platform-spring-test-bom:$cloudflightPlatformVersion")
}
````

While `platform-bom` only provides dependency constraints, `platform-bom-test` also puts the following libaries to
the `testImplementation` classpath of **all** submodules of your project:

* [JUnit 5](https://junit.org/junit5/) (including [JUnit-Params](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests))
* [Mockk](https://mockk.io/)
* [AssertJ](https://assertj.github.io/doc/)

That means you do not need to add them on your own. The Cloudflight Platform handles that for you.

Why exactly those? Because [not only we think that those are really valuable testing libraries](https://phauer.com/2018/best-practices-unit-testing-kotlin/).

In conjunction with the [AutoConfigure Gradle Plugin](https://github.com/cloudflightio/autoconfigure-gradle-plugin), you 
might add code like that to your root `build.gradle`:

````groovy
subprojects { proj ->
    dependencies {
        api platform("io.cloudflight.platform.spring:platform-spring-bom:$cloudflightPlatformVersion")
        annotationProcessor platform("io.cloudflight.platform.spring:platform-spring-bom:$cloudflightPlatformVersion")
        if (proj.plugins.hasPlugin(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper)) {
            kapt platform("io.cloudflight.platform.spring:platform-spring-bom:$cloudflightPlatformVersion")
        }
        testImplementation platform("io.cloudflight.platform.spring:platform-spring-test-bom:$cloudflightPlatformVersion")
    }
}
````

By adding the platform to your gradle root file, you can add any other submodule of the platform without entering the version number:

````groovy
dependencies {
    testImplementation('io.cloudflight.platform.spring:platform-spring-jpa-test')
}
````

## Production modules

These modules are meant to be embedded into your production code, either as api/implementation or as test dependency. Find more details
about the modules on the according subpages.

Each module can be added to your code like that:

````groovy
dependencies {
    implementation('io.cloudflight.platform.spring:%MODULE_NAME%')
}
````

i.e.

````groovy
dependencies {
    implementation('io.cloudflight.platform.spring:platform-spring-profiling')
}
````

### Server Configuration

Whenever you have a module in your code-base that fires up a Spring Boot Server (typically modules with the suffix
`-server`), then add the module `io.cloudflight.platform.spring:platform-spring-server-config` to your `implementation` classpath.

#### Server module identification

It not only adds other required modules for monitoring and logging config but also provides the interface
`ServerModuleIdentification` as a Spring Bean which can be injected to your service. It provides you:

* the name of the server
* the current version (as provided from the build pipeline)
* the underyling Git Hash.

#### Startup time analysis

When Spring Boot applications get bigger, startup time often decreases. In order to have more insights on what is going on,
the module `platform-server-config` comes with a utility which prints a detailled analysis of all startup phases of your
`ApplicationContext`.

To enable that, you need to do two things:

1. Set the logger `io.cloudflight.platform.spring.server.ApplicationStartupPrinter` to `TRACE`
2. Set a `BufferingApplicationStartup` to your `SpringBootApplication` as described in the [official docs](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-application-startup-tracking).

````java
public static void main(String[] args) {
    SpringApplication app = new SpringApplication(MySpringConfiguration.class);
    app.setApplicationStartup(new BufferingApplicationStartup(2048));
    app.run(args);
}
````

The log itself gets quite huge and broad and would not fit into this documentation, but if you're interested, just try it out.

If you are running integration tests with `@SpringBootTest`, you don't need to set this bean manually. All you need to do is to ensure you have `platform-test` on your classpath.

### Environment

The module `io.cloudflight.platform.spring:platform-spring-context` provides the object `ApplicationContextProfiles` which comes with
constants for our default [Spring profile](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-definition-profiles) names that will be put into the environment.

The profile names to be used then (also in `application.yaml` files) are the following:


| Profile name                                                          | Description                                                                                                                                                                     | 
|---------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `development` | to be used for local development inside the IDE                                                                                                                                 |
| `staging` | staging environment                                                                                                                                                             |
| `production` | production environment                                                                                                                                                          |
| `test` | default profile to be used in Spring Application tests                                                                                                                          |
| `testcontainer` | To be used in test cases when using [TestContainers](https://www.testcontainers.org/), those tests run reasonabily slower and it should be possible to not run them explicitely |


Whenever you are accessing one of those profile names from within the code (i.e. in a `@Profile` annotation), use the according contstant
in `ApplicationContextProfiles`. Application configuration files need to be suffixed with the strings mentioned above (i.e. `application-development.yaml`).


#### Monitoring Config

Monitoring via Spring Boot Actuator and Prometheus will be activated automatically with the module
`io.cloudflight.platform.spring:platform-spring-monitoring` (which comes together with `platform-server-config` as mentioned above).

Important thing to know here is that it automatically sets the port to listen for actuator requests to `server.port + 10000`.
That means, if your server is running on port 8080, you will find the actuator endpoints on 18080. We are doing this
to provide a clean and easy-to-manage security concept for these endpoints in production as we can simply restrict
accessing this port from outside and don't need to deal with Spring Security in parallel.

#### Logging Config

The module `io.cloudflight.platform.spring:platform-spring-logging-server-config` (which is also being transitively loaded with)
`platform-server-config` comes with a basic configuration for Logback, especially also preparing our logging mechanism
for the usage of the ELK stack on production.

Use this file as reference in your `logback-spring.xml` files as follows:

````xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <include resource="io/cloudflight/platform/spring/logging/clf-base.xml" />

  <logger name="io.cloudflight" level="INFO"/>

  <root level="WARN"/>

</configuration>
````

### Logging

The module `platform-logging` wraps [Slf4J](http://slf4.org) and [Kotlin Logging](https://github.com/MicroUtils/kotlin-logging) and also provides some annotations for more convenient
access for [MDC](https://www.baeldung.com/mdc-in-log4j-2-logback) values. MDC gives you the possibility to append
structured information to your log output which you can then easily filter and search in Kibana, see [this blog entry](https://www.baeldung.com/mdc-in-log4j-2-logback#mdc-in-slf4jlogback)
for more information.

#### LogParam

While this is a great thing, coding is a bit verbose as you need to manually take care to clear the MDC context after your method call.
The code example from the above linked blog entry is not fully correct, as you also need to catch exceptions properly and clean up
in a `finally` block. That means, the correct usage would be (here now in kotlin code):

````kotlin
fun sayHello(name: String) {
    try {
        MDC.put("name", name)
        LOG.info("We are calling hello now")
    } finally {
        MDC.remove("name")
    }
}
````

With `platform-spring-logging`, you get the annotation `io.cloudflight.platform.logging.annotation.LogParam` which you can append on method parameters. The
following code does exactly the same:

````kotlin
fun sayHello(@LogParam name: String) {
    LOG.info("We are calling hello now")
}
````

:warning: We are using Spring AOP here, that means this only works for public methods of Spring beans (like any other Spring-related annotation like `@Transactional`).

The annotation `@LogParam` can also be customized and chained, here are some examples:

````kotlin
@Service
class MySpringBean {

    fun sayHello(@LogParam name: String) {
        // calls MDC.put("name", name)
    }

    fun sayHelloWithNamedParameter(@LogParam(name = "myName") name: String) {
        // calls MDC.put("myName", name)
    }

    fun sayHelloWithField(@LogParam(field = "firstName") person: Person) {
        // calls MDC.put("person.firstName", person.firstName)
    }

    fun sayHelloWithFieldAndName(@LogParam(field = "firstName", name = "myFirstName") person: Person) {
        // calls MDC.put("myFirstName", person.firstName)
    }

    fun sayHelloWithMultipleFieldNames(
        @LogParams(
            LogParam(field = "firstName"),
            LogParam(field = "lastName")
        ) person: Person
    ) {
        // calls MDC.put("person.firstName", person.firstName) and
        // calls MDC.put("person.lastName", person.lastName)
    }

    data class Person(val firstName: String, val lastName: String)
}
````

Please note that the underyling `io.cloudflight.platform.logging.interceptor.LogParamInterceptor` also takes care of cleaning
up the MDC context again after the method call.

#### mdcScope

If you cannot use `@LogParam` for some reason `platform-spring-logging` provides an additional convenient option.

The global function `mdcScope` keeps track of all fields MDC manipulations done in the passed lambda and cleans up for you afterwards.

````kotlin
fun sayHello(name: String) {
    mdcScope {
        MDC.put("name", name)
        MDC.put("name" to name) // this is equivalent to the line above
        LOG.info("We are calling hello now")
    }
}
````

If you need the `mdcScope` to cover your whole function you can also use it as a single-expression function:

````kotlin
fun sayHello(name: String) = mdcScope {
    MDC.put("name", name)
    MDC.put("name" to name) // this is equivalent to the line above
    LOG.info("We are calling hello now")
}
````

Please note that the `MDC` available inside the `mdcScope`-functions scope is not `org.slf4j.MDC` but a wrapper build around it.

### JPA

The module `platform-spring-jpa` wraps all required libraries in order to access a relational database
with JPA/Hibernate and Spring Data, especially Spring's `spring-data-jpa` and Spring Boot's `spring-boot-starter-data-jpa`.

It also automatically applies `@EnableTransactionManagement`.

#### QueryDSL Support

If you want to use [Query DSL](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02.html#jpa_integration), then
`platform-spring-jpa` autoconfigures a `JPQLQueryFactory` which you can use to create QueryDSL queries. Anyways,
you need to add QueryDSL to your classpath manually (it does not come by automatically), and also do not
forget to apply the annotation processor.

If you are using Kotlin entities (which is our preferred way), then your `build.gradle` should look somehow like that:

````groovy
dependencies {
    implementation 'io.cloudflight.platform.spring:platform-spring-jpa'
    implementation 'com.querydsl:querydsl-jpa'

    kapt 'com.querydsl:querydsl-apt::jpa'
    kapt 'io.cloudflight.platform.spring:platform-spring-jpa'
}
````

Then, in order to use QueryDSL, create a [custom repository](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior) and inject the `JPQLQueryFactory`:

````kotlin
interface ArtifactRepository : JpaRepository<Artifact, Long>, QueryDslArtifactRepository {  // <1>
    fun findArtifactByProjectAndName(project: Project, name: String): Artifact?
}

interface QueryDslArtifactRepository {                                                      // <2>
    fun findAllByGroupId(groupId: String): List<ArtifactListDto>
}

@Repository
class QueryDslArtifactRepositoryImpl(                                                       // <3>
    private val queryFactory: JPQLQueryFactory                                              // <4>
) : QueryDslArtifactRepository {

    private val a = QArtifact.artifact

    override fun findAllByGroupId(groupId: String): List<ArtifactListDto> {
        return queryFactory.select(QArtifactListDto(a.name, a.packaging))                   // <5>
            .from(a)
            .where(a.project.name.eq(groupId))
            .fetch()
    }
}
````

1. Your default `JpaRepository` which extends from your custom repository interface
2. Your custom repository interface with all methods that you want to query with QueryDSL
3. Implementation of your custom repository
4. Inject `JPQLQueryFactory`
5. Use the `JPQLQueryFactory` to create your query instances

### Caching

TBD

### Scheduling

TBD

### Internationalization (I18n)

The module `platform-spring-i18n` provides some additional utility services around Spring's i18n support:

* Tracking available locales
* Defining a default locale on the server

Add the module to your server `build.gradle` like that:

````groovy
dependencies {
    implementation 'io.cloudflight.platform.spring:platform-spring-i18n'
}
````

Add configuration to your `application.yaml`

````yaml
cloudflight:
  i18n:
    locales:
      - GERMAN
    default: GERMAN

spring:
  messages:
    basename: classpath:/messages
````

Inject the spring bean `io.cloudflight.platform.i18n.I18nService` to query all available and the default locale on the backend.

Implement the interface `io.cloudflight.platform.i18n.LocaleAccess` in any of your beans to get the the locale of the current thread.

If you want to handle exposing i18n keys to the frontend in some custom way, use the `ListResourceBundleMessageSource`
bean directly and disable automatic exposure of all the message-properties via `/api/i18n` with this configuration:

````yaml
cloudflight:
  i18n:
    httpendpoint:
      enabled: false
````

### Validation

The module `platform-spring-validation` gives you client-side support for validating user input, triggered by validations on the server, and it
plays well together with `platform-spring-i18n`.

Form-validations on client-side are insecure (no-one prevents an arbitrary client to bypass those validations),
but on the other hand web clients (like Angular) cannot easily deal with Spring's Backend Validation Support.

This module builds the bridge between Spring's `BindException` and DTOs which can be serialized as JSON and being used on the client
to display those validations.

All you need to do is to embed the module `platform-spring-validation`, the `PlatformValidationAutoConfiguration` will automatically create beans
to transform all instances of `BindException` or `MethodArgumentNotValidException` to `ErrorResponse` instances which look like the following:

````kotlin
data class ErrorResponse(
    val fieldMessages: List<FieldMessageDto> = emptyList(),
    val globalMessages: List<GlobalMessageDto> = emptyList()
)
````

Behind the scenes, Spring's I18n support around `MessageSource` is being utilized to transform technical error codes into human-readable
and localized strings. Simply create `messages_[lang].properties` files on the backend

You can also use Springs JSR303 Validation Support with the according annotations `@Valid`, `@NotNull` and so on, as well as on DTOs
and on entities.

### Messaging

TBD

## Test modules

The Cloudflight Platform also provides some modules that help you create Unit or Integration Tests.

As described in the section dependency management, the platform dependency `platform-spring-test-bom`
automatically adds JUnit5, AssertJ and MockK to your `testImplementation` configuration.

While those modules are handsome in each module and can be used everywhere, there exist additional test modules within the
Cloudflight platform for more sophisticated tests (mostly for the server modules):

### Performance-Profiling JUnit5 tests

When projects get bigger, very often also the test cases get more complex (especially intergration tests) which often
has negative impact on the compile/build performance.

In order to have more transparency of how long your tests, the module `platform-spring-test` adds some profiling support on different levels:

#### Spring Context

First thing is that we automatically register a `BufferingApplicationStartup` bean to your test case in order to be able to gain
insights of the performance of your `ApplicationContext` when it starts up. Under the hood it uses the same mechanism as described in
"Startup time analysis", that means in order to see the logs, you need to set the logger `io.cloudflight.platform.server.ApplicationStartupPrinter` to `TRACE`
in your `logback-test.xml`.

### Test-Support for Spring Boot server applications

This module transitively gives you support to test Spring and Spring Boot applications (`spring-test` and `spring-boot-starter-test`),
that means you can automatically use `@SpringBootTest` in your integration tests.

Include the module `platform-spring-test` as follows in your server module:

````groovy
dependencies {
    testImplementation 'io.cloudflight.platform.spring:platform-spring-test'
}
````

WARNING: Instances of `@SpringBootTest` are costly during execution. If possible, write plain unit tests in your service modules, and use Spring Boot container tests only in your server modules.

#### Client-side testing of Spring Boot applications

Running a `@SpringBootTest` is costly, and it should only be used in your Server-Module in order to reduce
test execution time. Anyways, you might then also use our support to test your APIs via the network, that means
starting up your whole server, dynamically creating a client based on your API interfaces, and then executing
HTTP requests. That way you are also testing your Spring WebMVC annotations without any mocking infrastructure.

Use the class `FeignTestClientFactory` in connection with `LocalServerPort` as shown in this snippet:

````kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)     // <1>
class FeignTestClientFactoryTest(
    @Autowired @LocalServerPort private val port: Int                           // <2>
) {

    private val helloApi =
        FeignTestClientFactory.createClientApi(HelloWorldApi::class.java, port) // <3>

    @Test
    fun helloWorld() {
        assertThat(helloApi.helloWorld("John").name).isEqualTo("John")
    }
}

// All subsequent classes usually come from the application itself, you don't need
// them in your test classes. We just want to give an impression here of what we are
// testing here

@SpringBootApplication
class TestApplication                                                           // <4>

@Api("Project")
interface HelloWorldApi {                                                       // <5>

    @GetMapping("/hello/world")
    fun helloWorld(@RequestParam("name") name: String): HelloWorldDto
}

data class HelloWorldDto(val name: String, val time: LocalDateTime)

@RestController
class HelloWorldController : HelloWorldApi {                                    // <6>
    override fun helloWorld(name: String): HelloWorldDto {
        return HelloWorldDto(name, LocalDateTime.now())
    }
}
````

1. It's important to have `SpringBootTest.WebEnvironment.RANDOM_PORT` here as `webEnvironment`
2. Inject the `@LocalServerPort` as variable into your test case, it will have the value of of the random port of your server
3. Create a client using the `FeignTestClientFactory` by passing exactly that port. You may also inject your own `ApplicationContext` here in order to add HTTP interceptors or similar.
4. Your application classes, usually from your Server-Module
5. Any API from your -api-module
6. The server implementation of your API



#### QuickPerf

Additionally, the module `platform-spring-test` comes with the great library [QuickPerf](https://github.com/quick-perf/doc/wiki/QuickPerf).
QuickPerf is a testing library for Java to quickly evaluate and improve some performance-related properties. The QuickPerf extension
is being registered by `platform-spring-test`, so you don't need to add `@QuickPerfTest` on your test classes.


### Test-Support for JPA

The module `platform-spring-test-jpa` leverages QuickPerf support by also adding support to [profile SQL queries](https://github.com/quick-perf/doc/wiki/SQL-annotations).

That way, you can write test methods like that:

````kotlin
@Test
@ExpectSelect(1)
fun getArtifact() {
    repositoryService.getArtifact("foo", "bar")
}
````

This test class will fail if the number of SQL queries being made within the body of that method is not equal to 1.
Have a look at the [QuickPerf website](https://github.com/quick-perf/doc/wiki/SQL-annotations#Available-SQL-annotations)
for all available annotations including configuration support.

### Test-Support for Testcontainers

Embed the module `platform-spring-test-testcontainers` to get support for [Testcontainers](https://testcontainers.org) via
the [wrapper library from playtika](https://github.com/Playtika/testcontainers-spring-boot).


The playtika library provides wrappers for lots of containers (MariaDB, Postgres, RabbitMQ, Localstack, MinIO,...) which
you have to add yourself in your Gradle scripts as shown below. You just don't need to care about versioning, that is being
done by the platform, as the BOM of Playtika is being embedded. The list of available wrapper libraries can be
found [here](https://github.com/Playtika/testcontainers-spring-boot#supported-services).

#### MariaDB via Testcontainers

Here is an example how to use the MariaDB testcontainer within your `@SpringBootTest`. First, add the dependency to
`platform-spring-test-testcontainers` along with `embedded-mariadb`:

````groovy
dependencies {
    testImplementation 'io.cloudflight.platform.spring:platform-spring-test-testcontainers'
    testImplementation 'com.playtika.testcontainers:embedded-mariadb'
}
````

Then configure your Spring DataSource with the exposed properties in your `application-test.yaml`:

````yaml
spring:
  datasource:
    url: jdbc:mariadb://${embedded.mariadb.host}:${embedded.mariadb.port}/${embedded.mariadb.schema}
    username: ${embedded.mariadb.user}
    password: ${embedded.mariadb.password}

````

Your test case then is as easy as to just use the test profile and start a `@SpringBootTest`:

````kotlin
@SpringBootTest
@ActiveProfiles(ApplicationContextProfiles.TEST)
class ServerIntegrationTest(
@Autowired private val personService: PersonService
) {

    @Test
    fun listPersons() {
        // your test comes here
    }
}
````

The underlying libraries have automatically created a MariaDB instance for you in an own container. Use Flyway or Liquibase
to initialize your database just as in production.


### BDD Support

The module `io.cloudflight.platform.spring:platform-spring-test-bdd` pulls the required libraries of [JGiven](https://jgiven.org) to our classpath. It automatically
registers the `JGivenExtension` on all test JUnit5 test cases, so you don't need to add something like `@ExtendsWith(JGivenExtension.class)` to your test cases.

Have a look at the excellent [JGiven documentation](https://jgiven.org/userguide/) how to use the full strength of those tests. This module also ships
a [Kotlin extension for JGiven](https://github.com/toolisticon/jgiven-kotlin) for better JGiven support in Kotlin.


### ArchUnit Support

The module `io.cloudflight.platform.spring:platform-spring-test-archunit` embeds
embeds the [ArchUnit CleanCode Verifier](https://github.com/cloudflightio/archunit-cleancode-verifier) and provides
an `AbstractCleanCodeTest` that you can use as follows:

````kotlin
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import io.cloudflight.platform.spring.test.archunit.AbstractCleanCodeTest

@AnalyzeClasses(packagesOf = [ArchitectureTest::class], importOptions = [DoNotIncludeTests::class])
class ArchitectureTest : AbstractCleanCodeTest() {
    // your ArchUnit tests go here
}
````

It embeds all `CleanCodeRuleSets` from the [ArchUnit CleanCode Verifier](https://github.com/cloudflightio/archunit-cleancode-verifier). 
Have a look at the documentation in that subproject.

We recommend to put that test into the root package of the module in your repository which
imports all other modules (typically a `-server`-module). This way, your whole source code is
being analyzed
