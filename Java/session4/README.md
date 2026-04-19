# Todo Management API

A simple Spring Boot REST API for managing todos with CRUD operations, DTO mapping, status handling, validation, logging, exception handling, and unit testing.

---

## Features

* Create a new todo
* Get all todos
* Get todo by id
* Update an existing todo
* Delete a todo
* Status support using enum
* Default status handling
* Status transition validation
* Exception handling for invalid ids
* Logging using SLF4J
* DTO mapping
* Unit testing using JUnit and Mockito
* Notification service call after creating a todo

---

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven
* JUnit 5
* Mockito
* SLF4J Logger

---

## Project Structure

```text
src
 ┣ main
 ┃ ┣ java/com/example
 ┃ ┃ ┣ client
 ┃ ┃ ┃ ┗ NotificationServiceClient.java
 ┃ ┃ ┣ controller
 ┃ ┃ ┃ ┗ TodoController.java
 ┃ ┃ ┣ dto
 ┃ ┃ ┃ ┣ RequestDTO.java
 ┃ ┃ ┃ ┗ ResponseDTO.java
 ┃ ┃ ┣ entity
 ┃ ┃ ┃ ┗ Todo.java
 ┃ ┃ ┣ enums
 ┃ ┃ ┃ ┗ TodoStatus.java
 ┃ ┃ ┣ exception
 ┃ ┃ ┃ ┗ ResourceNotFoundException.java
 ┃ ┃ ┣ mapper
 ┃ ┃ ┃ ┗ TodoMapper.java
 ┃ ┃ ┣ repository
 ┃ ┃ ┃ ┗ TodoRepository.java
 ┃ ┃ ┣ service
 ┃ ┃ ┃ ┣ TodoService.java
 ┃ ┃ ┃ ┗ TodoServiceImplementation.java
 ┃ ┃ ┗ TodoManagementApiApplication.java
 ┃ ┗ resources
 ┃ ┃ ┗ application.properties
 ┗ test
 ┃ ┗ java/com/example/service
 ┃ ┃ ┗ TodoServiceImplementationTest.java
```

---

## Entity

The Todo entity stores all information related to a todo item. 

Fields:

* id
* title
* description
* status
* createdAt

---

## Todo Status Enum

Todo status is stored using enum.

Values:

* PENDING
* COMPLETED

This helps in restricting status values and avoids invalid strings.

---

## DTOs

### RequestDTO

Used for taking request data from the client.

Fields:

* title
* description
* status

### ResponseDTO

Used for sending response data back to the client.

Fields:

* id
* title
* description
* status
* createdAt

DTOs are useful because they help separate internal entity structure from API request and response structure.

---

## Mapper

Mapper is used for converting:

* RequestDTO -> Todo Entity
* Todo Entity -> ResponseDTO

This keeps the service layer cleaner and separates entity structure from API response structure.

---

## Service Layer Logic

### Create Todo

Steps:

1. Convert RequestDTO to Todo entity
2. Set createdAt using LocalDateTime.now()
3. If status is null, set default status as PENDING
4. Save todo in database
5. Send notification
6. Convert saved entity to ResponseDTO

---

### Get All Todos

Steps:

1. Fetch all todos from database
2. Convert list of entities into list of ResponseDTO
3. Return the response list

---

### Get Todo By Id

Steps:

1. Find todo using id
2. If not found, throw ResourceNotFoundException
3. Convert entity to ResponseDTO
4. Return response

---

### Update Todo

Steps:

1. Find existing todo by id
2. If id does not exist, throw ResourceNotFoundException
3. Update title and description
4. Check status transition
5. Save updated todo
6. Return ResponseDTO

Status transition rules:

Valid:

* PENDING -> COMPLETED
* COMPLETED -> PENDING

Invalid:

* PENDING -> PENDING
* COMPLETED -> COMPLETED

---

### Delete Todo

Steps:

1. Find todo by id
2. If not found, throw ResourceNotFoundException
3. Delete the todo

---

## Exception Handling

### ResourceNotFoundException

Used when the given id does not exist in the database.

### IllegalArgumentException

Used for invalid status transitions.

---

## Logging

SLF4J Logger is used for better tracking of flow.

Logging helps in debugging and understanding what is happening in the application.

Examples of logs used:

* Started creating new todo
* Getting all todos
* Updating todo by id
* Deleting todo by id

---

## API Endpoints

| Method | Endpoint    | Description       |
| ------ | ----------- | ----------------- |
| POST   | /todos      | Create a new todo |
| GET    | /todos      | Get all todos     |
| GET    | /todos/{id} | Get todo by id    |
| PUT    | /todos/{id} | Update todo       |
| DELETE | /todos/{id} | Delete todo       |

---

## ThunderClient API Testing Snapshots

1. GET  /todos
   <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/6a1d2205-1781-4888-9391-57edf0862ce4" />

2. GET  /todos/{id}
   <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/c100aabc-4ae0-49ef-b169-3f14061616b7" />

3. POST  /todos
    <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/e6c51a2f-4562-4942-820a-5ff946a37634" />

4. PUT  /todos/{id}  
   Invalid transition
   <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/85be61b9-27d8-480d-a841-99aebea65726" />
   
   Valid transition
   <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/18792e69-a72f-4bce-898d-9e07d93c8996" />

5. DELETE  /todos/{id}
   <img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/163b3aee-06cb-4e8a-b6e5-bf03ade07b5f" />

---

## Unit Testing

Unit testing is done using JUnit and Mockito.

Test cases included:

* shouldCreateTodoSuccessfully
* shouldGetByIdSuccessfully
* shouldGetAllTodo
* shouldUpdateTodoSuccessfully
* shouldDeleteTodoSuccessfully

Mockito is used for:

* Mocking TodoRepository
* Mocking NotificationServiceClient
* Verifying repository calls
* Verifying notification calls

---

## application.properties Example

You need to configure:

* Database URL
* Database username
* Database password
* Hibernate ddl-auto property
* SQL logging property

---

## Conclusion

This project helped me understand:

* Layered architecture
* DTO mapping
* CRUD operations
* JPA repository usage
* Exception handling
* Logging
* Enum handling
* Unit testing with Mockito
* Status transition validation

