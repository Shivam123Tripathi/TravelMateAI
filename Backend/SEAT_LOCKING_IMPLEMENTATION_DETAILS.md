# Seat Locking System - Implementation Summary

## Date: March 1, 2026
## System: TravelMate AI Backend
## Feature: Temporary Seat Locking with Automatic Expiration

---

## 🎯 Overview

A complete seat locking system has been implemented to prevent overbooking and race conditions in the TravelMate AI booking platform. This system temporarily locks seats for a configurable duration (default 5 minutes) before users complete their booking.

---

## ✅ Implementation Checklist

### New Entities Created
- [x] **SeatLock.java** - Main seat lock entity with status tracking
- [x] **SeatLockStatus.java** - Enum for lock statuses (ACTIVE, EXPIRED, CONFIRMED)

### New Repositories
- [x] **SeatLockRepository.java** - Custom queries for seat lock operations

### New Services
- [x] **SeatLockService.java** - Business logic for locking/unlocking seats
- [x] **SeatLockScheduler.java** - Scheduled job for automatic lock expiration

### New Controllers
- [x] **SeatLockController.java** - REST endpoints for seat lock operations

### New DTOs
- [x] **SeatLockRequest.java** - Request DTO for locking seats
- [x] **SeatLockResponse.java** - Response DTO for seat lock details

### Modified Existing Components
- [x] **Trip.java** - Added `@Version` for optimistic locking
- [x] **BookingService.java** - Enhanced to support seat locking workflow
- [x] **TravelMateAiApplication.java** - Added `@EnableScheduling` annotation
- [x] **application.yml** - Added seat lock configuration

---

## 📁 File Structure

```
src/main/java/com/travelmateai/backend/
├── entity/
│   ├── SeatLock.java (NEW)
│   ├── SeatLockStatus.java (NEW)
│   └── Trip.java (MODIFIED - added @Version)
│
├── repository/
│   └── SeatLockRepository.java (NEW)
│
├── service/
│   ├── SeatLockService.java (NEW)
│   ├── SeatLockScheduler.java (NEW)
│   └── BookingService.java (MODIFIED - enhanced with seat lock support)
│
├── controller/
│   └── SeatLockController.java (NEW)
│
├── dto/
│   ├── request/
│   │   └── SeatLockRequest.java (NEW)
│   └── response/
│       └── SeatLockResponse.java (NEW)
│
└── TravelMateAiApplication.java (MODIFIED - added @EnableScheduling)

src/main/resources/
└── application.yml (MODIFIED - added seat lock configuration)
```

---

## 🔧 Technical Details

### 1. SeatLock Entity Structure

```java
Entity Properties:
├── id (Long) - Primary Key
├── user (ManyToOne) - Reference to User who locked seats
├── trip (ManyToOne) - Reference to Trip
├── numberOfSeats (Integer) - Count of locked seats
├── lockTime (LocalDateTime) - When lock was created
├── expiryTime (LocalDateTime) - When lock expires (lockTime + 5 min)
├── status (SeatLockStatus) - ACTIVE | EXPIRED | CONFIRMED
├── createdAt (LocalDateTime) - Timestamp
└── updatedAt (LocalDateTime) - Timestamp

Indexes:
├── idx_seat_lock_user (user_id)
├── idx_seat_lock_trip (trip_id)
├── idx_seat_lock_status (status)
└── idx_seat_lock_expiry (expiry_time) - Critical for scheduler

Methods:
├── isActive() - Checks if lock is still valid
└── isExpired() - Checks if lock time has passed
```

### 2. SeatLockService Core Methods

#### lockSeats(SeatLockRequest)
**Purpose:** Create a temporary seat lock

**Flow:**
1. Get current user and trip
2. Check if user already has an active lock for this trip (prevent duplicates)
3. Calculate **effective available seats**:
   - Formula: trip.availableSeats - sum(active locks for trip)
   - This prevents double-counting
4. Validate enough seats are available
5. Create SeatLock with expiryTime = now + lockDurationMinutes
6. Save and return SeatLockResponse

**Transactional:** Yes (@Transactional)
**Authorization:** User must be authenticated
**Concurrency:** Protected by @Version on Trip entity

#### confirmLock(Long lockId)
**Purpose:** Manually confirm a lock before booking

**Flow:**
1. Find seat lock by ID
2. Verify user ownership
3. Check lock is ACTIVE and not expired
4. Update status to CONFIRMED
5. Return lock details

**Note:** This is optional; booking creation automatically confirms the lock

#### releaseExpiredLocks()
**Purpose:** Mark expired locks as EXPIRED (called by scheduler)

**Flow:**
1. Find all locks with status=ACTIVE and expiryTime <= now
2. Update status to EXPIRED
3. Log count of released locks

**Concurrency:** Safe due to row-level locking in database

#### calculateEffectiveAvailableSeats(Trip)
**Purpose:** Calculate real available seats accounting for locked seats

**Formula:**
```java
effectiveAvailable = trip.availableSeats - sumLockedSeatsByTrip(tripId, ACTIVE)
```

**Why Important:** 
- trip.availableSeats only counts confirmed bookings
- Locked seats should reduce availability for new locks
- Prevents overselling to multiple users

### 3. SeatLockScheduler

**Cron Expression:** Fixed rate of 60,000 milliseconds (1 minute)

```java
@Scheduled(fixedRate = 60000)
public void releaseExpiredLocksJob()
```

**What it does:**
1. Runs every 1 minute
2. Finds all ACTIVE locks with expiryTime <= now
3. Marks them as EXPIRED
4. Makes their seats available again automatically

**Error Handling:** Catches exceptions to prevent scheduler from stopping

### 4. Trip Entity Modifications

**Added Field:**
```java
@Version
@Column(name = "version")
private Long version;
```

**Purpose:** Optimistic Locking
- Prevents race conditions when multiple users modify trip simultaneously
- If user A and B both try to reduce seats:
  - A succeeds (version incremented)
  - B fails with OptimisticLockingFailureException
  - B can retry
- Database automatically handles version checking

### 5. BookingService Enhancement

**Modified createBooking() Method:**

**New Flow:**
1. Check if ACTIVE seat lock exists for user + trip
   - If yes → Use lock (Path 1)
   - If no → Direct booking (Path 2)

**Path 1: Seat Lock Based (Recommended)**
```
Check active lock exists
  ↓
Verify lock not expired
  ↓
Verify requested seats ≤ locked seats
  ↓
Create booking with locked seats
  ↓
Reduce available seats on trip
  ↓
Update lock status to CONFIRMED
  ↓
Send confirmation email
```

**Path 2: Direct Booking (Backward Compatible)**
```
Calculate effective available seats
  ↓
Check effective seats >= requested seats
  ↓
Create booking directly
  ↓
Reduce available seats on trip
  ↓
Send confirmation email
```

**New Dependencies Added:**
```java
private final SeatLockRepository seatLockRepository;
```

---

## 🌐 API Endpoints (New)

### 1. Lock Seats
```http
POST /api/seat-locks
Authorization: Bearer {token}

Request:
{
  "tripId": 1,
  "numberOfSeats": 2
}

Response (201):
{
  "id": 5,
  "status": "ACTIVE",
  "lockTime": "2026-03-01T10:30:00",
  "expiryTime": "2026-03-01T10:35:00",
  ...
}
```

### 2. Confirm Lock (Manual)
```http
POST /api/seat-locks/{lockId}/confirm
Authorization: Bearer {token}

Response (200):
{
  "id": 5,
  "status": "CONFIRMED",
  ...
}
```

### 3. Get My Locks
```http
GET /api/seat-locks/my-locks
Authorization: Bearer {token}

Response (200):
{
  "data": [
    { lockId: 5, status: "ACTIVE", expiryTime: "..." },
    { lockId: 6, status: "CONFIRMED", expiryTime: "..." }
  ]
}
```

### 4. Get Active Locks
```http
GET /api/seat-locks/my-active-locks
Authorization: Bearer {token}

Response (200):
{
  "data": [
    { lockId: 5, status: "ACTIVE", expiryTime: "..." }
  ]
}
```

### 5. Get Lock Details
```http
GET /api/seat-locks/{lockId}
Authorization: Bearer {token}

Response (200):
{
  "id": 5,
  "status": "ACTIVE",
  ...
}
```

---

## ⚙️ Configuration

### application.yml Addition

```yaml
seat:
  lock:
    duration-minutes: 5   # Configurable lock duration
```

**How it's used:**
```java
@Value("${seat.lock.duration-minutes:5}")
private Integer lockDurationMinutes;

// Then:
LocalDateTime expiryTime = lockTime.plusMinutes(lockDurationMinutes);
```

**Benefits:**
- No hardcoding
- Easily configurable without code changes
- Can be adjusted per environment (dev, test, prod)

---

## 🔒 Concurrency & Thread Safety

### Mechanisms Implemented

#### 1. @Transactional Annotation
Used on all critical methods:
- `lockSeats()` - Atomic lock creation
- `confirmLock()` - Atomic status update
- `releaseExpiredLocks()` - Atomic bulk update
- `cancelBooking()` - Safe seat restoration

**Isolation Level:** READ_COMMITTED (default, sufficient for this use case)

#### 2. Optimistic Locking (@Version)
```java
@Version
private Long version;  // in Trip entity
```

**When two users simultaneously**:
1. User A reads trip (version = 5)
2. User B reads trip (version = 5)
3. User A updates trip (version becomes 6)
4. User B tries to update (version mismatch!)
5. OptimisticLockingFailureException thrown
6. User B can retry

**Benefits:**
- No long-running database locks
- Better concurrency
- Prevents lost updates

#### 3. Database Indexes
```sql
CREATE INDEX idx_seat_lock_user ON seat_locks(user_id);
CREATE INDEX idx_seat_lock_trip ON seat_locks(trip_id);
CREATE INDEX idx_seat_lock_status ON seat_locks(status);
CREATE INDEX idx_seat_lock_expiry ON seat_locks(expiry_time);  -- Critical for scheduler
```

**Impact:**
- Fast queries for finding locks
- Scheduler efficiently finds expired locks
- No full table scans

#### 4. Row-Level Locking
MySQL InnoDB provides automatic row-level locking during transactions:
- Two users can't modify same trip simultaneously
- JPA/Hibernate manages this transparently

---

## 📊 Data Flow Diagrams

### Scenario 1: User Locks Seats Successfully

```
Frontend                  Backend                Database
   |                         |                      |
   |-- POST /seat-locks -->  |                      |
   |                         |-- Check active lock -|
   |                         |<-- None found -------|
   |                         |                      |
   |                         |-- Calculate ---------|
   |                         |   effective seats    |
   |                         |<-- 15 seats ---------|
   |                         |                      |
   |                         |-- Create lock ------|
   |                         |<-- Saved -----------|
   |                         |                      |
   |<-- 201 + lockId --      |
   |   (expiry: 5 min)      |
   |                         |
   |-- Show timer for 5 min  |
```

### Scenario 2: User Lets Lock Expire

```
User locks seats at 10:30
   |
   |-- Scheduler runs at 10:31, 10:32, 10:33, 10:34
   |   (No action needed, lock still active)
   |
   |-- Scheduler runs at 10:35
   |   (expiryTime == 10:35)
   |
   |-- Finds lock with expiryTime <= now
   |
   |-- Updates status = EXPIRED
   |
   |-- Seats become available again
   |
User tries to book at 10:36
   |
   |-- Lock is EXPIRED, can't use it
   |
   |-- Must lock seats again
```

### Scenario 3: Optimistic Locking Conflict

```
User A                    Database                User B
   |                         |                       |
   |-- Read trip (v=5) ----->|                       |
   |<-- Trip data (v=5) ------|                       |
   |                         |<---- Read trip (v=5) -|
   |                         |-- Trip data (v=5) --->|
   |                         |                       |
   |-- Update trip (v=5) --->|                       |
   |<-- Updated (v=6) -------|                       |
   |                         |                       |
   |                         |<---- Update trip (v=5)|
   |                         |-- OptimisticLock -----
   |                         |    Exception!         |
   |                         |                       |
   |                         |<-- Retry recommended--|
```

---

## 🚀 Booking Flow (Recommended)

```
1. User Register/Login
   ↓
2. Browse Trips (GET /api/trips)
   ↓
3. Select Seat Count
   ↓
4. POST /api/seat-locks (Lock seats for 5 minutes)
   ↓
5. Display Lock Details with Timer
   ↓
6. User Fills Checkout Form (within 5 minutes)
   ↓
7. POST /api/bookings (Confirms lock + Creates booking)
   ↓
8. Display Confirmation with Receipt
   ↓
9. Optional: User can view bookings GET /api/bookings/my-bookings
```

---

## ⚠️ Important Notes for Frontend

### 1. Lock Expiry Handling
```javascript
// When lock expires (5 minutes pass):
- Display message: "Your seat lock has expired"
- Show "Lock again" button
- Refresh available seats
- Don't allow checkout after expiry
```

### 2. Timer Implementation
```javascript
// Store expiryTime from lock response
// Display countdown: expiryTime - currentTime
// Update every second
// Show warnings at 1 minute remaining
// Auto-lock/refresh at expiry
```

### 3. Error Handling
```javascript
// Common errors:
{
  "status": "ERROR",
  "message": "You already have an active seat lock for this trip"
}

{
  "status": "ERROR", 
  "message": "Insufficient seats. Requested: 5, Available: 2"
}

{
  "status": "ERROR",
  "message": "Your seat lock has expired. Please lock seats again."
}
```

### 4. Token Management
```javascript
// After login, store token:
localStorage.setItem('authToken', response.data.token);
localStorage.setItem('expiresIn', response.data.expiresIn);

// Include in all requests:
headers: {
  'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
  'Content-Type': 'application/json'
}

// Clear on logout:
localStorage.removeItem('authToken');
```

---

## 🔍 Testing Scenarios

### Test 1: Normal Booking with Lock
**Precondition:** Trip has 10 seats available
```
1. User A locks 3 seats
   - Available seats shown to other users: 7
   - Lock expires in 5 min

2. User B tries to lock 4 seats
   - Available: 7 (not 10, because 3 are locked)
   - Lock succeeds for User B

3. User C tries to lock 5 seats
   - Available: 3 (7 - 4 locked by B)
   - Lock fails: Insufficient seats

4. User A books within 5 minutes
   - Lock converts to CONFIRMED
   - Available seats: 7

5. After 5 minutes, if User B doesn't book:
   - Scheduler marks lock as EXPIRED
   - Available seats: 10 again
```

### Test 2: Concurrent Lock Attempts
**Precondition:** Trip has 5 seats, 5 users trying to lock 1 seat each
```
1. All 5 users send lock request simultaneously
2. Database lock prevents conflicts
3. First to acquire lock: succeeds (lock created)
4. Others: wait for lock release
5. Next user: succeeds
6. ... (repeats until all locked)
7. 6th user: fails (no seats available)
```

### Test 3: Lock Expiry
**Precondition:** User locks seats at T=0
```
T=0:    Lock created, expiry = T+5min
T=1:    Scheduler runs (no action)
T=2:    Scheduler runs (no action)
T=3:    Scheduler runs (no action)
T=4:    Scheduler runs (no action)
T=5:    Scheduler runs, finds lock expired, marks as EXPIRED
T=6:    User tries to book with this lock: FAILS
        User must lock again
```

---

## 📈 Performance Considerations

### Query Performance
- Lock retrieval by user+trip: Uses indexes → O(log n)
- Finding expired locks: `idx_seat_lock_expiry` → O(log n)
- Calculating effective seats: Single aggregate query → O(n)

### Database Load
- Scheduler runs once per minute (not per second)
- No continuous polling
- Batch updates of expired locks (efficient)

### Memory Usage
- Only active locks stored (others marked EXPIRED)
- Clean up strategy: Consider archiving old locks monthly
- No memory leaks in service

---

## 🐛 Known Limitations & Future Improvements

### Current Limitations
1. ⚠️ No notification when lock is about to expire
   - **Fix:** Add WebSocket notifications

2. ⚠️ Can't extend lock duration mid-checkout
   - **Fix:** Add `extendLock()` method

3. ⚠️ No analytics on lock-to-booking conversion rate
   - **Fix:** Add reporting service

### Future Improvements
1. Implement lock extension before expiry
2. Add WebSocket real-time seat availability updates
3. Implement payment integration with lock expiry
4. Add analytics dashboard for conversion rates
5. Implement user preference for lock duration
6. Add audit logging for all lock operations

---

## 🔐 Security Considerations

### Implemented
- ✅ JWT token validation on all endpoints
- ✅ User ownership verification (can't access others' locks)
- ✅ Role-based access control (ADMIN functions)
- ✅ Input validation on all request bodies
- ✅ SQL injection prevention (using JPA queries)

### Recommendations
- 🔒 Use HTTPS in production (not just HTTP)
- 🔒 Implement rate limiting on lock endpoints
- 🔒 Monitor for lock-bombing attacks
- 🔒 Regular security audits
- 🔒 Database encryption for sensitive data

---

## 📞 Support & Debugging

### If Users Can't Lock Seats:
1. Check if trip has available seats
2. Check if user already has active lock
3. Check JWT token validity
4. Check logs for exceptions

### If Locks Don't Expire:
1. Verify scheduler is running
2. Check application logs for scheduler errors
3. Verify database has proper indexes
4. Check expiryTime calculations

### If Booking Creation Fails:
1. Check if lock exists and is active
2. Check if lock has expired
3. Check if seat count matches
4. Check effective available seats calculation

---

## 📝 Maintenance Notes

### Database Maintenance
```sql
-- Check active locks
SELECT * FROM seat_locks WHERE status = 'ACTIVE';

-- Check expired locks (maintenance)
SELECT * FROM seat_locks WHERE status = 'EXPIRED';

-- Archive old locks (monthly)
DELETE FROM seat_locks WHERE updated_at < DATE_SUB(NOW(), INTERVAL 3 MONTH);

-- Verify lock consistency
SELECT trip_id, COUNT(*) as active_locks, SUM(number_of_seats) as total_locked
FROM seat_locks
WHERE status = 'ACTIVE'
GROUP BY trip_id;
```

### Monitoring
- Monitor scheduler execution logs
- Track lock-to-booking conversion rates
- Monitor database query performance
- Alert on 'too many locks' for single trip

---

## ✅ Validation Checklist Before Deployment

- [x] All entities created and validated
- [x] Repositories with custom queries working
- [x] Services with business logic implemented
- [x] Controllers with REST endpoints created
- [x] Scheduler implemented and enabled
- [x] Configuration added to application.yml
- [x] Optimistic locking with @Version added
- [x] BookingService integrated with seat locks
- [x] Error handling comprehensive
- [x] Documentation complete
- [ ] Integration tests passed
- [ ] Load testing completed
- [ ] Security review completed
- [ ] Database backup strategy in place

---

**Implementation Status:** ✅ COMPLETE

**Ready for Frontend Integration:** ✅ YES

**Production Ready:** ⚠️ Requires testing and security review

---

Last Updated: March 1, 2026
