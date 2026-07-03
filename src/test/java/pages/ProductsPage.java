package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ProductsPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By productList = AppiumBy.id("com.androidsample.generalstore:id/rvProductList");
    private final By productNames = AppiumBy.id("com.androidsample.generalstore:id/productName");
    private final By addToCartButtons = AppiumBy.id("com.androidsample.generalstore:id/productAddCart");
    private final By cartButton = AppiumBy.id("com.androidsample.generalstore:id/appbar_btn_cart");

    public ProductsPage(AndroidDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(productList)).isDisplayed();
    }

    public String getFirstProductName() {
        List<WebElement> names = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(productNames, 0));
        return names.get(0).getText();
    }

    public ProductsPage addFirstProductToCart() {
        List<WebElement> buttons = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(addToCartButtons, 0));
        buttons.get(0).click();
        return this;
    }

    public String getFirstAddToCartButtonText() {
        List<WebElement> buttons = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(addToCartButtons, 0));
        return buttons.get(0).getText();
    }

    public CartPage openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartButton)).click();
        return new CartPage(driver, wait);
    }
}
