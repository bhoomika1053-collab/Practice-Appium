---
name: appium-best-practices
description: Appium E2E test automation standards — locator strategy, assertion patterns, test structure, POM, API mocking, auth patterns, wait strategies, and anti-patterns. Use when writing, reviewing, or debugging Appium tests.
user-invocable: false
---


# Appium Standard Java TestNG Mobile App

## Overview

This document provides a **standard Appium + Java + TestNG framework template** for mobile app automation. It is suitable for Android and can be extended for iOS.

The framework is designed with the following goals:

- Clean project structure
- Reusable page objects
- Centralized driver management
- Configurable capabilities
- TestNG-based execution and reporting
- Easy CI/CD integration

---

## Recommended Tech Stack

- **Language:** Java
- **Build Tool:** Maven
- **Test Framework:** TestNG
- **Automation Tool:** Appium
- **Design Pattern:** Page Object Model (POM)
- **Logging:** SLF4J / Log4j2
- **Reporting:** TestNG default reports / Extent Reports (optional)
- **Configuration:** `.properties` files or environment variables

---

## Suggested Project Structure

```text
appium-java-testng-framework/
├── pom.xml
├── testng.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/
│   │   │   │   ├── BaseTest.java
│   │   │   │   └── DriverManager.java
│   │   │   ├── config/
│   │   │   │   └── ConfigReader.java
│   │   │   ├── pages/
│   │   │   │   ├── LoginPage.java
│   │   │   │   └── HomePage.java
│   │   │   ├── utils/
│   │   │   │   ├── WaitUtils.java
│   │   │   │   ├── GestureUtils.java
│   │   │   │   ├── ScreenshotUtils.java
│   │   │   │   └── TestDataUtils.java
│   │   │   └── constants/
│   │   │       └── FrameworkConstants.java
│   └── test/
│       ├── java/
│       │   ├── tests/
│       │   │   ├── LoginTest.java
│       │   │   └── SmokeTest.java
│       │   └── listeners/
│       │       └── TestListener.java
│       └── resources/
│           ├── config.properties
│           ├── android/
│           │   └── app.apk
│           ├── ios/
│           │   └── app.ipa
│           └── testdata/
│               └── users.json
└── reports/
```

---

## Key Design Principles

### 1. Driver Isolation
- Use `ThreadLocal<AppiumDriver>` if parallel execution is needed.
- Keep driver creation in a dedicated `DriverManager`.

### 2. Configuration First
Store environment-specific values in `config.properties` or pass them via Maven command line.

### 3. Page Object Model
Encapsulate locators and user actions in page classes.

### 4. Reusable Utilities
Create helper methods for waits, gestures, screenshots, and common actions.

### 5. Keep Tests Thin
Tests should validate behavior, not manage low-level driver logic.

---

## Sample `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mobile.automation</groupId>
    <artifactId>appium-java-testng-framework</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <appium.java.client.version>9.x.x</appium.java.client.version>
        <selenium.version>4.x.x</selenium.version>
        <testng.version>7.x.x</testng.version>
        <slf4j.version>2.x.x</slf4j.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.appium</groupId>
            <artifactId>java-client</artifactId>
            <version>${appium.java.client.version}</version>
        </dependency>

        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>

        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
                <configuration>
                    <suiteXmlFiles>
                        <suiteXmlFile>testng.xml</suiteXmlFile>
                    </suiteXmlFiles>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Sample `config.properties`

```properties
platformName=Android
deviceName=Android Emulator
automationName=UiAutomator2
app=src/test/resources/android/app.apk
appPackage=com.example.app
appActivity=com.example.app.MainActivity
udid=emulator-5554
appiumServerURL=http://127.0.0.1:4723
implicitWait=10
explicitWait=20
noReset=true
fullReset=false
```

---

## Standard Capabilities Strategy

Keep desired capabilities centralized and configurable.

### Android Example

```java
UiAutomator2Options options = new UiAutomator2Options()
        .setPlatformName("Android")
        .setDeviceName("Android Emulator")
        .setAutomationName("UiAutomator2")
        .setApp("src/test/resources/android/app.apk")
        .setAppPackage("com.example.app")
        .setAppActivity("com.example.app.MainActivity")
        .setNoReset(true);
```

### iOS Example

```java
XCUITestOptions options = new XCUITestOptions()
        .setPlatformName("iOS")
        .setDeviceName("iPhone 15")
        .setAutomationName("XCUITest")
        .setApp("src/test/resources/ios/app.ipa")
        .setNoReset(true);
```

---

## Driver Manager Example

```java
package base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverManager {

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static void initializeDriver() {
        try {
            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setDeviceName("Android Emulator")
                    .setAutomationName("UiAutomator2")
                    .setApp("src/test/resources/android/app.apk");

            driver.set(new AndroidDriver(new URL("http://127.0.0.1:4723"), options));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Appium driver", e);
        }
    }

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

---

## Base Test Example

```java
package base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.initializeDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
```

---

## Page Object Example

### `LoginPage.java`

```java
package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class LoginPage {

    private final AppiumDriver driver;

    private final By usernameField = AppiumBy.accessibilityId("username");
    private final By passwordField = AppiumBy.accessibilityId("password");
    private final By loginButton = AppiumBy.accessibilityId("loginBtn");

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public LoginPage enterUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
        return this;
    }

    public void tapLogin() {
        driver.findElement(loginButton).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        tapLogin();
    }
}
```

---

## Test Class Example

### `LoginTest.java`

```java
package tests;

import base.BaseTest;
import base.DriverManager;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test(description = "Verify valid user can log in")
    public void validLoginTest() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login("testuser", "Password123");

        // Add assertion for home screen visibility
        // Example: Assert.assertTrue(new HomePage(DriverManager.getDriver()).isHomeDisplayed());
    }
}
```

---

## Sample `testng.xml`

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Mobile Automation Suite" parallel="false">
    <test name="Android Tests">
        <classes>
            <class name="tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

---

## Standard Utility Classes to Add

### 1. `WaitUtils`
Include explicit wait wrappers such as:
- wait for element visibility
- wait for clickability
- wait for presence

### 2. `GestureUtils`
Common gesture methods:
- tap
- long press
- swipe
- scroll
- drag and drop

### 3. `ScreenshotUtils`
Capture screenshots on:
- test failure
- important checkpoints
- debugging

### 4. `ConfigReader`
Read properties from `config.properties` and expose methods like:
- `getPlatformName()`
- `getDeviceName()`
- `getAppiumServerURL()`
- `getAppPath()`

---

## Naming Standards

Use consistent naming conventions:

- Test classes: `LoginTest`, `CheckoutTest`
- Page classes: `LoginPage`, `HomePage`
- Utility classes: `WaitUtils`, `GestureUtils`
- Methods: `enterUsername()`, `tapLogin()`, `verifyHomePageLoaded()`

---

## Best Practices

### Framework
- Keep locators only inside page classes.
- Avoid hardcoding environment-specific values.
- Use reusable methods for repeated actions.
- Separate test data from test logic.

### Stability
- Prefer accessibility id or resource-id locators.
- Use explicit waits instead of unnecessary sleeps.
- Add retry or recovery strategy only where justified.

### Maintainability
- Keep tests small and scenario-focused.
- Use descriptive test names and annotations.
- Group smoke, regression, and sanity tests using TestNG groups.

### Parallel Execution
- Use `ThreadLocal` driver management.
- Make sure no static shared mutable state exists in tests.
- Ensure devices and ports are unique per execution thread.

---

## Maven Commands

### Run all tests

```bash
mvn clean test
```

### Run a specific TestNG suite

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### Run a specific test class

```bash
mvn -Dtest=LoginTest test
```

---

## CI/CD Recommendations

Integrate with:
- Jenkins
- Azure DevOps
- GitHub Actions
- GitLab CI

Pipeline stages can include:
1. Checkout code
2. Install dependencies
3. Start Appium server
4. Execute tests
5. Publish reports
6. Archive logs/screenshots

---

## Reporting Recommendations

Start simple with:
- TestNG reports
- Screenshots on failure
- Console/log file retention

Optional enhancements:
- Extent Reports
- Allure Reports
- Device logs attachment
- Video recording in device cloud environments

---

## Folder-Level Ownership Guidance

- `base/` → framework core setup
- `pages/` → app screens and reusable actions
- `tests/` → validation scenarios only
- `utils/` → helpers and support functions
- `resources/` → app builds, configs, and test data

---

## Minimal Starter Checklist

Before running the framework, ensure:

- Java is installed
- Maven is installed
- Appium server is installed and running
- Android SDK / Xcode is configured
- Device or emulator/simulator is available
- App path is valid
- Desired capabilities are correct

---

## Scalability Suggestions

As your framework grows, consider adding:

- Environment profiles (`qa`, `uat`, `prod-like`)
- API validation helpers for hybrid test flows
- Data providers for multi-user testing
- Soft assertions
- BDD layer with Cucumber (if required by team)
- Cloud device execution support (BrowserStack, Sauce Labs, LambdaTest, etc.)

---

## Conclusion

This standard Appium Java TestNG framework gives you a solid base for:

- maintainable mobile automation
- scalable test execution
- clean project organization
- robust driver and page management

If needed, this can be further extended into:
- Android-only framework
- Android + iOS cross-platform framework
- Hybrid app automation framework
- CI/CD-ready enterprise framework with reporting and parallel execution
