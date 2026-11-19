# Newsletter Sign-up – Selenium WebDriver Test Automation



Automated UI tests for a newsletter sign-up form with a success message, built with
**Selenium WebDriver**, **ChromeDriver**, **JUnit 5** and the **Page Object Model (PageFactory)**,
and run automatically by a **GitHub Actions** CI pipeline that notifies the team on Slack.

| Tool | Purpose |
|---|---|
| Java 17 | Test code language |
| Maven | Dependencies, build and test execution (`pom.xml`) |
| Selenium WebDriver 4.25 | Controls the Chrome browser |
| ChromeDriver | Bridge between Selenium and Chrome |
| JUnit 5 | Test structure, lifecycle and parameterized tests |
| AssertJ | Readable assertions |
| SLF4J Simple | Test execution logging |
| GitHub Actions | Continuous integration |

## Project structure

```
Newsletter-selenium/
├── .github/workflows/ci.yml          # CI pipeline
├── app/                              # Website under test
│   ├── index.html
│   ├── styles.css
│   ├── script.js
│   └── assets/
├── pom.xml                           # Maven configuration
└── src/test/
    ├── java/com/newsletter/
    │   ├── config/
    │   │   ├── TestConfig.java       # Settings: baseUrl, headless, wait timeout
    │   │   └── DriverFactory.java    # Creates and configures ChromeDriver
    │   ├── pages/
    │   │   ├── BasePage.java         # PageFactory setup + explicit-wait helpers
    │   │   ├── SignUpPage.java       # Sign-up form page object
    │   │   └── SuccessPage.java      # Success message page object
    │   ├── base/
    │   │   └── BaseTest.java         # Opens/closes Chrome around each test
    │   ├── extensions/
    │   │   ├── TestLoggingExtension.java          # START / PASSED / FAILED logs
    │   │   └── ScreenshotOnFailureExtension.java  # PNG screenshot on failure
    │   ├── data/
    │   │   └── TestData.java         # Test inputs and expected page text
    │   └── tests/
    │       └── NewsletterSignUpTest.java
    └── resources/
        ├── simplelogger.properties   # Log format
        └── junit-platform.properties # JUnit settings
```

The website (`app/`) and the tests (`src/test/`) are kept separate. Inside the tests, each layer has
one job: configuration, browser setup, page structure, test data, and test scenarios.

## 1. Setup and configuration

**Requirements:** JDK 17+, Maven 3.8+, Google Chrome.

All libraries are declared in `pom.xml`, and Maven downloads them automatically:

```bash
mvn dependency:resolve
```

**ChromeDriver** is resolved by `DriverFactory` in this order:

1. The `CHROMEDRIVER_PATH` environment variable. The CI pipeline installs Chrome and ChromeDriver
   explicitly and sets this.
2. Otherwise, **Selenium Manager** (built into Selenium 4.6+) detects the installed Chrome version and
   downloads the matching ChromeDriver.

Chrome is configured with `ChromeOptions`: headless mode, a fixed 1366×900 window so the desktop layout
is always tested, and the flags needed to run inside CI containers.

## 2. Running the tests

```bash
mvn test                                 # all tests, headless Chrome
mvn test -Dheadless=false                # show the browser (needs a screen, not Codespaces)
mvn test -Dgroups=smoke                  # only the @Tag("smoke") tests
mvn test -DbaseUrl=https://example.com   # test a deployed version of the site
```

By default the tests open `app/index.html` directly from disk, so no web server is needed.

**Outputs:**
- `target/surefire-reports/`: test results (XML and text)
- `target/screenshots/`: a PNG of the page for every failed test

## 3. Test cases (JUnit 5)

14 test runs, grouped with `@Nested` classes:

| Group | Test | What it checks |
|---|---|---|
| When the page loads | shows title, heading, benefits and button | Content renders correctly |
| | no error before submit | No error text, no `aria-invalid` |
| With a valid email | shows the success message | Heading, confirmed email, form hidden |
| | trims spaces around the email | `"  a@b.com  "` is confirmed as `a@b.com` |
| | dismiss returns to a clean form | Form shown, field empty, no error |
| With an invalid email | empty field | "Email address is required" + `aria-invalid="true"` |
| | 6 badly formatted emails (parameterized) | "Valid email required", no success message |
| | error clears live | Completing the email removes the error without resubmitting |
| | fixing the email after an error | User can then subscribe successfully |

**JUnit 5 features used:** `@Test`, `@BeforeEach`, `@AfterEach`, `@Nested`, `@DisplayName`, `@Tag`,
`@ParameterizedTest` with `@MethodSource`, and custom extensions registered with `@ExtendWith`.

Each test gets a fresh browser, so tests are independent and can run in any order.

## 4. Page Object Model

- **One class per screen:** `SignUpPage` (the form) and `SuccessPage` (the confirmation message),
  both extending `BasePage`.
- **Locators with `@FindBy`:** elements are found by `id` and CSS selector, and initialised with
  `PageFactory.initElements(driver, this)` in `BasePage`.
- **No locators in tests, no assertions in page objects:** tests call readable actions like
  `subscribeWith(email)` and `dismiss()`, and the page objects only report what is on screen.
- **Fluent navigation:** actions return the next page, e.g. `signUpPage.subscribeWith(email).dismiss()`.
- **Explicit waits only:** all waiting uses `WebDriverWait` with `ExpectedConditions` or custom
  conditions, never `Thread.sleep`.

## 5. CI pipeline (GitHub Actions)

`.github/workflows/ci.yml` runs on every **push** and **pull request** to `main`, and can be started
manually with **Run workflow**.

**`test` job:**
1. Checks out the code and installs JDK 17 (with Maven caching)
2. Installs Chrome and ChromeDriver and prints their versions
3. Downloads Maven dependencies
4. Runs `mvn test` headless
5. Publishes a test report listing every test by name, as a check and job summary
6. Uploads `surefire-reports`, plus `failure-screenshots` when a test fails

**`notify` job:** runs after `test` every time, and sends the result (PASSED / FAILED / CANCELLED)
with the branch, commit, author and a link to the run.

**Detailed logs:** tool versions, a START / PASSED / FAILED line with timing for every test,
full stack traces on failure, and the published test report.



| Channel | Secrets |
|---|---|
| Slack | `SLACK_WEBHOOK_URL`: Incoming Webhook URL from a Slack app with the `incoming-webhook` scope |


