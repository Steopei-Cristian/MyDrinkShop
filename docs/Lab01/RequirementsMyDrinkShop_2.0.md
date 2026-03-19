# Requirements – MyDrinkShop v2.0
> Updated based on Lab01 Requirements Inspection findings (v1.0 → v2.0)

---

## 1. Overview

MyDrinkShop is a desktop Java application for managing a drinks shop. It allows operators to manage beverage products, recipes, ingredient stock, and customer orders, and to generate daily financial reports.

---

## 2. Functional Requirements

### FR-01 – Product Management
- The system SHALL allow the operator to **add**, **update**, and **delete** beverage products.
- Each product SHALL have: a unique integer ID, a name (non-empty string), a price (positive real number), a category (`CategorieBautura`), and a type (`TipBautura`).
- A product SHALL be linked to exactly one recipe (`Reteta`) via a shared ID.
- The system SHALL NOT allow adding a product if a product with the same ID already exists.
- The system SHALL validate all product fields before persisting: ID > 0, name not blank, price > 0.

### FR-02 – Beverage Categories and Types
- The system SHALL support the following beverage categories (`CategorieBautura`):
  `CLASSIC_COFFEE`, `MILK_COFFEE`, `SPECIAL_COFFEE`, `ICED_COFFEE`, `TEA`, `BUBBLE_TEA`, `JUICE`, `SMOOTHIE`, `ALL`.
- The system SHALL support the following beverage types (`TipBautura`):
  `BASIC`, `DAIRY`, `LACTOSE_FREE`, `WATER_BASED`, `PLANT_BASED`, `POWDER`, `ALL`.
- The value `ALL` SHALL act as a wildcard for filtering operations (returns all products regardless of category/type).
- The system SHALL allow filtering the product list by category and by type independently.

### FR-03 – Recipe Management
- The system SHALL allow the operator to **add**, **update**, and **delete** recipes.
- Each recipe (`Reteta`) SHALL have: a unique integer ID (matching the associated product ID) and a non-empty list of ingredients (`IngredientReteta`).
- Each ingredient SHALL have a name (non-empty string) and a required quantity (positive real number > 0).
- The system SHALL validate all recipe fields before persisting.

### FR-04 – Stock Management
- The system SHALL maintain a stock list of ingredients (`Stoc`).
- Each stock entry SHALL have: a unique integer ID, an ingredient name (non-empty string), a current quantity (real number ≥ 0), and a minimum threshold quantity (real number ≥ 0).
- The system SHALL allow the operator to **add**, **update**, and **delete** stock entries.
- The system SHALL check whether sufficient stock is available before preparing a product (i.e., before consuming ingredients for a recipe).
- **[NEW v2.0]** The system SHALL display a warning to the operator when any ingredient's current quantity falls below its minimum threshold (`stocMinim`).
- **[NEW v2.0]** Stock quantities SHALL be stored and manipulated as real numbers (`double`), not integers, to support fractional ingredient amounts.

### FR-05 – Order Management
- The system SHALL allow the operator to create a current order composed of multiple order items (`OrderItem`).
- Each order item SHALL reference a product and a quantity (positive integer, 1–10).
- The system SHALL compute the total price of an order as the sum of (product price × quantity) for all items.
- The system SHALL allow the operator to finalize and save an order.
- **[NEW v2.0]** Order IDs SHALL be generated sequentially, starting at 1, incrementing by 1 for each new order within a session. The ID of a saved order SHALL be unique within the data file.
- **[NEW v2.0]** The system SHALL NOT allow finalizing an empty order (an order with no items).
- **[NEW v2.0]** If a product referenced in a saved order is subsequently deleted, the system SHALL handle the missing product gracefully (display "N/A" instead of crashing).

### FR-06 – Stock Consumption on Product Preparation
- The system SHALL consume the ingredients listed in a product's recipe from the stock when `comandaProdus()` is called.
- The system SHALL throw a descriptive error if stock is insufficient, specifying the product name.
- The system SHALL throw a descriptive error if no recipe exists for the requested product.

### FR-07 – Receipt Generation
- The system SHALL generate a text receipt for a finalized order containing:
  - Order ID
  - For each item: product name, unit price, quantity, and line total
  - Grand total in RON
- **[NEW v2.0]** The system SHALL handle the case where a product in the order is not found in the product list (e.g., product was deleted after order creation) without throwing an exception — display "Unknown product" instead.

### FR-08 – CSV Export
- The system SHALL export all orders to a CSV file at a user-specified path.
- **[NEW v2.0]** The export file SHALL conform to standard CSV format: all lines SHALL be comma-separated values. Summary lines (order totals, daily total) SHALL be placed in dedicated columns or in a separate section clearly demarcated, not mixed with data rows.
- The export SHALL include: order ID, product name, quantity, line price, order subtotal, and the daily total with the current date.

### FR-09 – Daily Revenue Report
- The system SHALL display the total revenue from all orders for the current session.
- The total revenue SHALL equal the sum of `totalPrice` across all saved orders.

---

## 3. Non-Functional Requirements

### NFR-01 – Platform and Environment
- **[NEW v2.0]** The application SHALL run on **Java 17 or higher** with the Java Platform Module System (JPMS) enabled (`module-info.java`).
- **[NEW v2.0]** The application SHALL use **JavaFX 17 or higher** for the graphical user interface.
- **[NEW v2.0]** The application SHALL be compatible with **Windows 10/11**, **macOS 12+**, and **Ubuntu 20.04+**.
- The build system SHALL be **Apache Maven**.

### NFR-02 – Data Persistence
- All data SHALL be persisted in plain text files located in the `data/` directory:
  - `data/products.txt` – product records
  - `data/orders.txt` – order records
  - `data/retete.txt` – recipe records
  - `data/stocuri.txt` – stock records
- **[NEW v2.0]** If a data file does not exist at startup, the application SHALL create an empty file and start with an empty repository, without throwing an exception.
- **[NEW v2.0]** The system SHALL initialize all repositories by loading data from the corresponding files at application startup.

### NFR-03 – Architecture
- The application SHALL follow a **3-layer architecture**: Presentation (UI), Business Logic (Service), Data Access (Repository).
- The Repository layer SHALL use the generic `Repository<ID, E>` interface for all data access.
- The Service layer SHALL use `Validator<T>` implementations to validate entities before persistence.
- The `DrinkShopService` class SHALL act as a **Facade** delegating to specialized services (`ProductService`, `OrderService`, `RetetaService`, `StocService`).
- The file repository layer SHALL use the **Template Method** pattern via `FileAbstractRepository<ID, E>`.

### NFR-04 – Input Validation
- **[NEW v2.0]** All user input entered via text fields (product name, price, ingredient name, ingredient quantity) SHALL be validated before processing.
- **[NEW v2.0]** If the user enters a non-numeric value in a numeric field (e.g., price, quantity), the system SHALL display a user-friendly error message and SHALL NOT throw an unhandled exception.
- Validation errors SHALL be reported as a `ValidationException` containing all validation error messages separated by newlines.

### NFR-05 – Error Handling
- **[NEW v2.0]** The system SHALL define a consistent error-handling strategy:
  - Domain validation errors → `ValidationException`
  - Business rule violations (insufficient stock, missing recipe) → `IllegalStateException` with descriptive message
  - I/O errors in file repositories → logged to standard error; the operation SHALL NOT silently fail
- **[NEW v2.0]** The UI layer SHALL catch all exceptions from the service layer and display them to the operator via an error dialog, without crashing the application.

---

## 4. System Initialization

- **[NEW v2.0]** On first launch (empty data files), the system SHALL start with zero products, zero orders, zero recipes, and zero stock entries. No demo data is pre-loaded.
- **[NEW v2.0]** All four data files (`products.txt`, `orders.txt`, `retete.txt`, `stocuri.txt`) SHALL be loaded on startup via `loadFromFile()` in each repository's constructor.

---

## 5. Constraints

- The application is a **single-user desktop application** — no multi-user or concurrent access support is required.
- No authentication or user role management is required in v2.0.
- The application SHALL NOT require a network connection.

---

*Document version: 2.0 — Updated 2026-03-19 following Lab01 Requirements & Code Inspection.*
