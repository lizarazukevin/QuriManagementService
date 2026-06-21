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

### 2. Configure environment

Set `MONGO_USERNAME`, `MONGO_PASSWORD`, `MONGO_CLUSTER`, and `MONGO_APP_NAME` as environment variables.

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

[Detekt](https://detekt.dev/) and [ktlint](https://pinterest.github.io/ktlint/) enforce code style and catch potential bugs on every build.

```bash
./gradlew detekt            # check
./gradlew detekt --auto-correct  # auto-format
```

Configuration: `config/detekt/detekt.yml`

---

## API

All endpoints require authentication via a Clerk JWT (`Authorization: Bearer <token>`) and accept/return JSON.

### Profiles

| Method | Path             | Description |
|---|------------------|---|
| `POST` | `/profiles`      | Create a profile |
| `PATCH` | `/profiles/{id}` | Partially update a profile | 
| `GET` | `/profiles/{id}` | Get a profile |
| `GET` | `/profiles`      | List all profiles |
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

| Method   | Path          | Description             |
|----------|---------------|-------------------------|
| `POST`   | `/bills`      | Create a bill           |
| `PATCH`  | `/bills/{id}` | Partially update a bill |
| `GET`    | `/bills/{id}` | Get a bill              |
| `GET`    | `/bills`      | List all bills          |
| `DELETE` | `/bills/{id}` | Delete a bill           |

**Create bill request body:**
```json
{
  "total": { "amount": "100.00", "currencyCode": "USD" },
  "balance": { "amount": "50.00", "currencyCode": "USD" }
}
```

### Receipts

| Method | Path             | Description |
|---|------------------|---|
| `POST` | `/receipts`      | Create a receipt |
| `PUT` | `/receipts/{id}` | Update a receipt |
| `GET` | `/receipts`      | List all receipts (supports `maxResults` and `nextToken` pagination) |
| `GET` | `/receipts/{id}` | Get a receipt |
| `DELETE` | `/receipts/{id}` | Delete a receipt |

**Create receipt request body:**
```json
{
  "vendorName": "Trader Joe's",
  "items": [
    {
      "name": "Organic Milk",
      "quantity": 1,
      "unitPrice": { "amount": "5.99", "currencyCode": "USD" },
      "totalPrice": { "amount": "5.99", "currencyCode": "USD" }
    }
  ],
  "occurredAt": "2026-05-08T16:00:00Z",
  "paymentMethod": "CREDIT",
  "subtotal": { "amount": "45.67", "currencyCode": "USD" },
  "tax": 0.08,
  "tip": 0.15,
  "totalSavings": { "amount": "3.50", "currencyCode": "USD" },
  "fees": [
    { "name": "Service fee", "amount": { "amount": "2.00", "currencyCode": "USD" } }
  ],
  "address": {
    "street": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "zip": "94105"
  },
  "urls": ["https://receipts.example.com/photo.jpg"]
}
```

---

## Architecture

### Request Flow & Smithy Relationship

This service follows a strict 3-layer boundary pattern when working with Smithy models:

| Layer | Location | Responsibility |
|---|---|---|
| **HTTP Layer** | `/api` | All handler, input request, output response and error handling lives here. This is the only layer that sees JSON and HTTP concerns. |
| **Service Layer** | `/services` | Pure business logic that only operates on Smithy model types. No JSON, no HTTP, no framework concerns. |
| **Database Layer** | `/db` | MongoDB persistence, directly storing and retrieving Smithy model types via custom codecs. |

Every request follows this exact flow:

```
HTTP Request → Jackson (Smithy configured) → Smithy Model → Service → Smithy Model → Jackson → HTTP Response
```

When adding a new Smithy model, wire it through serialization (`SmithyJacksonConfig.kt`), codecs (`/db/mongo/codecs/`), and the service layer as needed.

### Smithy Serialization

Jackson is fully configured with custom mixins and deserializers to serialize/deserialize Smithy models directly from HTTP requests/responses — no intermediate DTO layer. See `SmithyJacksonConfig.kt`.

> SpringBoot 4+ ships with Jackson 3, leveraging [`JsonMapperBuilderCustomizer`](https://spring.io/blog/2025/10/07/introducing-jackson-3-support-in-spring)

---

## Development

### Reactive stack (WebFlux)

The service uses Spring WebFlux with Kotlin `suspend` functions bridged into Reactor via `kotlinx-coroutines-reactor`. This keeps the runtime non-blocking end-to-end — from the Netty event loop through to the MongoDB Kotlin coroutine driver.

### Exception handling

A global [`@RestControllerAdvice`](src/main/kotlin/com/quri/management/errors/GlobalExceptionHandler.kt) intercepts all exceptions:

- **4xx** errors are logged at `WARN` without a stack trace
- **5xx** errors are logged at `ERROR` with the full stack trace
- Clients always receive a generic error message — internal details are never exposed
- Smithy-modeled exceptions (`ValidationException`, `ResourceNotFoundException`, `InternalFailureException`) map to `400`, `404`, and `500` respectively

### Smithy model usage

Rather than using Smithy's Java server codegen (which targets blocking Netty handlers), this project consumes **Smithy client models** from [`QuriModels`](https://github.com/lizarazukevin/QuriModels) for request/response shapes, database entities, and all business logic. The entire stack — Jackson serialization, MongoDB codecs, and the service layer — operates natively on Smithy types.

The controller structure (one class per operation, explicit input/output builders) is maintained so that if Smithy releases a non-blocking Kotlin server-stub codegen in the future, migration will be straightforward.

### MongoDB Smithy Codecs

Custom [MongoDB Codec](https://www.mongodb.com/docs/drivers/java/sync/current/data-formats/codecs/) implementations are provided for all embedded Smithy structured types (e.g. `MonetaryAmountCodec`). These are registered directly in the Mongo client registry, eliminating manual mapping code between database documents and domain models.

### Testing

All tests are written with [Kotest](https://kotest.io/), using `DescribeSpec` for a Kotlin-idiomatic, readable test structure with multiplatform support.

Two test sourcesets provide different levels of coverage:

| Sourceset | Base class | Scope | Annotation |
|---|---|---|---|
| `src/test/` | — | Unit tests (validators, services, documents, etc.) | — |
| `src/integration/` | `IntegrationTest` | Full Spring context + real MongoDB | `@SpringBootTest` |
| `src/integration/` | `HandlerTest` | Sliced WebFlux handler tests (controllers only) | `@WebFluxTest` |

- **Handler tests** use `@WebFluxTest` with `WebTestClient` — they mock security and only bring in web/serialization/error-handling beans, keeping them fast.
- **Integration tests** use `@SpringBootTest` and connect to a real MongoDB (via `application-integration.yml`), exercising the full stack end-to-end.

```bash
./gradlew test                    # unit tests only
./gradlew integration             # integration tests
```

---

## Project Status

This service is under active development. Known limitations:

- **Input validation** — Edge cases and input validation are not fully covered yet.
- **Local development only** - Personal domain for this project does not currently exist, all development work must be done locally, fetching cookies from the frontend.

### Roadmap

- [x] Migrate to async/reactive operations (WebFlux)
- [x] Add auth layer (Clerk JWT resource server)
- [x] Add global exception handling
- [x] Native Smithy Jackson serialization
- [x] MongoDB Smithy Codecs for direct object persistence
- [x] Add a proper test suite (unit + integration)
- [ ] Docker support
- [ ] OpenAPI/Swagger docs
- [ ] Monitoring and metrics

---

## Contributing

1. Fork the repo and create a feature branch: `git checkout -b feature/my-feature`
2. Commit your changes: `git commit -m "Add my feature"`
3. Push and open a pull request

Please follow existing code style and architecture conventions. Update docs alongside code changes.

> Sign your commits via SSH. See [GitHub docs](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent) for setup, and configure signing via:
> ```shell
> git config --global gpg.format ssh
> git config --global user.signingkey ~/.ssh/id_ed25519.pub
> git config --global commit.gpgsign true
> ```

---

## License

MIT — see [LICENSE](LICENSE) for details.
