package tests;

import base.BaseTest;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class KAN2AddSingleProductToCartTest extends BaseTest {

    private static final By NAME_FIELD = AppiumBy.id("com.androidsample.generalstore:id/nameField");
    private static final By COUNTRY_DROPDOWN = AppiumBy.id("com.androidsample.generalstore:id/spinnerCountry");
    private static final By FEMALE_RADIO = AppiumBy.id("com.androidsample.generalstore:id/radioFemale");
    private static final By LETS_SHOP_BUTTON = AppiumBy.id("com.androidsample.generalstore:id/btnLetsShop");

    private static final By CART_ICON = AppiumBy.id("com.androidsample.generalstore:id/appbar_btn_cart");
    private static final By PRODUCT_NAME = AppiumBy.id("com.androidsample.generalstore:id/productName");
    private static final By PRODUCT_PRICE = AppiumBy.id("com.androidsample.generalstore:id/productPrice");
    private static final By ADD_TO_CART_BUTTON = AppiumBy.id("com.androidsample.generalstore:id/productAddCart");
    private static final By CART_BADGE = AppiumBy.id("com.androidsample.generalstore:id/counterText");

    @Test
    public void addSingleProductToCartUpdatesBadgeAndShowsProductInCart() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(NAME_FIELD)).sendKeys("Rahul");
        driver.findElement(FEMALE_RADIO).click();
        driver.findElement(COUNTRY_DROPDOWN).click();

        By indiaOption = AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(text(\"India\"))");
        wait.until(ExpectedConditions.visibilityOfElementLocated(indiaOption)).click();
        driver.findElement(LETS_SHOP_BUTTON).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(CART_ICON));

        List<WebElement> productNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_NAME));
        List<WebElement> productPrices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_PRICE));
        List<WebElement> addToCartButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(ADD_TO_CART_BUTTON));

        Assert.assertFalse(productNames.isEmpty(), "At least one product should be visible on the products page");
        Assert.assertEquals(productNames.size(), productPrices.size(), "Each product should have a matching price");

        String selectedProductName = productNames.get(0).getText();
        String selectedProductPrice = productPrices.get(0).getText();

        WebElement firstAddButton = addToCartButtons.get(0);
        firstAddButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(firstAddButton, "ADDED TO CART"));
        Assert.assertEquals(firstAddButton.getText(), "ADDED TO CART", "Button state should change after adding product");

        WebElement cartBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_BADGE));
        Assert.assertEquals(cartBadge.getText().trim(), "1", "Cart badge should update to one item");

        driver.findElement(CART_ICON).click();

        List<WebElement> cartProductNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_NAME));
        List<WebElement> cartProductPrices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_PRICE));

        Assert.assertFalse(cartProductNames.isEmpty(), "Cart should display at least one product");
        Assert.assertEquals(cartProductNames.get(0).getText(), selectedProductName, "Cart product name should match selected product");
        Assert.assertEquals(cartProductPrices.get(0).getText(), selectedProductPrice, "Cart product price should match selected product");
    }
}