package base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

import java.io.File;
import java.net.URI;
import java.time.Duration;

public class BaseTest {

    protected AndroidDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        File app = new File(ConfigReader.get("app.path"));

        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(ConfigReader.get("platform.name"));
        options.setAutomationName(ConfigReader.get("automation.name"));
        options.setUdid(ConfigReader.get("device.name"));
        options.setApp(app.getAbsolutePath());

        try {
            driver = new AndroidDriver(new URI(ConfigReader.get("appium.url")).toURL(), options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(ConfigReader.get("explicit.wait"))));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Appium driver", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
