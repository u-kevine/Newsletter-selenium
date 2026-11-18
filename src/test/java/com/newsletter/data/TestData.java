package com.newsletter.data;

import java.util.stream.Stream;

/**
 * Test inputs and expected page text, kept in one place.
 *
 * If the page copy changes (e.g. a new error message), update it here
 * and every test that uses it stays correct.
 */
public final class TestData {

    private TestData() {
    }

    // ---- Inputs ----

    public static final String VALID_EMAIL = "ash@loremcompany.com";

    /** Missing only the ".com", used to check that live validation clears the error. */
    public static final String INCOMPLETE_EMAIL = "ash@loremcompany";
    public static final String COMPLETING_SUFFIX = ".com";

    /** Badly formatted emails, fed to the parameterized test one at a time. */
    public static Stream<String> invalidEmails() {
        return Stream.of(
                "plainaddress",          // no @ at all
                "missing-at.com",        // no @, has a dot
                "user@",                 // nothing after @
                "@domain.com",           // nothing before @
                "user@domain",           // no top-level domain (.com, .rw, ...)
                "user name@domain.com"   // space inside the address
        );
    }

    // ---- Expected page text ----

    public static final String PAGE_TITLE_FRAGMENT = "newsletter";
    public static final String SIGNUP_HEADING = "Stay updated!";
    public static final int BENEFIT_COUNT = 3;
    public static final String SUBSCRIBE_BUTTON_TEXT = "Subscribe to monthly newsletter";
    public static final String SUCCESS_HEADING = "Thanks for subscribing!";

    // These must match the MESSAGES object in app/script.js
    public static final String EMPTY_EMAIL_ERROR = "Email address is required";
    public static final String INVALID_EMAIL_ERROR = "Valid email required";
}