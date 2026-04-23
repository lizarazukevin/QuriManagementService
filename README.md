# Quri Management Service

> Backend management service for the best bill splitter application.

A Kotlin/Spring Boot microservice providing RESTful APIs for user profile and bill management. Uses [Smithy](https://smithy.io/) client models for request/response types and MongoDB for persistence.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2.20 (JVM 21) |
| Framework | Spring Boot 4.0.0-RC1 |
| Web | Spring WebFlux (Reactive) |
| API Models | Smithy Client Models (input/output shapes) |
| Auth | Spring Security OAuth2 Resource Server (JWT / Clerk) |
| Database | MongoDB (Kotlin coroutine driver) |
| Build | Gradle (Kotlin DSL) |

---

## Prerequisites

- **JDK 21+**
- **Gradle 8+** (or use the included `./gradlew` wrapper)
- **MongoDB** — local instance or [MongoDB Atlas](https://www.mongodb.com/atlas)
- **Clerk** — a Clerk application for JWT issuance and JWKS endpoint
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

## Authentication

This service acts as an **OAuth2 Resource Server** that validates JWTs issued by [Clerk](https://clerk.com/).

### How it works

1. **JWT verification** — Every incoming request must include a valid `Authorization: Bearer <token>` header. Spring Security verifies the token signature and expiry against Clerk's JWKS endpoint.
2. **Role extraction** — The service reads `metadata.role` from the Clerk session token claims and maps it to Spring Security authorities using the `ROLE_*` prefix convention.
3. **Authorization** — All endpoints require an authenticated user (`anyExchange().authenticated()`).

### Auth failure responses

| Scenario | Response |
|---|---|
| Missing or invalid token | `401 Unauthorized` |

> For more details on extending session tokens with metadata, see the [Clerk docs](https://clerk.com/docs/guides/users/extending#metadata-in-the-session-token).

---

## Code Quality

This project uses static analysis tools to maintain code quality and consistency:

### Detekt
[Detekt](https://detekt.dev/) is a static analysis tool for Kotlin that helps identify code smells, complexity issues, and potential bugs on every call to build. It's configured with comprehensive rules covering:

- **Complexity analysis** - Detects overly complex functions and classes
- **Naming conventions** - Enforces consistent naming patterns
- **Performance optimizations** - Identifies potential performance issues
- **Potential bugs** - Catches common programming mistakes
- **Style enforcement** - Maintains consistent code formatting

### ktlint
[ktlint](https://pinterest.github.io/ktlint/) is a Kotlin code formatter that enforces the official Kotlin coding style. It automatically formats code to ensure consistency across the project.

### Usage

Run the linters to check code quality:
```bash
./gradlew detekt
```

To quickly format your code it is recommended to enter the following:
```shell
./gradlew detekt --auto-correct
```

The linters are configured to:
- Auto-correct formatting issues (ktlint)
- Generate multiple report formats (HTML, SARIF, Markdown)
- Integrate with your IDE for real-time feedback
- Work with CI/CD pipelines

Configuration files:
- Main config: `config/detekt/detekt.yml`
- Rules are based on IntelliJ IDEA style with custom project-specific settings

---

## API

All endpoints require authentication via a Clerk JWT (`Authorization: Bearer <token>`) and accept/return JSON.

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

## Development

### Reactive stack (WebFlux)

The service was migrated from Spring MVC to **Spring WebFlux** to eliminate blocking I/O on request handlers. Controllers are written as Kotlin `suspend` functions, which the framework bridges into Reactor `Mono`/`Flux` via `kotlinx-coroutines-reactor`. This keeps the runtime non-blocking end-to-end — from the Netty event loop through to the MongoDB Kotlin coroutine driver.

### Exception handling

A global [`@RestControllerAdvice`](src/main/kotlin/com/quri/management/errors/GlobalExceptionHandler.kt) intercepts all exceptions before they reach the client. The policy is simple:

- **Never expose internal details** — Clients always receive a generic message (`"An unexpected error occurred"`).
- **Log intelligently** — 4xx errors are logged at `WARN` without a stack trace; 5xx errors are logged at `ERROR` with the full stack trace for debugging.
- **Smithy-modeled exceptions** — `ValidationException`, `ResourceNotFoundException`, and `InternalFailureException` are mapped to `400`, `404`, and `500` respectively.

### Smithy model usage

Rather than using Smithy's Java server codegen (which targets blocking Netty handlers), this project consumes **Smithy client models** from [`QuriModels`](https://github.com/lizarazukevin/QuriModels) for request/response shapes. The controller structure (one class per operation, explicit input/output builders) is intentionally maintained so that if Smithy releases a non-blocking Kotlin server-stub codegen in the future, migration will be straightforward.

---

## Project Status

This service is under active development. Known limitations:

- **No test suite** — Tests are disabled in the build config and planned for a future milestone.
- **Input validation** — Edge cases and input validation are not fully covered yet.

### Roadmap

- [x] Migrate to async/reactive operations (WebFlux)
- [x] Add auth layer (Clerk JWT resource server)
- [x] Add global exception handling
- [ ] Add a proper test suite (unit + integration)
- [ ] Docker support
- [ ] OpenAPI/Swagger docs
- [ ] Monitoring and metrics

---

## Contributing

1. Fork the repo and create a feature branch: `git checkout -b feature/my-feature`
2. Commit your changes: `git commit -m "Add my feature"`
3. Push and open a pull request

Please follow existing code style and architecture conventions. Update docs alongside code changes.

> Sign your commits by creating SSH keys and adding them to your account: https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent
```shell
ssh-add ~/.ssh/id_ed25519
git config --global gpg.format ssh
git config --global user.signingkey ~/.ssh/id_ed25519.pub
git config --global commit.gpgsign true
```

> When adding new ssh to github, make sure the key type is _**signing**_

Add new ssh to allowlist to view signatures:
```shell
echo "your_email@example.com namespaces=\"git\" $(cat ~/.ssh/id_ed25519.pub)" >> ~/.ssh/allowed_signers
git config --global gpg.ssh.allowedSignersFile ~/.ssh/allowed_signers
```


---

## License

MIT — see [LICENSE](LICENSE) for details.
