# TravelMateAI — Frontend Integration Guide

> **Base URL:** `http://localhost:8080/api`
> **Auth:** JWT Bearer token in `Authorization` header
> **Content-Type:** `application/json`

---

## Quick Setup (Backend)

```bash
# Prerequisites: Java 21, Maven, MySQL running
# Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS travelmateai;"

# Set environment variables
set DB_PASSWORD=your_mysql_password
set JWT_SECRET=your_256_bit_secret_key_min_32_chars_long
set MAIL_USERNAME=your_email@gmail.com
set MAIL_PASSWORD=your_gmail_app_password

# Run
cd Backend
mvn clean install -DskipTests
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`

---

## CORS (Already Configured)

| Setting | Value |
|---------|-------|
| Allowed Origins | `http://localhost:3000`, `http://localhost:5173`, `http://localhost:4200` |
| Allowed Methods | GET, POST, PUT, DELETE, PATCH, OPTIONS |
| Credentials | Allowed |

---

## Auth Flow (JWT)

1. Register → `POST /api/users/register`
2. Login → `POST /api/users/login` → returns `token`
3. Store token (localStorage/sessionStorage)
4. All subsequent requests: `Authorization: Bearer <token>`
5. Token expires in **2 hours** — re-login when you get 401

```js
// React — Axios interceptor
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

---

## Every Response Follows This Shape

```json
{
  "status": "success" | "error",
  "message": "string",
  "data": { ... } | null,
  "timestamp": "2026-03-01T10:00:00"
}
```

Null fields are omitted from the response.

---

## Roles

| Role | Can Do |
|------|--------|
| `USER` | Browse trips, lock seats, book, view own bookings/profile |
| `ADMIN` | Everything above + create/edit/delete trips + view reports |

---

## All 25 Endpoints

### 1. AUTH (Public — no token needed)

#### Register
```
POST /api/users/register
```
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "pass123",
  "phone": "1234567890"
}
```
| Field | Rules |
|-------|-------|
| name | Required, 2-100 chars |
| email | Required, valid email, must be unique |
| password | Required, 6-40 chars |
| phone | Optional, exactly 10 digits |

**Response** → `UserResponse` in `data`:
```json
{
  "id": 1, "name": "John Doe", "email": "john@example.com",
  "phone": "1234567890", "role": "USER",
  "createdAt": "...", "updatedAt": "..."
}
```

#### Login
```
POST /api/users/login
```
```json
{
  "email": "john@example.com",
  "password": "pass123"
}
```
**Response** → `AuthResponse` in `data`:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```
**Save `data.token` to localStorage. Use `data.role` for UI routing.**

---

### 2. USER PROFILE (Token required)

#### Get Profile
```
GET /api/users/{id}
```
Response → `UserResponse`

#### Update Profile
```
PUT /api/users/{id}
```
```json
{
  "name": "New Name",
  "phone": "9876543210",
  "password": "newpass123"
}
```
All fields optional. Only send what changed.

#### Delete Account
```
DELETE /api/users/{id}
```

---

### 3. TRIPS

#### List All Trips (Public)
```
GET /api/trips?page=0&size=10&sort=createdAt,desc
```
| Param | Default | Description |
|-------|---------|-------------|
| page | 0 | Zero-based page number |
| size | 10 | Items per page |
| sort | createdAt | Field + direction (e.g. `price,asc`) |

Response → Paginated `TripResponse[]`:
```json
{
  "data": {
    "content": [
      {
        "id": 1, "title": "Beach Paradise", "destination": "Maldives",
        "description": "A relaxing trip", "price": 299.99, "duration": 5,
        "availableSeats": 48, "totalSeats": 50,
        "imageUrl": "https://...", "createdAt": "...", "updatedAt": "..."
      }
    ],
    "totalPages": 5,
    "totalElements": 42,
    "number": 0,
    "size": 10,
    "first": true,
    "last": false
  }
}
```

#### Get Single Trip (Public)
```
GET /api/trips/{id}
```

#### Search by Destination (Public)
```
GET /api/trips/search?destination=Maldives
```
Response → `TripResponse[]` (list, not paginated)

#### Create Trip (ADMIN only)
```
POST /api/trips
```
```json
{
  "title": "Beach Paradise",
  "destination": "Maldives",
  "description": "A relaxing beach trip",
  "price": 299.99,
  "duration": 5,
  "totalSeats": 50,
  "imageUrl": "https://example.com/image.jpg"
}
```
| Field | Rules |
|-------|-------|
| title | Required, max 200 |
| destination | Required, max 100 |
| description | Optional, max 2000 |
| price | Required, min 0.01 |
| duration | Required, min 1 (days) |
| totalSeats | Required, min 1 |
| imageUrl | Optional, max 500 |

#### Update Trip (ADMIN only)
```
PUT /api/trips/{id}
```
Same body as Create.

#### Delete Trip (ADMIN only)
```
DELETE /api/trips/{id}
```

---

### 4. SEAT LOCKING (Token required)

> **How it works:** User locks seats → has 5 min to confirm → lock auto-expires if not confirmed. This prevents overbooking.

#### Lock Seats
```
POST /api/seat-locks
```
```json
{
  "tripId": 1,
  "numberOfSeats": 2
}
```
| Field | Rules |
|-------|-------|
| tripId | Required |
| numberOfSeats | Required, 1-10 |

Response → `SeatLockResponse`:
```json
{
  "id": 1, "userId": 1, "userEmail": "john@example.com",
  "tripId": 1, "tripTitle": "Beach Paradise", "destination": "Maldives",
  "numberOfSeats": 2,
  "lockTime": "2026-03-01T10:00:00",
  "expiryTime": "2026-03-01T10:05:00",
  "status": "ACTIVE",
  "createdAt": "...", "updatedAt": "..."
}
```
**Use `expiryTime` to show a countdown timer in the UI.**

#### Confirm Lock → Creates Booking
```
POST /api/seat-locks/{lockId}/confirm
```
No body needed. Converts the lock into a permanent booking.

#### Get My Locks
```
GET /api/seat-locks/my-locks
```

#### Get My Active Locks
```
GET /api/seat-locks/my-active-locks
```

#### Get Lock by ID
```
GET /api/seat-locks/{lockId}
```

**Lock statuses:** `ACTIVE` → `CONFIRMED` (if user confirms) or `EXPIRED` (auto after 5 min)

---

### 5. BOOKINGS (Token required)

#### Create Booking (Direct — without seat lock)
```
POST /api/bookings
```
```json
{
  "tripId": 1,
  "numberOfSeats": 2
}
```
Response → `BookingResponse`:
```json
{
  "id": 1, "userId": 1, "userName": "John Doe", "userEmail": "john@example.com",
  "tripId": 1, "tripTitle": "Beach Paradise", "destination": "Maldives",
  "numberOfSeats": 2, "totalPrice": 599.98,
  "status": "CONFIRMED",
  "bookingDate": "...", "updatedAt": "..."
}
```

#### Get Booking
```
GET /api/bookings/{id}
```

#### Get My Bookings
```
GET /api/bookings/my-bookings
```

#### Get User's Bookings (by userId)
```
GET /api/bookings/user/{userId}
```

#### Cancel Booking
```
DELETE /api/bookings/{id}
```
Returns cancelled booking with `status: "CANCELLED"`.

**Booking statuses:** `CONFIRMED`, `CANCELLED`, `PENDING`

---

### 6. ADMIN REPORTS (ADMIN only)

```
GET /api/reports/total-bookings
GET /api/reports/popular-destination
GET /api/reports/revenue
GET /api/reports/dashboard
```
All return dynamic key-value data in `data` field.

---

## Error Handling

| HTTP Code | Meaning | When |
|-----------|---------|------|
| 400 | Bad Request | Validation fails, bad input |
| 401 | Unauthorized | No/expired token, wrong credentials |
| 403 | Forbidden | Not enough permissions (e.g. USER hitting ADMIN route) |
| 404 | Not Found | Trip/User/Booking doesn't exist |
| 409 | Conflict | Duplicate email on register |
| 500 | Server Error | Unexpected backend error |

**Validation error response:**
```json
{
  "status": "error",
  "message": "Validation failed",
  "data": {
    "email": "Please provide a valid email address",
    "name": "Name is required"
  }
}
```

**In React:** check `response.data.status === "error"` and display `response.data.message` or iterate `response.data.data` for field-level errors.

---

## Recommended Booking Flow (Frontend)

```
Browse trips (GET /api/trips)
  → Select trip (GET /api/trips/{id})
  → Lock seats (POST /api/seat-locks)
  → Show 5-min countdown using expiryTime
  → Confirm (POST /api/seat-locks/{lockId}/confirm)
  → Show booking confirmation
  → If timer expires → show "Lock expired, try again"
```

---

## React Tips

- **Auth header:** Use axios interceptor (shown above)
- **401 handling:** Global interceptor → redirect to login, clear token
- **Role routing:** Store `role` from login → conditionally show admin pages
- **Protected routes:** Use `react-router` with auth guards
- **Pagination:** Use `data.content`, `data.totalPages`, `data.number`
- **Countdown timer:** `Math.max(0, new Date(expiryTime) - Date.now())` → update every second
- **Loading states:** Show spinners during API calls
- **TypeScript interfaces:** Define types matching the response DTOs above

---

## Quick Reference

| Config | Value |
|--------|-------|
| Backend Port | 8080 |
| Seat Lock Duration | 5 minutes |
| JWT Expiry | 2 hours |
| Max Seats per Lock/Booking | 10 |
| Default Page Size | 10 |
| Database | MySQL `travelmateai` |

---

*Last Updated: March 2026*
