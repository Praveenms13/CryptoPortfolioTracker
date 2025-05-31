# ✅ Crypto Portfolio Tracker (Spring Boot Backend)

A backend application that helps users manage and monitor their cryptocurrency investments. Users can track their coin holdings, view real-time prices, calculate profit/loss, and set up price alerts.

---

# 🎯 Objective

To build a secure and user-friendly backend system where individuals can:

- Add, edit, and delete cryptocurrency holdings  
- Track real-time prices using third-party APIs (e.g., CoinGecko)  
- View profit/loss calculations for individual coins and total portfolio  
- Get alerts when coins hit specific price thresholds  

---

# 🧩 Core Features (Explained Simply for Freshers)

## 🔐 1. User Authentication Module

**Purpose:** Protect user data and allow secure access.  
**Functionalities:**  
- ✅ Register and Login functionality  
- ✅ Basic authentication using session_token  
- ✅ Each user sees only their holdings  

**What You’ll Learn:**  
- How Spring Security works  
- Securing API routes using roles  
- Session management and validation  

## 📈 2. Holdings Management Module

**Purpose:** Allow users to record and manage their crypto investments.  
**Functionalities:**  
- Add new holdings:  
    - Coin Symbol (e.g., BTC)  
    - Quantity (how much they own)  
    - Buy Price (price when bought)  
- Edit and Delete entries  
- View current portfolio  

**What You’ll Learn:**  
- Building CRUD APIs with Spring Boot  
- Using JPA for database operations  
- Filtering user-specific data  

## 🌐 3. Real-Time Price Fetcher

**Purpose:** Keep prices up to date using real market data.  
**Functionalities:**  
- Integrate with CoinGecko or similar public APIs  
- Fetch latest prices every 5–10 minutes using `@Scheduled`  
- Store prices in a local database or in-memory  

**What You’ll Learn:**  
- Making REST API calls with `RestTemplate`  
- Using `@Scheduled` to run background tasks  
- Parsing and mapping JSON to Java objects using Jackson  
- Database design for price history  

## 📊 4. Gain/Loss Calculator

**Purpose:** Show how each holding is performing.  
**Functionalities:**  
- For each coin, calculate:  
  - gain = (currentPrice - buyPrice) * quantity  
  - percentageChange = (gain / (buyPrice * quantity)) * 100  
- Show total portfolio profit/loss  

**What You’ll Learn:**  
- Writing business logic in service layers  
- Performing math operations in Java  
- Sending calculated results via REST APIs  

## 🔔 5. Price Alert Module

**Purpose:** Notify users when their coin hits a target price.  
**Functionalities:**  
- Users can set a price alert (e.g., ETH > $3,000)  
- The system checks prices on each cron job  
- If the condition is met:  
  - Mark the alert as “Triggered”  
  - Optionally log it or send an email  

**What You’ll Learn:**  
- Writing condition-based business logic  
- Storing and updating alert status  
- Optional: Logging or sending notifications  

---

# ⚙️ Tech Stack Overview

| Layer         | Technology                       |
|---------------|--------------------------------|
| Language      | Java                           |
| Framework     | Spring Boot, Spring Data JPA    |
| Security      | Spring Security, Basic authentication using session_token |
| Scheduler     | Spring Scheduler (`@Scheduled`) |
| REST Client   | RestTemplate / WebClient        |
| JSON Parser   | Jackson                        |
| Database      | MySQL                          |
| Build Tool    | Maven                          |

---

# 🧪 API Module Overview

### 📌 User Controller
- POST `/api/auth/register` – Register a new user  
- POST `/api/auth/login` – Login and receive token  
- PUT `/api/users/updateUser/{id}` – Update user details  
- GET `/api/users/getUser/{id}` – Get user details by ID  
- GET `/api/users/getId` – Get currently logged-in user's ID  

### 📌 Holdings
- GET `/api/holdings/add` – Add a new cryptocurrency holding  
- POST `/api/holdings/getHoldings` – Get current user's holdings  
- PUT `/api/holdings/getMyNetValue` – Get total portfolio value for current user  

### 📌 Price Alerts
- POST `/api/alert/create` – Create a new price alert  
- GET `/api/alert/user/{userid}` – List all alerts  
- GET `/api/alerts/user/{userid}/triggered` – List all triggered alerts  
- PUT `/api/alert/{id}` – Update a price alert  
- PUT `/api/alert/{id}/disable` – Disable a specific alert  
- DELETE `/api/alert/{id}` – Delete a price alert  

### 📌 Admin Controller
- POST `/api/admin/login` – Login as admin  
- GET `/api/admin/users` – View all users  
- GET `/api/admin/user/{id}` – Get user details by ID (admin access)  

### 📌 Crypto Controller
- GET `/api/crypto/getPrice/{symbol}` – Get current price of a crypto by symbol  
- GET `/api/crypto/getAllCryptos` – Get list of all supported cryptocurrencies  

---

# Class Diagram

This diagram represents a stock portfolio management system with layers:  
Controllers, DTO, Enum, Model, Repository, Services, and Entities.  
It includes user authentication, alert generation, portfolio creation, and gain/loss calculation.  
Entities like User, PriceAlert, Alert, Holding, and Log manage data and behavior.  
Services like PriceService, AlertService, AlertSchedulerService, AdminService, CryptoService, CustomUserDetailsService, EmailService, HoldingService, NotificationService, and UserService provide business logic.

---

# ER Diagram

This ER diagram models Users, Admins, Holdings, and Logs with relationships among them.

- A User can have multiple Holdings and can generate multiple Logs when transactions occur.  
- Holdings store data about the cryptocurrencies owned, such as coin name, quantity, buy price, and price alerts.  
- Logs record detailed transaction history including buy/sell prices and dates.  
- An Admin can review logs but does not directly manage holdings or users.  

---

# Database Diagram

This database diagram models the core components of a Crypto Portfolio Tracker application, including users, holdings, transaction logs, and external coin data.  
It consists of four main tables: `users`, `holdings`, `logs`, and `coin_API`.

---

# 🧠 Key Concepts for Freshers

| Area           | What You’ll Learn                                       |
|----------------|--------------------------------------------------------|
| Spring Boot APIs| Build and secure backend routes                         |
| Security       | Basic authentication login using session_token and access control |
| Scheduling     | Automate background tasks like price fetcher            |
| API Integration| Call real-world APIs using Java                          |
| JSON Handling  | Deserialize API responses with Jackson                   |
| Logic & Math   | Calculate gains, losses, and percentages                 |
| Alerts        | Apply business rules (if-else, comparisons)              |

---

# 🗂 Project Structure

