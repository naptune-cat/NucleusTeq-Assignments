

# User Management System

## Project Description

This is a simple Spring Boot REST API project for managing users.
It supports basic operations like:

* Get all users
* Get user by ID
* Add a new user
* Delete a user by ID
* Format Message (long/short/invalid)
* Send Notification
* Update full user details (PUT)
* Update partial user details (PATCH)

This project was created to practice Spring Boot basics such as:

* Controllers
* Services
* Repositories
* Dependency Injection
* Exception Handling
* REST APIs

---

## Technologies Used

* Java
* Spring Boot
* Maven
* Thunder Client/ Postman api

---

## Project Structure

```html
src
 └── main
      └── java
           └── com.example.usermanagementsystem
                ├── controller
                ├── service
                ├── repository
                ├── model
                ├── exception
                └── UserManagementSystemApplication.java
```

---

## How to Run the Project

1. Open the project in VS Code
2. Open terminal in the project root folder
3. Run the following command:

```html
mvn spring-boot:run
```

4. The server will start on:

```html
http://localhost:8080
```

---

## API Endpoints

### 1. Get All Users

```html
GET /users
```

Example:

```html
http://localhost:8080/users
```

---

### 2. Get User By ID

```html
GET /users/{id}
```

Example:

```html
http://localhost:8080/users/1
```

---

### 3. Add User

```html
POST /users
```

Example Request Body:

```html
{
  "id": 4,
  "name": "Zoya",
  "email": "zoya@gmail.com"
}
```

---

### 4. Delete User By ID

```html
DELETE /users/{id}
```

Example:

```html
http://localhost:8080/users/2
```

---

### 5. Send Notification

```html
POST /notifications
```

Example:

```html
http://localhost:8080/notifications
```

---

### 6. Partial updation of user details

```html
PATCH /users/{id}
```

Example:

```html
http://localhost:8080/users/1
```

---

### 7. Send Message

```html
POST /message?type=SHORT
```

Example:

```html
http://localhost:8080/message?type=SHORT
```

---

### 8. Update full user details

```html
PUT /users/{id}
```

Example:

```html
http://localhost:8080/users/2
```

---
## Exception Handling

Custom exception handling is used to return meaningful error messages when:

* User ID does not exist
* Invalid request is sent

Example Error Response:

```html
{
  "message": "User not found with id: 2"
}
```

---

## Testing

This project can be tested using:

* Postman
* Thunder Client

---

# Test Screenshots

* GET /users
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/8f0d5bdd-d6b2-42fc-859d-901d4f5584ed" />

---

* GET /users/{id}
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/bd76c4e3-3ee3-4845-9c1f-ce9c3bc645b5" />

---

* POST /users
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/156ed2af-3d4b-4c74-9f49-4e8b3fc14cd9" />
after the post request
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/5a8d748c-9170-4263-b769-88c5c0868916" />

---

* POST /users/message?type=SHORT
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/fc161bd3-0d78-4e20-b448-9b3bab973546" />

---

* POST /users/message?type={m}
  this is the invalid type
  <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/8e9a364c-5fb6-4698-97d3-f10392b6d300" />
  
---

* DELETE /users/{id}
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/0eadf67f-b308-4246-b0ca-f5dfb2b13f44" />
if we try to delete again we will get error message
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/5fa779a5-06e0-4301-9d80-5d04edfeaaad" />

---

* POST /users/notification
  <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/f482537d-e8fd-4fc0-a4f4-a12a26c8dada" />

---

* PUT /users/{id}
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/288053b0-b0c0-480d-9ffa-264410e7daa4" />

after updation
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/0b9ed042-0297-4af1-bb16-f52f6e9d6fed" />

---

*PATCH /users/{id}
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/556c30bc-a8ee-4a3d-a12f-cdec10656064" />
after updation
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/00b02edb-569c-4516-a8b9-21fcfacb7308" />

---

## Author

Zoya Khanam
