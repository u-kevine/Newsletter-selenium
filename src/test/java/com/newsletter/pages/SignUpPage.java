package com.newsletter.pages;

import com.newsletter.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/** Page object for the newsletter sign-up card (section#signup). */
public class SignUpPage extends BasePage {

    // ---- Locators ----

    @FindBy(id = "signup")
    private WebElement signUpCard;

    @FindBy(id = "signup-title")
    private WebElement heading;

    @FindBy(css = "#signup .benefits li")
    private List<WebElement> benefits;

    @FindBy(id = "email")
    private WebElement emailInput;

    /** Always in the DOM; its text is empty when there is no error. */
    @FindBy(id = "email-error")
    private WebElement emailError;

    @FindBy(css = "#signup-form button[type='submit']")
    private WebElement subscribeButton;

    public SignUpPage(WebDriver driver) {
        super(driver);
    }

    // ---- Navigation ----

    /** Opens the page and waits until the form is ready. */
    public SignUpPage open() {
        driver.get(TestConfig.baseUrl());
        return waitUntilLoaded();
    }

    public SignUpPage waitUntilLoaded() {
        waitForVisible(heading);
        waitForVisible(emailInput);
        return this;
    }

    // ---- Actions ----

    public SignUpPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    /** Types extra characters without clearing the field (used to test live validation). */
    public SignUpPage appendToEmail(String text) {
        waitForVisible(emailInput).sendKeys(text);
        return this;
    }

    public void clickSubscribe() {
        click(subscribeButton);
    }

    /** Happy path: submits a valid email and returns the success page. */
    public SuccessPage subscribeWith(String email) {
        enterEmail(email);
        clickSubscribe();
        return new SuccessPage(driver).waitUntilLoaded();
    }

    /** Unhappy path: submits an email and waits for the validation error. */
    public SignUpPage subscribeExpectingError(String email) {
        enterEmail(email);
        clickSubscribe();
        return waitForError();
    }

    public SignUpPage waitForError() {
        wait.until(d -> !emailError.getText().isBlank());
        return this;
    }

    public SignUpPage waitForErrorToClear() {
        wait.until(d -> emailError.getText().isBlank());
        return this;
    }

    // ---- State the tests can check ----

    public boolean isDisplayed() {
        return isVisible(signUpCard);
    }

    public String getHeadingText() {
        return heading.getText();
    }

    public int getBenefitCount() {
        return benefits.size();
    }

    public String getEmailValue() {
        return emailInput.getDomProperty("value");
    }

    /** The error span is always present, so "showing an error" means it has text. */
    public boolean isErrorDisplayed() {
        return !emailError.getText().isBlank();
    }

    public String getErrorText() {
        return emailError.getText().trim();
    }

    /** Your script sets aria-invalid="true" on errors and removes it when cleared. */
    public boolean isEmailMarkedInvalid() {
        return "true".equals(emailInput.getDomAttribute("aria-invalid"));
    }

    public String getSubscribeButtonText() {
        return subscribeButton.getText();
    }
}