package com.newsletter.tests;

import com.newsletter.config.DriverFactory;
import com.newsletter.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

class DriverSmokeTest {

    @Test
    void chromeOpensThePage() {
        WebDriver driver = DriverFactory.createChromeDriver();
        try {
            driver.get(TestConfig.baseUrl());
            System.out.println("Page title: " + driver.getTitle());
        } finally {
            driver.quit();   // always close Chrome, even if something fails
        }
    }
}