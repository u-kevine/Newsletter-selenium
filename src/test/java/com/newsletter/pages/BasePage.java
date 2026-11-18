package com.newsletter.pages;

import com.newsletter.config.TestConfig;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Parent class for all page objects.
 *
 * The constructor calls PageFactory.initElements, which connects every
 * @FindBy field in the subclass to the real element on the page.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.explicitWait());
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    /** Waits until the element is visible, then returns it. */
    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /** Waits until the element is no longer visible. */
    protected void waitForHidden(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /** Waits until the element can be clicked, then clicks it. */
    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /** Clears a field and types new text into it. */
    protected void type(WebElement element, String text) {
        WebElement field = waitForVisible(element);
        field.clear();
        field.sendKeys(text);
    }

    /** True if the element is on screen; false if hidden or missing (never throws). */
    protected boolean isVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }
}