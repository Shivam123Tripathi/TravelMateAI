# TravelMate AI Backend

A production-ready REST API backend for **TravelMate AI** — a smart travel assistant platform built as a Final Year B.Tech CSE project.

> ⚠️ **Security Notice**: This project uses environment variables for all sensitive credentials. No passwords, API keys, or secrets are hardcoded.

## 🚀 Features

### Authentication & Authorization
- ✅ User registration with encrypted passwords (BCrypt)
- ✅ JWT-based stateless authentication
- ✅ Role-based access control (USER / ADMIN)
- ✅ Secure endpoint protection

### User Management
- ✅ Registration, login, profile update, account deletion

### Trip Management
- ✅ CRUD operations (Admin-only for create/update/delete)
- ✅ Search by destination with pagination
- ✅ Safe deletion with booking existence checks

### Booking System
- ✅ Book trips with seat availability validation
- ✅ Seat locking system (temporary seat reservation before payment)
- ✅ Automatic seat lock expiration via scheduled jobs
- ✅ Booking cancellation with seat restoration
- ✅ Email notifications (confirmation & cancellation)

### Admin Analytics
- ✅ Total bookings, popular destinations, revenue reports
- ✅ Dashboard API with typed DTOs

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming Language |
| Spring Boot 3.2.3 | Application Framework |
| Spring Security + JWT | Authentication & Authorization |
| Spring Data JPA | Database ORM |
| MySQL 8.0 | Relational Database |
| Lombok | Boilerplate Reduction |
| SpringDoc OpenAPI | API Documentation (Swagger UI) |
| Docker | Containerization |
| JUnit 5 + Mockito | Unit Testing |
| Maven | Build Tool |

## 📁 Project Structure

```
src/main/java/com/travelmateai/backend/
├── TravelMateAiApplication.java       # Main entry point
├── config/                            # Configuration
│   ├── SecurityConfig.java            # Spring Security + JWT filter chain
│   ├── SwaggerConfig.java             # OpenAPI documentation setup
│   ├── AsyncConfig.java               # Thread pool for async email
│   └── CorsConfig.java                # CORS for frontend integration
├── controller/                        # REST Controllers
│   ├── UserController.java
│   ├── TripController.java
│   ├── BookingController.java
│   ├── SeatLockController.java
│   └── ReportController.java
├── service/                           # Business Logic
│   ├── UserService.java
│   ├── TripService.java
│   ├── BookingService.java
│   ├── SeatLockService.java
│   ├── SeatLockScheduler.java
│   ├── ReportService.java
│   ├── EmailService.java
│   └── EmailAsyncHelper.java
├── repository/                        # Data Access (JPA)
├── entity/                            # JPA Entities
├── dto/                               # Request/Response DTOs
├── security/                          # JWT utilities & filters
└── exception/                         # Global exception handling
```

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- MySQL 8.0+

### Option 1: Run Locally

```bash
# 1. Create MySQL database
mysql -u root -p -e "CREATE DATABASE travelmateai_db;"

# 2. Set environment variables (PowerShell)
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="YourBase64Encoded256BitSecretKeyHere"
$env:MAIL_USERNAME="your_email@gmail.com"
$env:MAIL_PASSWORD="your_gmail_app_password"

# 3. Run the application
mvn spring-boot:run
```

### Option 2: Run with Docker

```bash
# Set required env vars
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="YourBase64SecretKey"

# Start all services
docker-compose up --build
```

### Access Points
| Service | URL |
|---|---|
| API Base | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs (JSON) | http://localhost:8080/v3/api-docs |

## 🔌 API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login → get JWT token |

### Users (Authenticated)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/{id}` | Get user profile |
| PUT | `/api/users/{id}` | Update profile |
| DELETE | `/api/users/{id}` | Delete account |

### Trips (Public GET / Admin CUD)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/trips` | Get all trips (paginated) |
| GET | `/api/trips/{id}` | Get trip by ID |
| GET | `/api/trips/search?destination=X` | Search trips |
| POST | `/api/trips` | Create trip (Admin) |
| PUT | `/api/trips/{id}` | Update trip (Admin) |
| DELETE | `/api/trips/{id}` | Delete trip (Admin) |

### Bookings (Authenticated)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/bookings` | Create booking |
| GET | `/api/bookings/{id}` | Get booking |
| GET | `/api/bookings/my-bookings` | My bookings |
| DELETE | `/api/bookings/{id}` | Cancel booking |

### Seat Locks (Authenticated)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/seat-locks` | Lock seats |
| POST | `/api/seat-locks/{id}/confirm` | Confirm lock |
| GET | `/api/seat-locks/my-locks` | My locks |
| GET | `/api/seat-locks/my-active-locks` | My active locks |

### Reports (Admin Only)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/total-bookings` | Booking counts |
| GET | `/api/reports/popular-destination` | Top destinations |
| GET | `/api/reports/revenue` | Revenue summary |
| GET | `/api/reports/dashboard` | Full dashboard |

## ⚙️ Environment Variables

| Variable | Description | Required |
|---|---|---|
| `DB_PASSWORD` | MySQL password | ✅ |
| `DB_USERNAME` | MySQL username (default: `root`) | ❌ |
| `DB_URL` | JDBC URL (default: `localhost:3306`) | ❌ |
| `JWT_SECRET` | Base64 256-bit secret for JWT | ✅ |
| `MAIL_USERNAME` | Gmail address for notifications | ✅ |
| `MAIL_PASSWORD` | Gmail App Password | ✅ |
| `CORS_ORIGINS` | Allowed CORS origins (comma-separated) | ❌ |

## 🔐 Creating Admin User

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

## 📝 License

MIT License — © 2026 TravelMate AI Team (Final Year B.Tech CSE Project)
