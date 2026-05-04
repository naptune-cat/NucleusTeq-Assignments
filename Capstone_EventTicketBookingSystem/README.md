# 🎫 Evently.in

A full-stack web application for managing event bookings, tickets, and payments.

---

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Frontend Pages](#frontend-pages)
- [Key Concepts](#key-concepts)
- [Testing](#testing)
- [Security](#security)

---

## ✨ Features

### 🎯 Organizers
- Create, edit (4hr restriction), cancel events  
- View analytics & dashboard stats  
- Export attendees (CSV)

### 👥 Customers
- Browse events & view details  
- Two-phase booking (Booking → Payment)  
- Cancel bookings (3hr restriction)  
- View booking history  

### 🔒 Security
- JWT auth (30 min expiry)  
- Role-based access (CUSTOMER / ORGANIZER)  
- BCrypt password hashing  

---

## 🛠️ Tech Stack

**Backend**
- Spring Boot, Java 17, MySQL, Hibernate, JWT, Maven  

**Frontend**
- HTML, CSS, JavaScript (ES6), Fetch API, localStorage  

---

## 📁 Project Structure

```
event-ticket-booking-system/
│
├── backend/
│   ├── entity/
│   ├── dto/
│   ├── service/
│   ├── controller/
│   ├── repository/
│   └── security/
│
└── frontend/
    ├── index.html
    ├── dashboard.html
    ├── payment.html
    ├── js/
    └── css/
```

---

## 🚀 Setup & Installation

### Backend

```bash
cd backend
```

```sql
CREATE DATABASE event_booking_system;
```

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/event_booking_system
spring.datasource.username=root
spring.datasource.password=your_password

app.jwt.secret=your_secret_key
app.jwt.expiration-ms=1800000
```

```bash
mvn clean install
mvn spring-boot:run
```

➡ Runs at: http://localhost:8080

---

### Frontend

```bash
cd frontend
npm install -g live-server
live-server
```

➡ Runs at: http://127.0.0.1:5500

---

## 🗄️ Database Schema

**Users**
```sql
id, name, email, password, phone, role
```

**Events**
```sql
id, event_name, venue, event_date_time, seats, price, status
```

**Bookings**
```sql
id, user_id, event_id, tickets, amount, status
```

**Payments**
```sql
id, booking_id, amount, payment_method
```

---

## 🔌 API Endpoints

### Auth
- POST `/api/auth/register`
- POST `/api/auth/login`

### Events
- GET `/api/events`
- POST `/api/events`
- PUT `/api/events/{id}`
- PATCH `/api/events/{id}/cancel`

### Bookings
- POST `/api/bookings`
- POST `/api/bookings/payment`
- GET `/api/bookings/my`

---

## 📄 Frontend Pages

- index.html — Events list  
- login.html / register.html  
- dashboard.html — Organizer  
- event-detail.html — Booking  
- payment.html  
- myBookings.html  

---

## 🧠 Key Concepts

Two-Phase Booking:
1. Create booking (PENDING)  
2. Confirm payment → CONFIRMED  

Concurrency Handling:
- Pessimistic locking prevents double booking  

JWT:
- Stateless authentication with expiry  

---

## 🧪 Testing

- 100+ tests (JUnit, Mockito)

```bash
mvn test
```

---

## 🔐 Security

- BCrypt password hashing  
- JWT authentication  
- Role-based authorization  
- DTO pattern (no sensitive data exposure)  

---

## 🎓 Learning Outcomes

- Full-stack development  
- REST APIs  
- JWT security  
- Database design  
- Testing & debugging  

---

## 🎉 Thank You!
