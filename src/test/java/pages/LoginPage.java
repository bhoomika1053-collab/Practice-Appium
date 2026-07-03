package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By nameField = AppiumBy.id("com.androidsample.generalstore:id/nameField");
    private final By countrySpinner = AppiumBy.id("com.androidsample.generalstore:id/spinnerCountry");
    private final By letsShopButton = AppiumBy.id("com.androidsample.generalstore:id/btnLetsShop");

    public LoginPage(AndroidDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(nameField)).isDisplayed();
    }

    public LoginPage enterName(String name) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
        field.clear();
        field.sendKeys(name);
        return this;
    }

    public ProductsPage tapLetsShop() {
        wait.until(ExpectedConditions.elementToBeClickable(letsShopButton)).click();
        return new ProductsPage(driver, wait);
    }

    public ProductsPage loginAs(String name) {
        isLoaded();
        enterName(name);
        return tapLetsShop();
    }
}
