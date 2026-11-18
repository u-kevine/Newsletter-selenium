package com.newsletter.config;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Central place for run-time settings.
 *
 * Each value is looked up in this order:
 *   1. System property (set in pom.xml, or with -D on the command line)
 *   2. Environment variable (handy in CI)
 *   3. A sensible default
 */
public final class TestConfig {

    // Utility class: only static methods, so nobody should create an instance
    private TestConfig() {
    }

    /** URL of the page under test. Defaults to the local copy in app/index.html. */
    public static String baseUrl() {
        String fromProperty = System.getProperty("baseUrl");
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }

        String fromEnv = System.getenv("BASE_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        // e.g. file:///home/you/Newsletter-selenium/app/index.html
        return Path.of("app", "index.html").toAbsolutePath().toUri().toString();
    }

    /** Run Chrome without a visible window. True by default so CI works out of the box. */
    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "true"));
    }

    /** Maximum time an explicit wait will wait for something to happen on the page. */
    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.getLong("timeoutSeconds", 10L));
    }
}