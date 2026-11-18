package com.newsletter.tests;

import com.newsletter.base.BaseTest;
import com.newsletter.pages.SignUpPage;
import com.newsletter.pages.SuccessPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.newsletter.data.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Newsletter sign-up form")
class NewsletterSignUpTest extends BaseTest {

    private SignUpPage signUpPage;

    /** Runs after BaseTest has started Chrome: every test begins on a freshly opened page. */
    @BeforeEach
    void openSignUpPage() {
        signUpPage = new SignUpPage(driver).open();
    }

    @Nested
    @DisplayName("When the page loads")
    class PageLoad {

        @Test
        @Tag("smoke")
        @DisplayName("shows the title, heading, benefits and subscribe button")
        void pageLoadsWithExpectedContent() {
            assertThat(signUpPage.getTitle()).containsIgnoringCase(PAGE_TITLE_FRAGMENT);
            assertThat(signUpPage.getHeadingText()).isEqualTo(SIGNUP_HEADING);
            assertThat(signUpPage.getBenefitCount()).isEqualTo(BENEFIT_COUNT);
            assertThat(signUpPage.getSubscribeButtonText()).isEqualTo(SUBSCRIBE_BUTTON_TEXT);
        }

        @Test
        @DisplayName("shows no validation error before the form is submitted")
        void noErrorOnLoad() {
            assertThat(signUpPage.isErrorDisplayed()).isFalse();
            assertThat(signUpPage.isEmailMarkedInvalid()).isFalse();
        }
    }

    @Nested
    @DisplayName("With a valid email")
    class ValidEmail {

        @Test
        @Tag("smoke")
        @DisplayName("shows the success message with the submitted email")
        void validEmailShowsSuccessMessage() {
            // Act
            SuccessPage successPage = signUpPage.subscribeWith(VALID_EMAIL);

            // Assert
            assertThat(successPage.isDisplayed()).isTrue();
            assertThat(successPage.getHeadingText()).isEqualTo(SUCCESS_HEADING);
            assertThat(successPage.getConfirmedEmail()).isEqualTo(VALID_EMAIL);
            assertThat(successPage.getMessageText()).contains(VALID_EMAIL);
            assertThat(signUpPage.isDisplayed())
                    .as("sign-up card should be hidden after subscribing")
                    .isFalse();
        }

        @Test
        @DisplayName("trims spaces around the email before confirming it")
        void emailIsTrimmed() {
            SuccessPage successPage = signUpPage.subscribeWith("   " + VALID_EMAIL + "   ");

            assertThat(successPage.getConfirmedEmail()).isEqualTo(VALID_EMAIL);
        }

        @Test
        @DisplayName("dismissing the message returns to a clean, empty form")
        void dismissReturnsToCleanForm() {
            SignUpPage formAgain = signUpPage.subscribeWith(VALID_EMAIL).dismiss();

            assertThat(formAgain.isDisplayed()).isTrue();
            assertThat(formAgain.getEmailValue()).isEmpty();
            assertThat(formAgain.isErrorDisplayed()).isFalse();
            assertThat(formAgain.isEmailMarkedInvalid()).isFalse();
        }
    }

    @Nested
    @DisplayName("With an invalid email")
    class InvalidEmail {

        @Test
        @DisplayName("an empty field shows \"Email address is required\"")
        void emptyEmailShowsRequiredError() {
            // Act: submit without typing anything
            signUpPage.clickSubscribe();
            signUpPage.waitForError();

            // Assert
            assertThat(signUpPage.getErrorText()).isEqualTo(EMPTY_EMAIL_ERROR);
            assertThat(signUpPage.isEmailMarkedInvalid()).isTrue();
            assertThat(signUpPage.isDisplayed())
                    .as("user should stay on the form")
                    .isTrue();
        }

        @ParameterizedTest(name = "rejects \"{0}\"")
        @MethodSource("com.newsletter.data.TestData#invalidEmails")
        @DisplayName("badly formatted emails show \"Valid email required\"")
        void invalidFormatsShowError(String invalidEmail) {
            signUpPage.subscribeExpectingError(invalidEmail);

            assertThat(signUpPage.getErrorText()).isEqualTo(INVALID_EMAIL_ERROR);
            assertThat(signUpPage.isEmailMarkedInvalid()).isTrue();
            assertThat(new SuccessPage(driver).isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("the error clears live once the email becomes valid")
        void errorClearsWhileTyping() {
            // Arrange: get the form into an error state
            signUpPage.subscribeExpectingError(INCOMPLETE_EMAIL);

            // Act: finish typing the address, without submitting
            signUpPage.appendToEmail(COMPLETING_SUFFIX).waitForErrorToClear();

            // Assert
            assertThat(signUpPage.isErrorDisplayed()).isFalse();
            assertThat(signUpPage.isEmailMarkedInvalid()).isFalse();
        }

        @Test
        @DisplayName("fixing the email after an error lets the user subscribe")
        void correctingEmailAfterErrorSucceeds() {
            signUpPage.subscribeExpectingError("not-an-email");

            SuccessPage successPage = signUpPage.subscribeWith(VALID_EMAIL);

            assertThat(successPage.getConfirmedEmail()).isEqualTo(VALID_EMAIL);
        }
    }
}