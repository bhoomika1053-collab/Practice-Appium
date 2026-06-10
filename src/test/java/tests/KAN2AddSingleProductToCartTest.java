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

    private static final String APP_PACKAGE = "com.androidsample.generalstore";

    private static final By NAME_FIELD = AppiumBy.id(APP_PACKAGE + ":id/nameField");
    private static final By COUNTRY_DROPDOWN = AppiumBy.id(APP_PACKAGE + ":id/spinnerCountry");
    private static final By LETS_SHOP_BUTTON = AppiumBy.id(APP_PACKAGE + ":id/btnLetsShop");
    private static final By PRODUCT_NAME = AppiumBy.id(APP_PACKAGE + ":id/productName");
    private static final By PRODUCT_PRICE = AppiumBy.id(APP_PACKAGE + ":id/productPrice");
    private static final By ADD_TO_CART_BUTTON = AppiumBy.id(APP_PACKAGE + ":id/productAddCart");
    private static final By CART_BADGE = AppiumBy.id(APP_PACKAGE + ":id/counterText");
    private static final By CART_ICON = AppiumBy.id(APP_PACKAGE + ":id/appbar_btn_cart");

    @Test(description = "KAN-2: Add single product to cart and verify cart state")
    public void addSingleProductToCart() {
        enterUserDetailsAndOpenProductsPage("Rahul", "India");

        List<WebElement> productNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_NAME));
        List<WebElement> productPrices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_PRICE));
        List<WebElement> addButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(ADD_TO_CART_BUTTON));

        Assert.assertFalse(addButtons.isEmpty(), "At least one ADD TO CART button should be present");
        String selectedProductName = productNames.get(0).getText().trim();
        String selectedProductPrice = productPrices.get(0).getText().trim();

        WebElement selectedProductButton = addButtons.get(0);
        selectedProductButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(selectedProductButton, "ADDED TO CART"));
        Assert.assertEquals(
                selectedProductButton.getText().trim(),
                "ADDED TO CART",
                "Selected product button should reflect added state"
        );

        WebElement cartBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_BADGE));
        Assert.assertEquals(cartBadge.getText().trim(), "1", "Cart badge should update to 1 immediately");

        driver.findElement(CART_ICON).click();

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_NAME));
        List<WebElement> cartProductNames = driver.findElements(PRODUCT_NAME);
        List<WebElement> cartProductPrices = driver.findElements(PRODUCT_PRICE);

        int productIndexInCart = findProductIndex(cartProductNames, selectedProductName);
        Assert.assertTrue(productIndexInCart >= 0, "Selected product should appear in cart");
        Assert.assertEquals(
                cartProductPrices.get(productIndexInCart).getText().trim(),
                selectedProductPrice,
                "Selected product price in cart should match product page price"
        );
    }

    private void enterUserDetailsAndOpenProductsPage(String userName, String country) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(NAME_FIELD)).sendKeys(userName);

        try {
            driver.hideKeyboard();
        } catch (Exception ignored) {
            // Keyboard might already be hidden on some devices/emulators.
        }

        driver.findElement(COUNTRY_DROPDOWN).click();
        By countryOption = AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(" +
                        "new UiSelector().text(\"" + country + "\"));"
        );
        wait.until(ExpectedConditions.visibilityOfElementLocated(countryOption)).click();

        driver.findElement(LETS_SHOP_BUTTON).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_TO_CART_BUTTON));
    }

    private int findProductIndex(List<WebElement> productElements, String expectedName) {
        for (int i = 0; i < productElements.size(); i++) {
            if (productElements.get(i).getText().trim().equals(expectedName)) {
                return i;
            }
        }
        return -1;
    }
}
