# 🧪 COMPLETE POSTMAN TESTING GUIDE - Step by Step

## Before You Start

### 1. Set Environment Variables
Your backend uses environment variables for secrets. Set these **before** starting:

**Windows (Command Prompt):**
```cmd
set DB_PASSWORD=your_mysql_password
set JWT_SECRET=YourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm2026
set MAIL_USERNAME=your_email@gmail.com
set MAIL_PASSWORD=your_app_password
```

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="YourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm2026"
$env:MAIL_USERNAME="your_email@gmail.com"
$env:MAIL_PASSWORD="your_app_password"
```

> **Note:** For `MAIL_PASSWORD`, use a Gmail App Password (not your real password). 
> To get one: Google Account → Security → 2-Step Verification → App passwords.
> If you don't need emails for testing, set any dummy values — the app will still work, emails just won't send.

### 2. Make Sure MySQL is Running
```cmd
mysql -u root -p
```
If it connects, you're good. Type `exit` to close.

### 3. Build & Start the Backend
Open terminal in `c:\TravelMateAI\TMA\Backend` and run:
```cmd
set DB_PASSWORD=your_mysql_password
set JWT_SECRET=YourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm2026
set MAIL_USERNAME=test@gmail.com
set MAIL_PASSWORD=testpassword
mvn clean install -DskipTests
mvn spring-boot:run
```

You should see:
```
TravelMate AI Backend Started!
Base URL: http://localhost:8080
API Base: http://localhost:8080/api
```

**Keep this terminal open.** Open a new terminal for further commands.

---

## Download Postman
https://www.postman.com/downloads/

---

## Setting Up Postman

### Create a Postman Environment
1. Click the **gear icon** (top-right) → **Add Environment**
2. Name it: `TravelMateAI Local`
3. Add these variables:

| Variable | Initial Value | Current Value |
|----------|--------------|---------------|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `token` | (leave empty) | (leave empty) |

4. Click **Save**
5. Select `TravelMateAI Local` from the environment dropdown (top-right)

---

## TEST 1: Register a User

**Create a new request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/api/users/register`
- **Headers:** `Content-Type: application/json`
- **Body → raw → JSON:**

```json
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "TestPassword123!",
  "phone": "9876543210"
}
```

**Click Send**

**Expected Response (201 Created):**
```json
{
  "status": "SUCCESS",
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "Test User",
    "email": "test@example.com",
    "phone": "9876543210",
    "role": "USER"
  }
}
```

---

## TEST 2: Login & Get Token

- **Method:** `POST`
- **URL:** `{{base_url}}/api/users/login`
- **Body → raw → JSON:**

```json
{
  "email": "test@example.com",
  "password": "TestPassword123!"
}
```

**Click Send**

**Expected Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "name": "Test User",
    "email": "test@example.com",
    "role": "USER"
  }
}
```

### Save the Token Automatically
In Postman, go to the **Tests** tab of this request and paste:
```javascript
var jsonData = pm.response.json();
if (jsonData.data && jsonData.data.token) {
    pm.environment.set("token", jsonData.data.token);
}
```
Now click **Send** again. The token is auto-saved to your environment!

---

## TEST 3: Test Auth Works (Get Profile)

- **Method:** `GET`
- **URL:** `{{base_url}}/api/users/1`
- **Headers:**
  - `Authorization: Bearer {{token}}`

**Click Send**

**Expected (200 OK):** Your user profile data.

---

## TEST 4: Test Without Token (Should Fail)

- **Method:** `GET`
- **URL:** `{{base_url}}/api/users/1`
- **No Authorization header**

**Expected (401 Unauthorized):**
```json
{
  "status": "ERROR",
  "message": "Unauthorized"
}
```

✅ Security is working!

---

## TEST 5: Register Admin User (For Trip Creation)

You need to manually set role in the database:
```sql
-- In MySQL terminal:
USE travelmateai_db;
INSERT INTO users (name, email, password, phone, role, created_at, updated_at) 
VALUES ('Admin User', 'admin@example.com', '$2a$10$placeholder', '1234567890', 'ADMIN', NOW(), NOW());
```

**Or register normally and update role:**
1. Register with `admin@example.com`
2. Then in MySQL:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```
3. Login with admin@example.com to get admin token

---

## TEST 6: Create a Trip (Admin Only)

- **Method:** `POST`
- **URL:** `{{base_url}}/api/trips`
- **Headers:**
  - `Authorization: Bearer {{token}}` (use admin token)
  - `Content-Type: application/json`
- **Body → raw → JSON:**

```json
{
  "title": "Paris City Tour",
  "destination": "Paris",
  "description": "Beautiful 5-day guided tour of Paris including Eiffel Tower, Louvre, and more",
  "price": 1500.00,
  "duration": 5,
  "totalSeats": 20,
  "imageUrl": "https://example.com/paris.jpg"
}
```

**Expected (201 Created):** Trip details with `id`, `availableSeats: 20`

> Note: Save the `id` value (probably 1) — you need it for booking/locking tests.

---

## TEST 7: Get All Trips (Public - No Token Needed)

- **Method:** `GET`
- **URL:** `{{base_url}}/api/trips`

**Expected (200 OK):** List of trips with pagination info.

---

## TEST 8: Get Trip by ID (Public)

- **Method:** `GET`
- **URL:** `{{base_url}}/api/trips/1`

**Expected (200 OK):** Paris City Tour with `availableSeats: 20`

---

## TEST 9: Search Trips (Public)

- **Method:** `GET`
- **URL:** `{{base_url}}/api/trips/search?destination=Paris`

**Expected (200 OK):** Array with the Paris trip.

---

## 🔒 TEST 10: Lock Seats (Seat Locking System)

**Login as the regular user first** (test@example.com) and save token.

- **Method:** `POST`
- **URL:** `{{base_url}}/api/seat-locks`
- **Headers:**
  - `Authorization: Bearer {{token}}`
  - `Content-Type: application/json`
- **Body → raw → JSON:**

```json
{
  "tripId": 1,
  "numberOfSeats": 3
}
```

**Expected (201 Created):**
```json
{
  "status": "SUCCESS",
  "message": "Seats locked successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "userEmail": "test@example.com",
    "tripId": 1,
    "tripTitle": "Paris City Tour",
    "destination": "Paris",
    "numberOfSeats": 3,
    "lockTime": "2026-03-01T11:00:00",
    "expiryTime": "2026-03-01T11:05:00",
    "status": "ACTIVE"
  }
}
```

**Notice:** `expiryTime` is 5 minutes after `lockTime`. Save the lock `id` for next tests.

---

## TEST 11: Get My Active Locks

- **Method:** `GET`
- **URL:** `{{base_url}}/api/seat-locks/my-active-locks`
- **Headers:** `Authorization: Bearer {{token}}`

**Expected:** Array with your active lock.

---

## TEST 12: Get Lock Details

- **Method:** `GET`
- **URL:** `{{base_url}}/api/seat-locks/1`
- **Headers:** `Authorization: Bearer {{token}}`

**Expected:** Lock details with `status: "ACTIVE"`

---

## TEST 13: Try Duplicate Lock (Should Fail)

- **Method:** `POST`
- **URL:** `{{base_url}}/api/seat-locks`
- **Headers:** `Authorization: Bearer {{token}}`, `Content-Type: application/json`
- **Body:**
```json
{
  "tripId": 1,
  "numberOfSeats": 2
}
```

**Expected (400 Bad Request):**
```json
{
  "status": "ERROR",
  "message": "You already have an active seat lock for this trip. Please confirm the booking or wait for the lock to expire."
}
```

---

## TEST 14: Try Lock Too Many Seats (Should Fail)

Wait for the previous lock to expire (5 min), then:

- **Method:** `POST`
- **URL:** `{{base_url}}/api/seat-locks`
- **Body:**
```json
{
  "tripId": 1,
  "numberOfSeats": 100
}
```

**Expected (400):** Error about insufficient seats.

---

## TEST 15: Try Lock > 10 Seats (Validation)

```json
{
  "tripId": 1,
  "numberOfSeats": 11
}
```

**Expected (400):** `Maximum 10 seats can be locked at once`

---

## 📅 TEST 16: Create Booking (With Active Lock)

**First, lock seats again** (POST /api/seat-locks with 3 seats).
Then immediately book:

- **Method:** `POST`
- **URL:** `{{base_url}}/api/bookings`
- **Headers:** `Authorization: Bearer {{token}}`, `Content-Type: application/json`
- **Body:**
```json
{
  "tripId": 1,
  "numberOfSeats": 2
}
```

**Expected (201 Created):**
```json
{
  "status": "SUCCESS",
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "tripId": 1,
    "numberOfSeats": 2,
    "totalPrice": 3000.00,
    "status": "CONFIRMED"
  }
}
```

**What happened behind the scenes:**
- Found your active lock on trip 1
- Used it for booking (2 of your 3 locked seats)
- Lock status changed to `CONFIRMED`
- Trip `availableSeats` reduced by 2

---

## TEST 17: Verify Seat Reduction

- **Method:** `GET`
- **URL:** `{{base_url}}/api/trips/1`

**Check:** `availableSeats` should now be **18** (was 20, booked 2).

---

## TEST 18: Get My Bookings

- **Method:** `GET`
- **URL:** `{{base_url}}/api/bookings/my-bookings`
- **Headers:** `Authorization: Bearer {{token}}`

**Expected:** Array with your confirmed booking.

---

## TEST 19: Cancel Booking

- **Method:** `DELETE`
- **URL:** `{{base_url}}/api/bookings/1`
- **Headers:** `Authorization: Bearer {{token}}`

**Expected (200 OK):** Booking with `status: "CANCELLED"`.

**Verify:** GET `/api/trips/1` should show `availableSeats: 20` again.

---

## ⏰ TEST 20: Lock Expiration (Wait 5 Minutes)

1. **Lock seats:** POST `/api/seat-locks` with `{ "tripId": 1, "numberOfSeats": 2 }`
2. **Note the lock ID and current time**
3. **Wait 5+ minutes** (set a timer)
4. **Check lock status:** GET `/api/seat-locks/{lockId}`

**Expected:** `status` should change from `"ACTIVE"` to `"EXPIRED"`

This confirms the scheduled job is running every 60 seconds and cleaning up expired locks!

---

## TEST 21: Direct Booking (No Lock)

Make sure you have **no active locks** (wait for expiry or use a fresh trip).

- **Method:** `POST`
- **URL:** `{{base_url}}/api/bookings`
- **Body:**
```json
{
  "tripId": 1,
  "numberOfSeats": 1
}
```

**Expected (201 Created):** Booking created directly (backward compatible path).

---

## 📊 TEST 22: Admin Reports (Admin Token Required)

Login as admin and use admin token.

### Total Bookings
- **GET** `{{base_url}}/api/reports/total-bookings`
- **Headers:** `Authorization: Bearer {{admin_token}}`

### Popular Destination
- **GET** `{{base_url}}/api/reports/popular-destination`

### Revenue
- **GET** `{{base_url}}/api/reports/revenue`

### Dashboard
- **GET** `{{base_url}}/api/reports/dashboard`

**Expected:** Each returns analytics data. If accessed with USER token, should get **403 Forbidden**.

---

## TEST 23: Update User Profile

- **Method:** `PUT`
- **URL:** `{{base_url}}/api/users/1`
- **Headers:** `Authorization: Bearer {{token}}`, `Content-Type: application/json`
- **Body:**
```json
{
  "name": "Updated Name",
  "phone": "9999999999"
}
```

**Expected (200 OK):** Updated user data.

---

## TEST 24: Delete User

- **Method:** `DELETE`
- **URL:** `{{base_url}}/api/users/1`
- **Headers:** `Authorization: Bearer {{token}}`

**Expected (200 OK):** User deleted successfully.

---

## ❌ Error Scenario Tests

### Bad JSON (missing required field)
- POST `/api/seat-locks` with `{ "tripId": 1 }` (missing numberOfSeats)
- **Expected:** 400 with validation error

### Invalid Trip ID
- POST `/api/seat-locks` with `{ "tripId": 999, "numberOfSeats": 1 }`
- **Expected:** 404 Not Found

### Expired Token
- Wait 2+ hours, then use old token
- **Expected:** 401 Unauthorized

### Regular user accessing admin endpoint
- GET `/api/reports/dashboard` with USER token
- **Expected:** 403 Forbidden

---

## ✅ Complete Test Checklist

### Authentication
- [ ] Register user → 201
- [ ] Register duplicate email → Error
- [ ] Login → 200 with token
- [ ] Login wrong password → Error
- [ ] Access protected route with token → 200
- [ ] Access protected route without token → 401

### Trips (Public)
- [ ] Get all trips → 200
- [ ] Get trip by ID → 200
- [ ] Search by destination → 200
- [ ] Get non-existent trip → 404

### Trips (Admin)
- [ ] Create trip (admin) → 201
- [ ] Create trip (user) → 403
- [ ] Update trip (admin) → 200
- [ ] Delete trip (admin) → 200

### Seat Locking
- [ ] Lock seats → 201 with 5-min expiry
- [ ] Duplicate lock same trip → 400
- [ ] Lock > 10 seats → 400 validation
- [ ] Lock more than available → 400
- [ ] Get my active locks → 200
- [ ] Get lock by ID → 200
- [ ] Wait 5 min → lock status = EXPIRED

### Bookings
- [ ] Book with active lock → 201
- [ ] Book without lock (direct) → 201
- [ ] Get my bookings → 200
- [ ] Cancel booking → 200, seats restored
- [ ] Duplicate booking same trip → 400

### Reports (Admin)
- [ ] Total bookings → 200
- [ ] Popular destination → 200
- [ ] Revenue → 200
- [ ] Dashboard → 200
- [ ] User accessing reports → 403

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| `Connection refused` | MySQL not running. Start it: `net start MySQL80` |
| `Port 8080 already in use` | Kill: `netstat -ano \| findstr :8080` then `taskkill /PID <pid> /F` |
| `Invalid JWT` | Token expired (2hr limit). Login again. |
| `Access Denied` | Check role. Only ADMIN can create trips / view reports. |
| `DB_PASSWORD not set` | Set env var before running: `set DB_PASSWORD=yourpass` |
| `BUILD FAILURE` | Run `mvn clean install -DskipTests` |
| Lock still ACTIVE after 5 min | Wait up to 1 more minute (scheduler runs every 60s) |
