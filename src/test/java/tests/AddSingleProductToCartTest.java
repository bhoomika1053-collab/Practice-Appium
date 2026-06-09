package tests;

import base.BaseTest;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class AddSingleProductToCartTest extends BaseTest {

    @Test(description = "[KAN-2] Add a single product to cart and verify in cart")
    public void addSingleProductToCart() {
        By nameField = AppiumBy.id("com.androidsample.generalstore:id/nameField");
        By countrySpinner = AppiumBy.id("com.androidsample.generalstore:id/spinnerCountry");
        By countryName = AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(\"Argentina\"))"
        );
        By maleRadio = AppiumBy.id("com.androidsample.generalstore:id/radioMale");
        By letsShopButton = AppiumBy.id("com.androidsample.generalstore:id/btnLetsShop");

        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField)).sendKeys("KAN2 User");
        driver.findElement(countrySpinner).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(countryName)).click();
        driver.findElement(maleRadio).click();
        driver.findElement(letsShopButton).click();

        By firstProductName = AppiumBy.xpath("(//android.widget.TextView[@resource-id='com.androidsample.generalstore:id/productName'])[1]");
        By firstAddToCartButton = AppiumBy.xpath("(//android.widget.TextView[@resource-id='com.androidsample.generalstore:id/productAddCart'])[1]");

        String selectedProductName = wait.until(ExpectedConditions.visibilityOfElementLocated(firstProductName)).getText();

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(firstAddToCartButton));
        Assert.assertEquals(addButton.getText(), "ADD TO CART", "Initial state should be ADD TO CART");
        addButton.click();

        wait.until(driverRef -> driver.findElement(firstAddToCartButton).getText().contains("ADDED"));
        Assert.assertTrue(
            driver.findElement(firstAddToCartButton).getText().contains("ADDED"),
            "Button should change to added state after selecting product"
        );

        By cartButton = AppiumBy.id("com.androidsample.generalstore:id/appbar_btn_cart");
        driver.findElement(cartButton).click();

        By cartHeader = AppiumBy.id("com.androidsample.generalstore:id/toolbar_title");
        Assert.assertEquals(
                wait.until(ExpectedConditions.visibilityOfElementLocated(cartHeader)).getText(),
                "Cart",
                "Cart page should open"
        );

        By cartProductNames = AppiumBy.id("com.androidsample.generalstore:id/productName");
        By cartProductPrices = AppiumBy.id("com.androidsample.generalstore:id/productPrice");

        List<WebElement> names = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartProductNames));
        List<WebElement> prices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartProductPrices));

        Assert.assertFalse(names.isEmpty(), "Cart should contain at least one product");
        Assert.assertEquals(names.get(0).getText(), selectedProductName, "Selected product name should match in cart");
        Assert.assertFalse(prices.get(0).getText().trim().isEmpty(), "Selected product should have visible price in cart");

        List<WebElement> badges = driver.findElements(AppiumBy.id("com.androidsample.generalstore:id/counterText"));
        if (!badges.isEmpty()) {
            Assert.assertEquals(badges.get(0).getText(), "1", "Cart badge should show one item");
        }
    }
}
