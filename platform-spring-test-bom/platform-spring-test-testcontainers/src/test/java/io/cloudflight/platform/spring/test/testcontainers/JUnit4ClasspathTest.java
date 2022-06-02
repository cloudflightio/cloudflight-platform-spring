package io.cloudflight.platform.spring.test.testcontainers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class JUnit4ClasspathTest {

    @Test
    public void noJunit4OnClasspath() {
        try {
            Class.forName("org.junit.Before");
            fail("JUnit4 must not be on the classpath");
        } catch (ClassNotFoundException e) {
            // ok
        }
    }
}
