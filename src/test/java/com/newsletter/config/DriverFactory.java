package com.newsletter.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

/**
 * Creates a configured ChromeDriver.
 *
 * How ChromeDriver is found:
 *   1. CHROMEDRIVER_PATH environment variable (set by the CI workflow)
 *   2. Otherwise Selenium Manager (built into Selenium 4.6+) detects your
 *      Chrome version and downloads the matching driver automatically
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createChromeDriver() {
        ChromeOptions options = buildOptions();

        WebDriver driver;
        String driverPath = System.getenv("CHROMEDRIVER_PATH");
        if (driverPath != null && !driverPath.isBlank()) {
            LOG.info("Using ChromeDriver at: {}", driverPath);
            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(driverPath))
                    .build();
            driver = new ChromeDriver(service, options);
        } else {
            LOG.info("Resolving ChromeDriver with Selenium Manager");
            driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        LOG.info("Chrome started (headless={})", TestConfig.headless());
        return driver;
    }

    private static ChromeOptions buildOptions() {
        ChromeOptions options = new ChromeOptions();

        if (TestConfig.headless()) {
            options.addArguments("--headless=new");   // no visible window
        }
        options.addArguments(
                "--window-size=1366,900",   // fixed size so the desktop layout is used every run
                "--no-sandbox",             // needed inside CI containers
                "--disable-dev-shm-usage",  // avoids crashes from small shared memory in CI
                "--disable-gpu",
                "--disable-extensions");

        // In CI the workflow installs a specific Chrome and tells us where it is
        String chromeBinary = System.getenv("CHROME_BIN");
        if (chromeBinary != null && !chromeBinary.isBlank()) {
            options.setBinary(chromeBinary);
            LOG.info("Using Chrome binary: {}", chromeBinary);
        }
        return options;
    }
}