# BrightSaah_POS
Bright Saah POS is a GUI-based Java Swing desktop application built for the assignment topic "Point of Sale (POS) System". It demonstrates object-oriented programming, event-driven GUI design, role-based access, validation, exception handling, collections, reporting, and modular Java code.

## Main Features

- Login and logout with Administrator and Cashier roles.
- Role-based navigation and access control.
- Dashboard cards for products, customers, sales today, revenue, low stock, and transactions.
- Product management with add, edit, delete, search, category, barcode, cost price, selling price, quantity, and reorder level.
- Customer management with add, edit, delete, search, email, phone, and loyalty points.
- User management for administrators.
- Sales screen with search-as-you-type product lookup and keyboard Enter-to-add support.
- Shopping cart with add, remove, update quantity, cancel sale, and checkout.
- Live subtotal, discount, VAT, and total calculations.
- Bulk and loyalty discount rules.
- Cash, card, and mobile money payment validation.
- Automatic stock reduction after checkout.
- Inventory screen with increase/reduce stock, low-stock warnings, out-of-stock status, and stock history.
- Receipt preview, print action, TXT export, and simple PDF export.
- Sales history with search by receipt, customer, cashier, or date.
- Reports for daily, weekly, and monthly sales, revenue, transactions, best-selling products, least-selling products, and low-stock products.
- CSV report export for spreadsheet use.
- Dark/light theme toggle.
- Smoke tests for checkout, inventory, authentication, CRUD, reports, and receipt export.

## Technologies

- Java 26
- Java Swing GUI
- IntelliJ IDEA project structure
- No external dependencies

## How To Run

From the project root:

```powershell
javac -d out (Get-ChildItem -LiteralPath src -Recurse -Filter *.java).FullName
java -cp out Main
```

 login accounts:

```text
Administrator: admin / admin123
Cashier:       cashier / cash123
```

To run the non-GUI tests:

```powershell
java -cp out tests.PosSystemSmokeTest
java -cp out tests.SmartPosFeatureTest
```

Expected smoke test output:

```text
POS smoke test passed: POS-YYYYMMDD-1001
```

## Project Structure

```text
src/
  Main.java
  exceptions/
    PosException.java
    StockException.java
    ValidationException.java
  model/
    Product.java
    RetailProduct.java
    WeightedProduct.java
    InventoryItem.java
    CartItem.java
    Customer.java
    Payment.java
    CashPayment.java
    CardPayment.java
    MobileMoneyPayment.java
    Sale.java
  service/
    InventoryService.java
    CartService.java
    SalesService.java
    ReceiptPrinter.java
    ReceiptExporter.java
    ReportService.java
    CustomerService.java
    UserService.java
    DiscountPolicy.java
    BulkDiscountPolicy.java
    LoyaltyDiscountPolicy.java
  ui/
    SmartPosFrame.java
    PosSystemFrame.java
  tests/
    PosSystemSmokeTest.java
```

## OOP Concepts Demonstrated

- Encapsulation: model fields are private and modified through methods with validation.
- Inheritance: `RetailProduct` and `WeightedProduct` extend `Product`; payment types extend `Payment`.
- Polymorphism: checkout accepts the abstract `Payment` type and each payment validates differently.
- Abstraction: `Product`, `Payment`, `User`, and `DiscountPolicy` define common contracts for concrete classes.

## Suggested GitHub Submission Checklist

- Commit all source files and documentation.
- Add real application screenshots to `screenshots/`.
- Add generated sample receipts/reports if your lecturer wants output artifacts.
- Update `TECHNICAL_REPORT.md` with your name, course details, and GitHub repository link.
- Export the technical report to PDF or Word if required by your lecturer.

## Screenshots

1. Login screen


<img width="1364" height="841" alt="login_screen" src="https://github.com/user-attachments/assets/d2eb2f02-aac3-4d3d-9a9e-04a4e320a0fd" />

2. Dashboard Screen
   
<img width="1364" height="841" alt="dashboard_screen" src="https://github.com/user-attachments/assets/29d8398c-dbe5-4606-9388-6ef0e1570e98" />

3. Products Screen

<img width="1364" height="841" alt="products_screen" src="https://github.com/user-attachments/assets/51779e61-85c1-4d9b-b314-947c6e817b94" />


4. Customer Screen
   
<img width="1364" height="841" alt="customers_screen" src="https://github.com/user-attachments/assets/b037b89b-43a4-4023-a891-f23fc40d64c2" />

5. Inventory Screen
  <img width="1364" height="841" alt="inventory_screen" src="https://github.com/user-attachments/assets/d5e1e114-aba8-48a7-ac24-ac499ee9e749" />



