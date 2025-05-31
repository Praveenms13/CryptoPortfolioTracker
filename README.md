# ✅ Crypto Portfolio Tracker (Spring Boot Backend)
A backend application that helps users manage and monitor their cryptocurrency investments. Users can track their coin holdings, view real-time prices, calculate profit/loss, and set up price alerts.\
________________________________________
# 🎯 Objective
To build a secure and user-friendly backend system where individuals can:\
●\tAdd, edit, and delete cryptocurrency holdings\
\
●\tTrack real-time prices using third-party APIs (e.g., CoinGecko)\
\
●\tView profit/loss calculations for individual coins and total portfolio\
\
●\tGet alerts when coins hit specific price thresholds\
_____________________________________\
# 🧩 Core Features (Explained Simply for Freshers)\
________________________________________\
# 🔐 1. User Authentication Module\
Purpose: Protect user data and allow secure access.\
Functionalities:\
●\t✅ Register and Login functionality\
●\t✅Basic authentication using session_token\
\
●\t✅ Each user sees only their holdings\
\
What You’ll Learn:\
●\tHow Spring Security works\
\
●\tSecuring API routes using roles\
●\tSession management and validation\
\
________________________________________\
# 📈 2. Holdings Management Module\
Purpose: Allow users to record and manage their crypto investments.\
Functionalities:\
●\tAdd new holdings:\
\
○\tCoin Symbol (e.g., BTC)\
\
○\tQuantity (how much they own)\
\
○\tBuy Price (price when bought)\
\
●\tEdit and Delete entries\
\
●\tView current portfolio\
\
What You’ll Learn:\
●\tBuilding CRUD APIs with Spring Boot\
\
●\tUsing JPA for database operations\
\
●\tFiltering user-specific data\
\
________________________________________\
# 🌐 3. Real-Time Price Fetcher\
Purpose: Keep prices up to date using real market data.\
Functionalities:\
●\tIntegrate with CoinGecko or similar public APIs\
\
●\tFetch latest prices every 5–10 minutes using @Scheduled\
\
●\tStore prices in a local database or in-memory\
\
What You’ll Learn:\
●\tMaking REST API calls with RestTemplate \
\
●\tUsing @Scheduled to run background tasks\
\
●\tParsing and mapping JSON to Java objects using Jackson\
●\tDatabase design for price history\
\
________________________________________\
# 📊 4. Gain/Loss Calculator\
Purpose: Show how each holding is performing.\
Functionalities:\
For each coin, calculate:\
\
 gain = (currentPrice - buyPrice) * quantity\
percentageChange = (gain / (buyPrice * quantity)) * 100\
\
●\tShow total portfolio profit/loss\
\
What You’ll Learn:\
●\tWriting business logic in service layers\
\
●\tPerforming math operations in Java\
\
●\tSending calculated results via REST APIs\
\
________________________________________\
# 🔔 5. Price Alert Module\
Purpose: Notify users when their coin hits a target price.\
Functionalities:\
●\tUsers can set a price alert (e.g., ETH > $3,000)\
\
●\tThe system checks prices on each cron job\
\
●\tIf the condition is met:\
\
○\tMark the alert as “Triggered”\
\
○\tOptionally log it or send an email\
\
What You’ll Learn:\
●\tWriting condition-based business logic\
\
●\tStoring and updating alert status\
\
●\tOptional: Logging or sending notifications\
\
________________________________________\
# ⚙️ Tech Stack Overview\
Layer\tTechnology\
Language\tJava\
Framework\tSpring Boot, Spring Data JPA\
Security\tSpring Security, Basic authentication using session_token\
Scheduler\tSpring Scheduler (@Scheduled)\
REST Client\tRestTemplate / WebClient\
JSON Parser\tJackson\
Database\tMySQL \
Build Tool\tMaven\
________________________________________\
# 🧪 API Module Overview\
# 📌 User Controller\
•\tPOST /api/auth/register – Register a new user\
\
•\tPOST /api/auth/login – Login and receive \
\
•\tPUT /api/users/updateUser/{id} – Update user details\
\
•\tGET /api/users/getUser/{id} – Get user details by ID\
\
•\tGET /api/users/getId – Get currently logged-in user's ID\
\
# 📌 Holdings\
●\tGET /api/holdings/add – Add a new cryptocurrency holding\
\
●\tPOST /api/holdings/getHoldings – Get current user's holdings\
\
●\tPUT /api/holdings/getMyNetValue – Get total portfolio value for current user\
\
\
# 📌 Price Alerts\
●\tPOST /api/alert/create – Create a new price alert\
\
●\tGET /api/alert/user/{userid}– List all alerts\
\
●\tGET /api/alerts/ user/{userid}/triggered– List all triggered alerts\
●\tPUT /api/alert/{id} - Update a price alert\
●\tPUT /api/alert/{id}/disable - Disable a specific alert\
●\tDELETE /api/alert/{id} – Delete a price alert\
# 📌 Admin Controller\
•\tPOST /api/admin/login – Login as admin\
\
•\tGET /api/admin/users – View all users\
\
\
•\tGET /api/admin/user/{id} – Get user details by ID (admin access)\
# 📌 Crypto Controller\
•\tGET /api/crypto/getPrice/{symbol} – Get current price of a crypto by symbol\
\
•\tGET /api/crypto/getAllCryptos – Get list of all supported cryptocurrencies\
\
# Class Diagram\
This diagram represents a stock portfolio management system with layers: \
Controllers, Dto, Enum, Model, Repository, Services, and Entities. \
It includes user authentication, alert generation, portfolio creation, and gain/loss calculation. \
Entities like User, PriceAlert, Alert, Holding, and Log manage data and behavior. \
Services like PriceService, AlertService, AlertSchedulerService, AdminService, CryptoService, CustomUserDetailsService,EmailService,HoldingService, NotificationService, and UserService provide business logic.\
\
  \
\
# ER Diagram \
This ER diagram models Users, Admins, Holdings, and Logs with relationships among them.\
\
A User can have multiple Holdings and can generate multiple Logs when transactions occur.\
\
Holdings store data about the cryptocurrencies owned, such as coin name, quantity, buy price, and price alerts.\
\
Logs record detailed transaction history including buy/sell prices and dates.\
\
An Admin can review logs but does not directly manage holdings or users.\
 \
\
# Database Diagram:\
\
This database diagram models the core components of a Crypto Portfolio Tracker application, including users, holdings, transaction logs, and external coin data.\
It consists of four main tables: users, holdings, logs, and coin_API\
\
 \
________________________________________\
# 🧠 Key Concepts for Freshers\
Area\tWhat You’ll Learn\
Spring Boot APIs\
\
Security\
\tBuild and secure backend routes\
\
Basic authentication login using session_token and access control\
Scheduling\tAutomate background tasks like price fetcher\
API Integration\tCall real-world APIs using Java\
JSON Handling\tDeserialize API responses with Jackson\
Logic & Math\tCalculate gains, losses, and percentages\
Alerts\tApply business rules (if-else, comparisons)\
________________________________________\
# 🗂 Project Structure\
com.example.CryptoPortfolioTracker\
├── config\
│   └── SecurityConfig.java\
├── controller\
│   ├── AdminController.java\
│   ├── AlertController.java\
│   ├── CryptoController.java\
│   ├── CryptoControllerToGetPriceSymbol.java\
│   ├── HoldingController.java\
│   └── UserController.java\
├── dto\
│   ├── AddHoldingRequest.java\
│   ├── AlertRequestDTO.java\
│   ├── ClientResponse.java\
│   ├── CryptoData.java\
│   ├── LoginRequest.java\
│   ├── PriceResponse.java\
│   └── RegisterRequest.java\
├── entity\
│   ├── Alert.java\
│   ├── Holding.java\
│   ├── Log.java\
│   ├── PriceAlert.java\
│   └── User.java\
├── enums\
│   ├── AlertDirection.java\
│   ├── AlertStatus.java\
│   └── Role.java\
├── exception\
│   ├── GlobalExceptionHandler.java\
│   └── ResourceNotFoundException.java\
├── model\
│   └── ApiResponse.java\
├── repository\
│   ├── AlertRepository.java\
│   ├── HoldingRepository.java\
│   └── UserRepository.java\
├── scheduler\
│   └── AlertSchedulerService.java\
├── service\
│   ├── Implementation\
│   │   └── AdminServiceImpl.java\
│   ├── AdminService.java\
│   ├── AlertService.java\
│   ├── CryptoService.java\
│   ├── CustomUserDetailsService.java\
│   ├── EmailService.java\
│   ├── HoldingService.java\
│   ├── NotificationService.java\
│   ├── PriceService.java\
│   └── UserService.java\
├── CryptoPortfolioTrackerApplication.java\
└── README.md\
________________________________________\
# 🚀 How to Run\
# ✅ Prerequisites\
●\tJava 17+\
\
●\tMaven\
\
●\tMySQL\
\
●\tPostman (for testing APIs)\
\
# 🧪 Steps\
Clone the project:\
\
git clone https://github.com/your-username/CryptoPortfolioTracker.git\
cd CryptoPortfolioTracker\
1.\tCreate a database:\
\
 CREATE DATABASE CryptoPortfolioTracker;\
2.\tConfigure application.properties:\
\
 spring.application.name=CryptoPortfolioTracker\
\
spring.datasource.url=jdbc:mysql://10.9.124.199:3306/CryptoPortfolioTracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true\
spring.datasource.username=root\
spring.datasource.password=password \
\
spring.jpa.hibernate.ddl-auto=update\
spring.jpa.show-sql=true\
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect\
\
spring.mail.host=smtp-relay.brevo.com\
spring.mail.port=587\
spring.mail.username=8e3121001@smtp-brevo.com\
spring.mail.password=h1nr2WQwZysT5zVg\
spring.mail.properties.mail.smtp.auth=true\
spring.mail.properties.mail.smtp.starttls.enable=true\
3.\tBuild and Run:\
\
 ./mvnw clean install\
./mvnw spring-boot:run\
\
4.\tTest using Swagger or Postman\
\
________________________________________\
# 📦 Optional Enhancements\
Feature\tBenefit\
Email alerts\tBetter user notification\
Graphical reports (charts)\tEasier to visualize performance\
Export portfolio to PDF/Excel\tFor offline record-keeping\
Mobile app integration\tFuture frontend possibilities\
WebSocket price updates\tLive price tracking\
________________________________________\
# 👩‍💻 Contributors\
# Name\tRole\
Praveen\tProject Lead, User Module,GlobalExceptionHandling Module, Basic authentication using session_token  & Scheduler Logic\
Aayushi\tAlert module\
Kaushik, Harshnie\
\
Navya\
\
Lakshmi Bhanu\tPrice Fetcher, Gain Calculator\
\
\
Fetch API Module\
\
Admin Module and Junit testing
