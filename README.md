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
![image](https://github.com/user-attachments/assets/bec51212-66aa-499b-9757-c8dff64f5e81)


# ER Diagram

This ER diagram models Users, Admins, Holdings, and Logs with relationships among them.

- A User can have multiple Holdings and can generate multiple Logs when transactions occur.  
- Holdings store data about the cryptocurrencies owned, such as coin name, quantity, buy price, and price alerts.  
- Logs record detailed transaction history including buy/sell prices and dates.  
- An Admin can review logs but does not directly manage holdings or users.  

---
![image](https://github.com/user-attachments/assets/afe73b68-558e-45cf-8828-104b9260df93)


# Database Diagram

This database diagram models the core components of a Crypto Portfolio Tracker application, including users, holdings, transaction logs, and external coin data.  
It consists of four main tables: `users`, `holdings`, `logs`, and `coin_API`.

---
![image](https://github.com/user-attachments/assets/8793bd4c-28bf-4bc6-bec9-28d8ce6f66e2)


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
```
com.example.CryptoPortfolioTracker
├── config
│ └── SecurityConfig.java
├── controller
│ ├── AdminController.java
│ ├── AlertController.java
│ ├── CryptoController.java
│ ├── CryptoControllerToGetPriceSymbol.java
│ ├── HoldingController.java
│ └── UserController.java
├── dto
│ ├── AddHoldingRequest.java
│ ├── AlertRequestDTO.java
│ ├── ClientResponse.java
│ ├── CryptoData.java
│ ├── LoginRequest.java
│ ├── PriceResponse.java
│ └── RegisterRequest.java
├── entity
│ ├── Alert.java
│ ├── Holding.java
│ ├── Log.java
│ ├── PriceAlert.java
│ └── User.java
├── enums
│ ├── AlertDirection.java
│ ├── AlertStatus.java
│ └── Role.java
├── exception
│ ├── GlobalExceptionHandler.java
│ └── ResourceNotFoundException.java
├── model
│ └── ApiResponse.java
├── repository
│ ├── AlertRepository.java
│ ├── HoldingRepository.java
│ └── UserRepository.java
├── scheduler
│ └── AlertSchedulerService.java
├── service
│ ├── Implementation
│ │ └── AdminServiceImpl.java
│ ├── AdminService.java
│ ├── AlertService.java
│ ├── CryptoService.java
│ ├── CustomUserDetailsService.java
│ ├── EmailService.java
│ ├── HoldingService.java
│ ├── NotificationService.java
│ ├── PriceService.java
│ └── UserService.java
├── CryptoPortfolioTrackerApplication.java
└── README.md
```


---

# 🚀 How to Run

## ✅ Prerequisites

- Java 17+  
- Maven  
- MySQL  
- Postman (for testing APIs)  

## 🧪 Steps

1. Clone the project:  
git clone https://github.com/your-username/CryptoPortfolioTracker.git
cd CryptoPortfolioTracker
2. Create a database:
    CREATE DATABASE CryptoPortfolioTracker;
3. Configure application.properties:
    spring.application.name=CryptoPortfolioTracker
    spring.datasource.url=jdbc:mysql://10.9.124.199:3306/CryptoPortfolioTracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=root
    spring.datasource.password=password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    
    spring.mail.host=smtp-relay.brevo.com
    spring.mail.port=587
    spring.mail.username=8e3121001@smtp-brevo.com
    spring.mail.password=h1nr2WQwZysT5zVg
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
4. Build and run:
    ./mvnw clean install
    ./mvnw spring-boot:run
5. Test using Swagger or Postman.

### 📦 Optional Enhancements

| Feature                       | Benefit                         |
| ----------------------------- | ------------------------------- |
| Email alerts                  | Better user notification        |
| Graphical reports (charts)    | Easier to visualize performance |
| Export portfolio to PDF/Excel | For offline record-keeping      |
| Mobile app integration        | Future frontend possibilities   |
| WebSocket price updates       | Live price tracking             |

### 👩‍💻 Contributors

| Name              | Role                                                                                                                   |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------- |
| Praveen           | Project Lead, User Module, GlobalExceptionHandling Module, Basic authentication using session\_token & Scheduler Logic |
| Aayushi           | Alert module                                                                                                           |
| Kaushik, Harshnie | Price Fetcher, Gain Calculator                                                                                         |
| Navya             | Fetch API Module                                                                                                       |
| Lakshmi Bhanu     | Admin Module and Junit testing                                                                                         |
