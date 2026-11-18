package com.newsletter.base;

import com.newsletter.config.DriverFactory;
import com.newsletter.extensions.ScreenshotOnFailureExtension;
import com.newsletter.extensions.TestLoggingExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/**
 * Parent class for every UI test.
 *
 * Each test gets its own fresh browser, so tests never depend on each other:
 * one test's typed email or open success message can't leak into the next.
 */
@ExtendWith({TestLoggingExtension.class, ScreenshotOnFailureExtension.class})
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void startBrowser() {
        driver = DriverFactory.createChromeDriver();
    }

    @AfterEach
    void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Lets the screenshot extension reach the browser. */
    public WebDriver getDriver() {
        return driver;
    }
}