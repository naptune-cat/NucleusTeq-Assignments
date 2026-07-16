# CircleUp Database Schema

## Tables

### `users`

| Column          | Type        | Constraints      | Description                   |
| --------------- | ----------- | ---------------- | ----------------------------- |
| `id`            | Integer     | Primary Key      | Auto-incremented user ID      |
| `name`          | String(100) | Not Null         | User's full name              |
| `email`         | String(100) | Unique, Not Null | User's email address          |
| `phone_number`  | String(20)  | Not Null         | User's phone number           |
| `city`          | String(100) | Not Null         | User's city                   |
| `bio`           | Text        | Nullable         | User's short biography        |
| `password_hash` | String(255) | Not Null         | Argon2 hashed password        |
| `created_at`    | DateTime    | Default: NOW()   | Timestamp of account creation |

### `activities`

| Column             | Type        | Constraints              | Description                              |
| ------------------ | ----------- | ------------------------ | ---------------------------------------- |
| `id`               | Integer     | Primary Key              | Auto-incremented activity ID             |
| `creator_id`       | Integer     | Foreign Key (`users.id`) | The user who created the activity        |
| `title`            | String(150) | Not Null                 | Activity title                           |
| `description`      | Text        | Not Null                 | Detailed description                     |
| `category`         | Enum        | Not Null                 | Activity type (e.g. Sports, Meetup)      |
| `location`         | String(200) | Not Null                 | Where it takes place                     |
| `date`             | Date        | Not Null                 | Activity date                            |
| `time`             | Time        | Not Null                 | Activity time                            |
| `max_participants` | Integer     | Not Null (>0)            | Participant limit                        |
| `status`           | Enum        | Not Null                 | Status: Open, Full, Cancelled, Completed |
| `created_at`       | DateTime    | Default: NOW()           | When the activity was created            |
| `updated_at`       | DateTime    | Default: NOW()           | When the activity was last updated       |

### `participations`

| Column        | Type     | Constraints                   | Description                  |
| ------------- | -------- | ----------------------------- | ---------------------------- |
| `id`          | Integer  | Primary Key                   | Auto-incremented request ID  |
| `user_id`     | Integer  | Foreign Key (`users.id`)      | The user requesting to join  |
| `activity_id` | Integer  | Foreign Key (`activities.id`) | The activity to join         |
| `status`      | Enum     | Default: Pending              | Pending, Approved, Rejected  |
| `created_at`  | DateTime | Default: NOW()                | When the request was made    |
| `updated_at`  | DateTime | Default: NOW()                | When the request was updated |

## Relationships

- A `User` has many `Activities` (1:N)
- A `User` has many `Participations` (1:N)
- An `Activity` has many `Participations` (1:N)

## ER Diagram

<img width="1078" height="545" alt="Screenshot 2026-07-09 201819" src="https://github.com/user-attachments/assets/a3990f2a-02ff-47c3-adf6-e8285682122d" />
