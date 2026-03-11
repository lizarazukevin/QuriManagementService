# Quri Management Service

> Backend management service for the best bill splitter application.

A Kotlin/Spring Boot microservice providing RESTful APIs for user profile and bill management. Built on top of [Smithy](https://smithy.io/) for API generation and MongoDB for persistence.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2.20 (JVM 21) |
| Framework | Spring Boot 4.0.0-RC1 |
| API | Smithy Java Server (Netty) |
| Database | MongoDB (Kotlin coroutine driver) |
| Build | Gradle (Kotlin DSL) |

---

## Prerequisites

- **JDK 21+**
- **Gradle 8+** (or use the included `./gradlew` wrapper)
- **MongoDB** — local instance or [MongoDB Atlas](https://www.mongodb.com/atlas)
- **mise** (optional) — toolchain version management via `mise.toml`

---

## Getting Started

### 1. Clone

```bash
git clone https://github.com/lizarazukevin/QuriManagementService.git
cd QuriManagementService
```

### 2. Configure environment variables

The service reads MongoDB credentials from the environment:

```bash
export MONGO_USERNAME="your_username"
export MONGO_PASSWORD="your_password"
export MONGO_CLUSTER="your-cluster.mongodb.net"
export MONGO_APP_NAME="quri-management-service"
```

### 3. Build and run

```bash
./gradlew bootRun
```

The service starts at `http://localhost:8080`.

---

## API

All endpoints accept and return JSON.

### Profiles

| Method | Path | Description |
|---|---|---|
| `POST` | `/profiles` | Create a profile |
| `GET` | `/profiles/{id}` | Get a profile |
| `GET` | `/profiles` | List all profiles |
| `DELETE` | `/profiles/{id}` | Delete a profile |

**Create profile request body:**
```json
{
  "username": "jdoe",
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane@example.com",
  "phoneNumber": "+1234567890"
}
```

### Bills

| Method | Path | Description |
|---|---|---|
| `POST` | `/bills` | Create a bill |
| `GET` | `/bills/{id}` | Get a bill |
| `GET` | `/bills` | List all bills |
| `DELETE` | `/bills/{id}` | Delete a bill |

**Create bill request body:**
```json
{
  "total": { "amount": "100.00", "currencyCode": "USD" },
  "balance": { "amount": "50.00", "currencyCode": "USD" }
}
```

---

## Project Status

This service is under active development. Known limitations:

- **Blocking handlers** — API handlers currently use `runBlocking` as a workaround until async Smithy codegen for Kotlin is available. This may impact throughput under load.
- **No test suite** — Tests are disabled in the build config and planned for a future milestone.
- **Basic error handling** — Edge cases and input validation are not fully covered yet.

### Roadmap

- [ ] Migrate to async Smithy operations
- [ ] Add a proper test suite (unit + integration)
- [ ] Docker support
- [ ] Auth layer
- [ ] OpenAPI/Swagger docs
- [ ] Monitoring and metrics

---

## Contributing

1. Fork the repo and create a feature branch: `git checkout -b feature/my-feature`
2. Commit your changes: `git commit -m "Add my feature"`
3. Push and open a pull request

Please follow existing code style and architecture conventions. Update docs alongside code changes.

> Sign your commits by creating SSH keys and adding them to your account: https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent

---

## License

MIT — see [LICENSE](LICENSE) for details.