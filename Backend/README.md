# TravelMate AI Backend

A production-ready REST API backend for **TravelMate AI** - A smart travel assistant platform built as a Final Year B.Tech CSE project.

> ⚠️ **Security Notice**: This project uses environment variables for all sensitive credentials. No passwords, API keys, or secrets are hardcoded. See [Getting Started](#-getting-started) for configuration.

## 🚀 Features

### Authentication & Authorization
- ✅ User registration with encrypted passwords (BCrypt)
- ✅ JWT-based stateless authentication
- ✅ Role-based access control (USER / ADMIN)
- ✅ Secure endpoint protection

### User Management
- ✅ User registration and login
- ✅ Profile viewing and updating
- ✅ Account deletion

### Trip Management
- ✅ Create, read, update, delete trips (Admin only for CUD)
- ✅ Search trips by destination
- ✅ Pagination and sorting support
- ✅ Seat availability tracking

### Booking System
- ✅ Book trips with seat validation
- ✅ View booking history
- ✅ Cancel bookings with automatic seat restoration
- ✅ Email notifications for booking confirmations and cancellations

### Admin Reports & Analytics
- ✅ Total bookings count
- ✅ Popular destination analytics
- ✅ Revenue reports
- ✅ Dashboard overview

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Programming Language |
| Spring Boot 3.2.3 | Application Framework |
| Spring Security | Authentication & Authorization |
| Spring Data JPA | Database ORM |
| MySQL | Relational Database |
| JWT (jjwt) | Stateless Authentication Tokens |
| Lombok | Boilerplate Reduction |
| Maven | Build Tool |
| Swagger/OpenAPI | API Documentation |

## 📁 Project Structure

```
src/main/java/com/travelmateai/backend/
├── TravelMateAiApplication.java    # Main entry point
├── config/                         # Configuration classes
│   ├── SecurityConfig.java         # Spring Security configuration
│   ├── SwaggerConfig.java          # OpenAPI/Swagger configuration
│   ├── AsyncConfig.java            # Async execution configuration
│   └── CorsConfig.java             # CORS configuration
├── controller/                     # REST Controllers
│   ├── UserController.java         # User endpoints
│   ├── TripController.java         # Trip endpoints
│   ├── BookingController.java      # Booking endpoints
│   └── ReportController.java       # Report endpoints
├── service/                        # Business Logic Layer
│   ├── UserService.java
│   ├── TripService.java
│   ├── BookingService.java
│   ├── ReportService.java
│   └── EmailService.java
├── repository/                     # Data Access Layer
│   ├── UserRepository.java
│   ├── TripRepository.java
│   └── BookingRepository.java
├── entity/                         # JPA Entities
│   ├── User.java
│   ├── Trip.java
│   ├── Booking.java
│   ├── Role.java
│   └── BookingStatus.java
├── dto/                            # Data Transfer Objects
│   ├── request/                    # Request DTOs
│   └── response/                   # Response DTOs
├── security/                       # Security Components
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
└── exception/                      # Exception Handling
    ├── GlobalExceptionHandler.java
    └── [Custom Exceptions]
```

## 🔌 API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login and get JWT token |

### User Management (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | Get user profile |
| PUT | `/api/users/{id}` | Update user profile |
| DELETE | `/api/users/{id}` | Delete user account |

### Trips (Public GET, Admin CUD)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/trips` | Get all trips (paginated) |
| GET | `/api/trips/{id}` | Get trip by ID |
| GET | `/api/trips/search?destination=X` | Search trips |
| POST | `/api/trips` | Create trip (Admin) |
| PUT | `/api/trips/{id}` | Update trip (Admin) |
| DELETE | `/api/trips/{id}` | Delete trip (Admin) |

### Bookings (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings` | Create booking |
| GET | `/api/bookings/{id}` | Get booking by ID |
| GET | `/api/bookings/my-bookings` | Get current user's bookings |
| GET | `/api/bookings/user/{userId}` | Get user's bookings |
| DELETE | `/api/bookings/{id}` | Cancel booking |

### Reports (Admin Only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/total-bookings` | Total bookings count |
| GET | `/api/reports/popular-destination` | Most popular destination |
| GET | `/api/reports/revenue` | Total revenue |
| GET | `/api/reports/dashboard` | Comprehensive dashboard |

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- MySQL 8.0+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/travelmateai-backend.git
   cd travelmateai-backend
   ```

2. **Create MySQL Database**
   ```sql
   CREATE DATABASE travelmateai_db;
   ```

3. **Set Environment Variables**
   
   The application uses environment variables for all sensitive credentials (no hardcoding):
   
   **Windows PowerShell:**
   ```powershell
   $env:DB_PASSWORD="your_mysql_password"
   $env:JWT_SECRET="YourBase64Encoded256BitSecretKeyHere"
   $env:MAIL_USERNAME="your_email@gmail.com"
   $env:MAIL_PASSWORD="your_gmail_app_password"
   ```
   
   **Linux/macOS:**
   ```bash
   export DB_PASSWORD="your_mysql_password"
   export JWT_SECRET="YourBase64Encoded256BitSecretKeyHere"
   export MAIL_USERNAME="your_email@gmail.com"
   export MAIL_PASSWORD="your_gmail_app_password"
   ```
   
   | Variable | Description |
   |----------|-------------|
   | `DB_PASSWORD` | MySQL database password |
   | `JWT_SECRET` | 256-bit Base64 encoded secret for JWT signing |
   | `MAIL_USERNAME` | Gmail address for sending emails |
   | `MAIL_PASSWORD` | Gmail App Password (NOT your Gmail password) |

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs

## 📧 Email Configuration

Email notifications use Gmail SMTP with environment variables (no hardcoded credentials).

**Setup Gmail App Password:**
1. Enable 2-Factor Authentication in your Google Account
2. Go to: Google Account → Security → App Passwords
3. Generate a new App Password for "Mail"
4. Set the environment variable: `MAIL_PASSWORD="your_16_char_app_password"`

**Note:** Never use your actual Gmail password. Always use the generated App Password.

## 🔐 Creating Admin User

By default, all registered users have `USER` role. To create an admin:

**Option 1: Direct SQL**
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

**Option 2: Create a data initialization service** (for production)

## 📊 Sample API Requests

### Register User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "phone": "9876543210"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Create Booking (with JWT)
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "tripId": 1,
    "numberOfSeats": 2
  }'
```

## 🔮 Future AI Integration

The architecture is designed for future AI integration:

```
GET /api/recommendations/{userId}
```

The modular service layer allows easy integration with:
- External AI microservices
- Machine learning recommendation engines
- Natural language processing for travel queries

To add AI recommendations:
1. Create `RecommendationService.java`
2. Call external AI API or microservice
3. Create `RecommendationController.java`
4. Return personalized travel suggestions

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**TravelMate AI **
- Final Year B.Tech CSE Project
- 2026

---

⭐ If you found this project helpful, please give it a star!
