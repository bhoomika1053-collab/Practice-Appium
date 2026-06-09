package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest extends BaseTest {

    @Test
    public void appLaunchesSuccessfully() {
        Assert.assertNotNull(driver, "Driver should be initialized");
        Assert.assertFalse(driver.getPageSource().isEmpty(), "Page source should not be empty after app launch");
    }
}
