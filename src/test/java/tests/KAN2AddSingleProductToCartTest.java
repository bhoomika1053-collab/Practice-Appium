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

    private static final By COUNTRY_DROPDOWN = AppiumBy.id("com.androidsample.generalstore:id/spinnerCountry");
    private static final By NAME_FIELD = AppiumBy.id("com.androidsample.generalstore:id/nameField");
    private static final By LETS_SHOP_BUTTON = AppiumBy.id("com.androidsample.generalstore:id/btnLetsShop");

    private static final By PRODUCT_NAME = AppiumBy.id("com.androidsample.generalstore:id/productName");
    private static final By ADD_TO_CART_BUTTON = AppiumBy.id("com.androidsample.generalstore:id/productAddCart");
    private static final By CART_BADGE = AppiumBy.id("com.androidsample.generalstore:id/counterText");
    private static final By CART_ICON = AppiumBy.id("com.androidsample.generalstore:id/appbar_btn_cart");
    private static final By CART_PRODUCT_PRICE = AppiumBy.id("com.androidsample.generalstore:id/productPrice");

    @Test(description = "KAN-2: Add one product from Products page and verify cart details")
    public void addSingleProductToCart() {
        openProductsPage("Rahul", "India");

        List<WebElement> productNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_NAME));
        Assert.assertFalse(productNames.isEmpty(), "Products list should be visible");

        String selectedProduct = productNames.get(0).getText();

        List<WebElement> addButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(ADD_TO_CART_BUTTON));
        Assert.assertFalse(addButtons.isEmpty(), "At least one ADD TO CART button should be visible");
        addButtons.get(0).click();

        Assert.assertEquals(addButtons.get(0).getText().trim(), "ADDED TO CART",
                "Selected product button should reflect added state");

        String badgeCount = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_BADGE)).getText().trim();
        Assert.assertEquals(badgeCount, "1", "Cart badge should update to one item");

        wait.until(ExpectedConditions.elementToBeClickable(CART_ICON)).click();

        List<WebElement> cartProductNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_NAME));
        Assert.assertFalse(cartProductNames.isEmpty(), "Cart should show selected product");
        Assert.assertEquals(cartProductNames.get(0).getText().trim(), selectedProduct,
                "Cart should display the same selected product name");

        List<WebElement> cartProductPrices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(CART_PRODUCT_PRICE));
        Assert.assertFalse(cartProductPrices.isEmpty(), "Cart should display product price");
        String priceText = cartProductPrices.get(0).getText().trim();
        Assert.assertTrue(priceText.startsWith("$"), "Cart should display selected product price");
    }

    private void openProductsPage(String userName, String country) {
        wait.until(ExpectedConditions.elementToBeClickable(COUNTRY_DROPDOWN)).click();
        By countryOption = AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().text(\""
                        + country
                        + "\"));");
        wait.until(ExpectedConditions.elementToBeClickable(countryOption)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(NAME_FIELD)).sendKeys(userName);
        wait.until(ExpectedConditions.elementToBeClickable(LETS_SHOP_BUTTON)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_TO_CART_BUTTON));
    }
}
