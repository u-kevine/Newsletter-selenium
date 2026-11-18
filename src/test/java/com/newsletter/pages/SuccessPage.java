package com.newsletter.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the "Thanks for subscribing!" message (section#success). */
public class SuccessPage extends BasePage {

    // ---- Locators ----

    @FindBy(id = "success")
    private WebElement successCard;

    @FindBy(id = "success-title")
    private WebElement heading;

    @FindBy(css = "#success p")
    private WebElement message;

    @FindBy(id = "success-email")
    private WebElement confirmedEmail;

    @FindBy(id = "dismiss-btn")
    private WebElement dismissButton;

    public SuccessPage(WebDriver driver) {
        super(driver);
    }

    public SuccessPage waitUntilLoaded() {
        waitForVisible(heading);
        return this;
    }

    // ---- Actions ----

    /** Closes the message and returns to the sign-up form. */
    public SignUpPage dismiss() {
        click(dismissButton);
        waitForHidden(successCard);
        return new SignUpPage(driver).waitUntilLoaded();
    }

    // ---- State the tests can check ----

    public boolean isDisplayed() {
        return isVisible(successCard);
    }

    public String getHeadingText() {
        return heading.getText();
    }

    public String getMessageText() {
        return message.getText();
    }

    public String getConfirmedEmail() {
        return confirmedEmail.getText();
    }
}