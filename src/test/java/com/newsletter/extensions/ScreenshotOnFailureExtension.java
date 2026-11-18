package com.newsletter.extensions;

import com.newsletter.base.BaseTest;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Saves a PNG to target/screenshots when a test fails.
 *
 * Runs after the test method but BEFORE @AfterEach, so the browser is still
 * open. The CI workflow uploads this folder so you can download the images.
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotOnFailureExtension.class);
    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean testFailed = context.getExecutionException().isPresent();
        if (!testFailed) {
            return;
        }

        WebDriver driver = findDriver(context);
        if (driver == null) {
            return;   // Chrome never started, so there is nothing to photograph
        }

        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.createDirectories(SCREENSHOT_DIR);

            String fileName = toSafeFileName(
                    context.getRequiredTestClass().getSimpleName() + "_" + context.getDisplayName()) + ".png";
            Path file = SCREENSHOT_DIR.resolve(fileName);
            Files.write(file, png);

            LOG.warn("Screenshot saved: {}", file.toAbsolutePath());
        } catch (Exception e) {
            // A screenshot problem must never hide the real test failure
            LOG.error("Could not capture screenshot", e);
        }
    }

    /**
     * Finds the BaseTest that owns the browser. With @Nested test classes the
     * innermost instance is the nested class, so we search all enclosing instances.
     */
    private static WebDriver findDriver(ExtensionContext context) {
        return context.getRequiredTestInstances().getAllInstances().stream()
                .filter(BaseTest.class::isInstance)
                .map(instance -> ((BaseTest) instance).getDriver())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /** Turns "shows the success message" into "shows_the_success_message". */
    private static String toSafeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]+", "_");
    }
}