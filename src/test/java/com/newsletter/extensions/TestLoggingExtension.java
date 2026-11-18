package com.newsletter.extensions;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Writes a clear START / PASSED / FAILED line with timing for every test,
 * so the GitHub Actions log shows exactly what ran and how it went.
 */
public class TestLoggingExtension implements BeforeTestExecutionCallback, TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger("TEST");
    private static final String START_TIME = "startTime";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        store(context).put(START_TIME, System.currentTimeMillis());
        LOG.info("START   {}", testName(context));
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        LOG.info("PASSED  {} ({} ms)", testName(context), elapsedMillis(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        LOG.error("FAILED  {} ({} ms) - {}", testName(context), elapsedMillis(context), cause.toString());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        LOG.warn("ABORTED {} - {}", testName(context), cause.toString());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        LOG.warn("SKIPPED {} - {}", testName(context), reason.orElse("no reason given"));
    }

    /** e.g. "NewsletterSignUpTest > shows the success message with the submitted email" */
    private static String testName(ExtensionContext context) {
        return context.getRequiredTestClass().getSimpleName() + " > " + context.getDisplayName();
    }

    private static long elapsedMillis(ExtensionContext context) {
        Long start = store(context).get(START_TIME, Long.class);
        return start == null ? -1 : System.currentTimeMillis() - start;
    }

    /** A per-test storage space JUnit provides, so timings from different tests never mix. */
    private static ExtensionContext.Store store(ExtensionContext context) {
        return context.getStore(
                ExtensionContext.Namespace.create(TestLoggingExtension.class, context.getUniqueId()));
    }
}