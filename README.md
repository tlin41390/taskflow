# TaskFlow

TaskFlow is a small REST API for managing a to-do list, built with Spring Boot. You can create, list, filter, view, update and delete tasks. Each task has a title and a done/not-done status.

Anyone can read tasks. Creating and updating tasks requires a login, and deleting requires an admin login. When a task is created, TaskFlow also sends a notification to a separate notification service.

Tasks and users are stored in an in-memory H2 database, so **all data is lost when the application stops**. Two user accounts are created at startup.

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security)
- H2 in-memory database, with the H2 web console
- Maven (the wrapper is included, so you don't need to install Maven)

## Getting started

### Prerequisites

- JDK 25 or newer
- A notification service listening on `http://localhost:8081`, if you want to create tasks. See [Notification service](#notification-service).

### Run the application

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`.

### Run the tests

```bash
./mvnw test        # or mvnw.cmd test on Windows
```

| Test class                  | What it tests                                                                          |
|-----------------------------|----------------------------------------------------------------------------------------|
| `TaskControllerTest`        | Endpoints, validation, error bodies and security rules, with `TaskService` mocked       |
| `TaskRepositoryTest`        | Saving tasks and filtering by `done`, against a real in-memory database                 |
| `TaskflowApplicationTests`  | The whole application starts                                                            |

## Authentication

TaskFlow uses **HTTP Basic** authentication. Send a username and password with each request that needs one, for example with `curl -u alice:alicepass`.

These accounts are created when the app starts:

| Username | Password    | Role    |
|----------|-------------|---------|
| `admin`  | `adminpass` | `ADMIN` |
| `alice`  | `alicepass` | `USER`  |

> These passwords are for local development only. They are set in `DataSeeder.java`.

Access rules:

| Request                       | Who can do it               |
|-------------------------------|-----------------------------|
| `GET /tasks`, `GET /tasks/{id}` | Anyone, no login needed    |
| `POST /tasks`, `PUT /tasks/{id}` | Any logged-in user        |
| `DELETE /tasks/{id}`          | `ADMIN` only                |
| Everything else (including the H2 console) | Any logged-in user |

If you send no login or the wrong password, you get `401 Unauthorized`. If you're logged in but not allowed, for example `alice` deleting a task, you get `403 Forbidden`.

## API

All requests and responses use JSON.

### Task object

```json
{
  "id": 1,
  "title": "Buy groceries",
  "done": false
}
```

### Endpoints

| Method   | Path                | Login      | Description                 | Success       | Errors                          |
|----------|---------------------|------------|-----------------------------|---------------|---------------------------------|
| `GET`    | `/tasks`            | —          | List all tasks              | `200` + array | —                               |
| `GET`    | `/tasks?done=true`  | —          | List only done tasks        | `200` + array | —                               |
| `GET`    | `/tasks?done=false` | —          | List only unfinished tasks  | `200` + array | —                               |
| `POST`   | `/tasks`            | Any user   | Create a task               | `200` + task  | `400`, `401`, `500` (see below) |
| `GET`    | `/tasks/{id}`       | —          | Get one task                | `200` + task  | `404`                           |
| `PUT`    | `/tasks/{id}`       | Any user   | Update a task               | `200` + task  | `400`, `401`, `404`             |
| `DELETE` | `/tasks/{id}`       | Admin      | Delete a task               | `204` no body | `401`, `403`, `404`             |

### Request bodies

**Create** (`POST /tasks`). New tasks always start as not done.

```json
{ "title": "Buy groceries" }
```

**Update** (`PUT /tasks/{id}`). This replaces both fields, so send both. `done` is required. If you leave it out, the request is rejected with `400`.

```json
{ "title": "Buy groceries", "done": true }
```

In both requests, `title` is required and must not be blank.

### Error responses

A missing task (`404`) or a blank title (`400`) returns a JSON body in this format:

```json
{
  "timestamp": "2026-10-19T21:27:57.251Z",
  "status": 404,
  "message": "Task 99 not found"
}
```

For a blank title, `message` is `title must not be blank`.

Other errors use Spring Boot's default error format instead. Examples are invalid JSON, a missing `done` field, or a failed notification. That format has `error` and `path` fields and no `message`:

```json
{
  "timestamp": "2026-10-19T21:27:43.607Z",
  "status": 400,
  "error": "Bad Request",
  "path": "/tasks/1"
}
```

### Example with curl

```bash
# List tasks (no login needed)
curl http://localhost:8080/tasks

# List only unfinished tasks
curl "http://localhost:8080/tasks?done=false"

# Create a task
curl -u alice:alicepass -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Buy groceries"}'

# Mark task 1 as done
curl -u alice:alicepass -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Buy groceries", "done": true}'

# Delete task 1 (admin only)
curl -u admin:adminpass -X DELETE http://localhost:8080/tasks/1
```

On Windows PowerShell, use `curl.exe`, put each command on one line, and escape the inner quotes, for example `-d '{\"title\": \"Buy groceries\"}'`.

## Notification service

Each time a task is created, TaskFlow sends this request to a separate service:

```
POST http://localhost:8081/notifications
Content-Type: application/json

{ "taskId": 1, "message": "Task created: Buy groceries" }
```

The service isn't part of this project. Its address is set by `notification-service.service.url` in `application.properties`.

> **If the notification service isn't running, `POST /tasks` returns `500 Internal Server Error`, but the task is still saved.** The task is saved first and the notification is sent afterwards, and a failed notification isn't handled. To try creating tasks without the real service, run any server on port 8081 that accepts `POST /notifications`.

## Inspecting the database

The H2 web console is enabled at `http://localhost:8080/h2-console`. The connection details are:

- **JDBC URL:** `jdbc:h2:mem:taskflow`
- **User name:** `sa`
- **Password:** leave empty

> **The console doesn't currently work in a browser.** It requires a TaskFlow login, like every path not listed above. Even after you log in, Spring Security sends `X-Frame-Options: DENY` by default, which stops the console's page frames from loading. Using it would need a security rule that allows `/h2-console/**` and same-origin frames.

## Project structure

```
src/main/java/com/taskflow/
├── TaskflowApplication.java                # Application entry point
├── controller/TaskController.java          # REST endpoints
├── service/TaskService.java                # Business logic; sends a notification after create
├── repository/
│   ├── TaskRepository.java                 # Task storage, including findByDone
│   └── UserRepository.java                 # User lookup by username
├── model/
│   ├── Task.java                           # Task entity
│   └── AppUser.java                        # User entity (username, password hash, role)
├── dto/
│   ├── CreateTaskRequest.java              # POST body (with validation)
│   ├── UpdateTaskRequest.java              # PUT body (with validation)
│   └── ErrorResponse.java                  # JSON error body
├── exception/
│   ├── TaskNotFoundException.java          # Thrown when a task id doesn't exist
│   └── GlobalExceptionHandler.java         # Turns exceptions into 404/400 error bodies
├── config/
│   ├── SecurityConfig.java                 # Access rules, HTTP Basic, password hashing
│   ├── TaskFlowUserDetailsService.java     # Loads users from the database for login
│   └── DataSeeder.java                     # Creates the admin and alice accounts at startup
└── client/NotificationClient.java          # Calls the notification service
src/main/resources/
└── application.properties                  # Database, error-message and notification-service settings
```

A request first goes through Spring Security, which checks the login and the access rules. It then goes from the controller to the service, and from the service to the repository, which reads and writes the database. When a task is created, the service also calls `NotificationClient`. If something goes wrong, `GlobalExceptionHandler` turns the error into a JSON response.

Passwords are stored as BCrypt hashes, never as plain text.
