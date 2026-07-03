package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductsPage;

/**
 * KAN-1 / TC-001: Add Single Product To Cart From Product List.
 *
 * Business rule: User can add products to cart and cart updates immediately
 * after product addition.
 */
public class AddSingleProductToCartTest extends BaseTest {

    private static final String USER_NAME = "Rahul";

    @Test(description = "Add a single product to the cart and verify it appears in the cart")
    public void addSingleProductToCart() {
        LoginPage loginPage = new LoginPage(driver, wait);
        ProductsPage productsPage = loginPage.loginAs(USER_NAME);

        Assert.assertTrue(productsPage.isLoaded(),
                "Products page should be displayed after login");

        String firstProductName = productsPage.getFirstProductName();
        Assert.assertFalse(firstProductName.isEmpty(),
                "A product with a name should be visible on the Products page");

        productsPage.addFirstProductToCart();
        Assert.assertEquals(productsPage.getFirstAddToCartButtonText(), "ADDED TO CART",
                "Add-to-cart button should change to ADDED TO CART after adding the product");

        CartPage cartPage = productsPage.openCart();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be displayed");

        Assert.assertEquals(cartPage.getCartItemCount(), 1,
                "Cart should contain exactly one selected item");
        Assert.assertTrue(cartPage.getCartProductNames().contains(firstProductName),
                "The added product '" + firstProductName + "' should appear in the cart");
    }
}
