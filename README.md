# TaskFlow

TaskFlow is a small REST API for managing a to-do list, built with Spring Boot. You can create, list, view, update and delete tasks. Each task has a title and a done/not-done status.

Tasks are stored in an in-memory H2 database, so **all data is lost when the application stops**.

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- H2 in-memory database, with the H2 web console
- Maven (the wrapper is included, so you don't need to install Maven)

## Getting started

### Prerequisites

- JDK 25 or newer

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

| Method   | Path           | Description          | Success         | Errors                    |
|----------|----------------|----------------------|-----------------|---------------------------|
| `GET`    | `/tasks`       | List all tasks       | `200` + array   | —                         |
| `POST`   | `/tasks`       | Create a task        | `200` + task    | `400` if title is blank   |
| `GET`    | `/tasks/{id}`  | Get one task         | `200` + task    | `404` if not found        |
| `PUT`    | `/tasks/{id}`  | Update a task        | `200` + task    | `400` blank title, `404`  |
| `DELETE` | `/tasks/{id}`  | Delete a task        | `204` no body   | `404` if not found        |

### Request bodies

**Create** (`POST /tasks`). New tasks always start as not done.

```json
{ "title": "Buy groceries" }
```

**Update** (`PUT /tasks/{id}`). This replaces both fields. If you leave out `done`, it is set to `false`.

```json
{ "title": "Buy groceries", "done": true }
```

In both requests, `title` is required and must not be blank. A blank title returns `400 Bad Request` with the message `title must not be blank`.

### Example with curl

```bash
# Create a task
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Buy groceries"}'

# List tasks
curl http://localhost:8080/tasks

# Mark task 1 as done
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Buy groceries", "done": true}'

# Delete task 1
curl -X DELETE http://localhost:8080/tasks/1
```

## Inspecting the database

The H2 web console is enabled. While the app is running, open `http://localhost:8080/h2-console` and connect with:

- **JDBC URL:** `jdbc:h2:mem:taskflow`
- **User name:** `sa`
- **Password:** leave empty

## Project structure

```
src/main/java/com/taskflow/
├── TaskflowApplication.java      # Application entry point
├── controller/TaskController.java # REST endpoints
├── service/TaskService.java       # Business logic
├── repository/TaskRepository.java # Spring Data JPA repository
├── model/Task.java                # Task entity
└── dto/                           # Request bodies (with validation)
    ├── CreateTaskRequest.java
    └── UpdateTaskRequest.java
src/main/resources/
└── application.properties         # Database and error-message settings
```

Requests go from the controller to the service, then to the repository, which reads and writes the database.
