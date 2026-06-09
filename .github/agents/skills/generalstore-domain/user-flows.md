# General Store Mobile Application User Flows & Test Data
## (Appium + Java + TestNG)

---

# Flow 1: User Login

1. Launch General Store mobile application
2. Select country from dropdown
3. Enter user name
4. Click "Let’s Shop" button
5. Navigate to Products page

## Validation Rules

- Name field is mandatory
- Country selection is mandatory
- Toast message appears if name is empty

### Expected Toast Message

```text
Please enter your name
```

---

# Flow 2: Browse & Select Products

1. User lands on Products page
2. Scroll through available product list
3. View:
   - Product name
   - Product price
   - Product image
   - Add To Cart button
4. Click "Add To Cart" on selected products
5. Cart badge updates automatically
6. Navigate to Cart page

---

# Flow 3: Add Products to Cart

1. Select one or multiple products
2. Click "Add To Cart"
3. Verify button changes:
   - `ADD TO CART → ADDED TO CART`
4. Verify cart count updates
5. Open Cart page

## Cart Rules

- Same product should not be added multiple times
- Cart updates immediately after product addition

---

# Flow 4: Cart & Checkout

1. Navigate to Cart screen
2. Verify selected products are listed
3. Verify:
   - Product names
   - Product prices
   - Total amount
4. Long press Terms & Conditions button
5. Terms popup appears
6. Accept terms
7. Click Proceed button
8. WebView launches successfully

---

# Flow 5: WebView Navigation

1. Click Proceed from Cart page
2. Navigate to WebView page
3. Interact with search field
4. Perform search operation
5. Navigate back to native application

## Validation Rules

- WebView should load successfully
- User should return back to app correctly

---

# Flow 6: Cart Management

1. User adds multiple products to cart
2. User removes a product
3. Verify cart total recalculates
4. User clears complete cart

## Validation Rules

- Cart total updates immediately
- Cart count updates immediately
- Removed items disappear from cart

---

# Flow 7: Session Management

1. User launches app
2. Adds products into cart
3. Closes application
4. Relaunches app

## Validation Rules

- Temporary cart/session data resets properly
- New session starts successfully

---

# Flow 8: Scroll & Product Visibility

1. Navigate to Products page
2. Scroll till final product appears
3. Validate all products load properly

## Validation Rules

- Product scrolling should work smoothly
- User should reach last visible product

---

# Test Data

# Supported Countries

- Argentina
- Australia
- Brazil
- India
- USA

---


# Test Users

| User Type | Name        | Country |
|------------|-------------|----------|
| Valid User | Rahul       | India |
| Valid User | John        | USA |
| Valid User | Maria       | Brazil |
| Empty User | ""          | India |

---
