# General Store Mobile Application Business Rules
## Automation Testing Business Rules for Appium + Java + TestNG

---

# 1. User Journey

## Application Flow

- User launches the General Store mobile application.
- User selects country from dropdown.
- User enters name.
- User proceeds to shopping screen.
- User browses available products.
- User selects products.
- User adds products to cart.
- User opens cart page.
- User completes checkout process.

---

# 2. Login & User Validation Rules

## Mandatory Name Validation

- User name field is mandatory.
- User should not proceed without entering a name.

## Country Selection Validation

- User must select a country before proceeding.

## Error Handling

- If user clicks proceed without entering name:
  - Toast message should appear.

### Expected Toast Message

```text
Please enter your name
```

---

# 3. Product Listing Rules

## Product Visibility

- Products should be displayed on the products page.
- Each product should contain:
  - Product Name
  - Product Price
  - Product Image
  - Add To Cart button

## Product Scroll Behavior

- Product list should support vertical scrolling.

## Product Selection

- User can add multiple products into cart.
- Same product can be added only once.

---

# 4. Cart Functionality Rules

## Cart Update Rules

- Cart count should update immediately after adding products.
- Cart icon badge should display total selected items.

## Cart Product Rules

- Cart should display:
  - Product Name
  - Product Price
  - Quantity
  - Total Amount

## Remove Product Rules

- User can remove product from cart.
- Cart total should update immediately after removal.

## Clear Cart Rules

- User can clear all cart items in one action.

---

# 5. Price Calculation Rules

## Product Price Rule

- Product price is treated as per-item price.

## Total Amount Formula

```text
Total Amount = Sum of all selected product prices
```

## Cart Total Validation

- Displayed cart total must match calculated product sum.

---

# 6. Checkout Rules

## Checkout Preconditions

- User must have at least one item in cart before checkout.

## Terms & Conditions Rules

- User must accept Terms and Conditions before proceeding.

## Visit Website Feature

- User can proceed to web view after checkout.
- WebView should launch successfully.

---

# 7. WebView Rules

## WebView Launch

- Clicking Proceed button should open WebView page.

## Search Validation

- User should be able to interact with search field.

## Navigation Validation

- User should be able to:
  - Navigate back to app
  - Return from WebView successfully

---

# 8. Sandbox Isolation Rules

## User Session Isolation

- Each app session should maintain independent cart data.

## Session Reset Rules

- Relaunching application should reset temporary session/cart data.

---

# 9. Product Availability Rules

## Product Display Rules

- Static products are visible to all users.

## Product Count Rules

- Products page should display products correctly during scroll.

## Pagination / Scroll Rules

- User should be able to scroll until final product is visible.

---

# 10. Validation Message Rules

## Name Missing Validation

### Expected Message

```text
Please enter your name
```

## Terms Validation

- Terms popup should appear on long press/click.

---

# 11. UI Behavior Rules

## Loader / Transition Rules

- Screen transition should complete successfully between:
  - Login Page
  - Product Page
  - Cart Page
  - WebView Page

## Button State Rules

- Proceed button should remain clickable.
- Add To Cart button changes after product added.

### Example

```text
ADD TO CART → ADDED TO CART
```

---

# 13. Core Test Validations

## Login Validations

- Verify user cannot continue without entering name.
- Verify country selection works.
- Verify toast message validation.

## Product Validations

- Verify products are displayed.
- Verify scrolling works properly.
- Verify products can be added to cart.

## Cart Validations

- Verify cart updates immediately.
- Verify total amount calculation.
- Verify remove product functionality.
- Verify clear cart functionality.

## Checkout Validations

- Verify Terms and Conditions popup.
- Verify Proceed button functionality.
- Verify WebView launch.

## WebView Validations

- Verify search functionality inside WebView.
- Verify user navigates back successfully.

---


# 16. Recommended Test Scenarios

## Login Test Scenarios

- Login with valid details.
- Login without name.
- Login with different countries.

---

## Product Test Scenarios

- Scroll till last product.
- Add single product to cart.
- Add multiple products to cart.

---

## Cart Test Scenarios

- Verify cart item count.
- Verify cart total calculation.
- Remove product from cart.
- Clear complete cart.

---

## Checkout Test Scenarios

- Accept Terms and Conditions.
- Proceed to WebView.
- Verify successful navigation.

---

## WebView Test Scenarios

- Perform search inside WebView.
- Navigate back to native app.

---

# 17. Business Critical Rules

## Critical Validation Rules

- Name field is mandatory.
- Cart total must match product sum.
- Products must be added successfully.
- WebView must launch properly.
- User navigation should not break app flow.

---