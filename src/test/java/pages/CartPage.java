package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class CartPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By cartProductList = AppiumBy.id("com.androidsample.generalstore:id/rvCartProductList");
    private final By cartProductNames = AppiumBy.id("com.androidsample.generalstore:id/productName");
    private final By proceedButton = AppiumBy.id("com.androidsample.generalstore:id/btnProceed");

    public CartPage(AndroidDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartProductList)).isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(proceedButton)).isDisplayed();
    }

    public List<String> getCartProductNames() {
        List<WebElement> names = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(cartProductNames, 0));
        return names.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public int getCartItemCount() {
        return getCartProductNames().size();
    }
}
