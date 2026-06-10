---
name: generalstore-domain
description: General Store mobile application domain knowledge — business rules, mobile flows, validation logic, architecture, and error scenarios. Use when designing Appium Java tests or framework.
argument-hint: "scenario"
user-invocable: false
---

# General Store Mobile Domain Knowledge

## Overview
General Store is an Android-based ecommerce mobile application used for automation testing. Users fill a registration form, browse products, add items to cart, validate pricing, and complete checkout through a webview.

The app follows a linear journey:
Form → Product List → Cart → Checkout (Webview)

---

## Tech Stack
- Platform: Android (APK)
- Automation: Appium
- Language: Java
- Framework: TestNG / JUnit
- Driver: UiAutomator2
- Pattern: Page Object Model (POM)
- App Type: Hybrid (Native + Webview)

---

## Data Models

### User
| Field | Type | Notes |
|------|------|------|
| name | String | Required |
| gender | String | Male/Female |
| country | String | Required |

---

### Product
| Field | Type | Notes |
|------|------|------|
| name | String | Displayed in list |
| price | String | "$xx.xx" format |
| index | Int | Position in list |

---

### Cart
| Field | Type | Notes |
|------|------|------|
| products | List | Selected items |
| totalAmount | Decimal | Calculated value |

---

## Application Flow

1. Launch app
2. Fill form details
3. Navigate to product page
4. Add products to cart
5. Open cart
6. Validate total
7. Accept terms
8. Proceed to checkout
9. Switch to webview


## Detailed Knowledge (Sub-Files)

Load these based on what the current task needs:

- **Business rules & validation logic** → read `./business-rules.md`
- **User flows, test scenarios & test data** → read `./user-flows.md`
