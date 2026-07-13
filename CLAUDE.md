# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Dev mode (hot reload)
mvn quarkus:dev

# Compile
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=CfpServiceTest

# Run a single test method
mvn test -Dtest=CfpServiceTest#testCreateCfp

# Full build with integration tests
mvn clean install

# Native image build
mvn package -Pnative
```

## Architecture Overview

This is a **Quarkus 3.x + Java 25** application implementing **DDD with a hexagonal/clean architecture** for a Conference Call for Papers (CFP) system.

### Bounded Contexts

- **`cfp`** — the primary context: conference proposals, sessions, presenters, tracks, and formats
- **`communications`** — owns outbound messaging policy: turns published CFP events into durable `Communication` records and retryable email deliveries
- **`conference`** — nascent second context; cross-context access only via public `domain` APIs

### Durable acceptance-communications flow (CFP → Communications)

Accepting a session proposal emails the presenter via a durable, at-least-once pipeline (see `session-proposal-accepted-communications-spec.md`):

1. `CfpApplicationService.reviewSessionProposal(ACCEPTED)` calls `SessionProposal.accept()` and, **in the same transaction**, appends a `SessionProposalAcceptedEvent` (a `shared.domain.PublishedEvent`) to the `cfp_outbox_event` table. No CDI event is fired here.
2. `CfpOutboxScheduler` (Quarkus `@Scheduled`, `SKIP`) drains due rows via `CfpOutbox*ApplicationService`. Each row publishes in a `REQUIRES_NEW` transaction that fires a **synchronous** typed `Event<SessionProposalAcceptedEvent>` and marks the row `PUBLISHED`.
3. `communications` `SessionProposalEventListener` observes it synchronously (`@Observes`, no tx phase) and `CommunicationsApplicationService.recordAcceptedProposal(...)` (default `@Transactional`, so it **joins** the publisher's transaction) idempotently creates one `Communication` + `EMAIL` delivery, keyed by `source_event_id` (unique constraint). The insert and the outbox `PUBLISHED` update commit together; an observer failure rolls the publication back.
4. `CommunicationDeliveryScheduler` claims pending deliveries (`REQUIRES_NEW`), sends via the `EmailSender` port / `QuarkusEmailSender` (Quarkus Mailer) **outside any transaction**, then records `DELIVERED` / `RETRY_SCHEDULED` / `PERMANENTLY_FAILED` in separate `REQUIRES_NEW` transactions.

Semantics: **at-least-once** email — a crash after SMTP accepts but before the DB records success may re-send. `REQUIRES_NEW` state methods live on a separate bean from the non-transactional batch orchestrators (no self-invocation). Communications may depend on `cfp` **only** through `cfp.domain.events`.

### Layer Structure (within each bounded context)

```
domain/           # Pure domain — no Jakarta EE, no ORM, no REST
  aggregates/     # Aggregate roots with factory methods (Cfp, ConferenceSession, Presenter)
  (root)          # Value objects (records), enums, domain services (*DomainService)

application/      # Use-case orchestration (*ApplicationService)
  commands/       # Immutable command records with validation in compact constructors
  dto/            # Outbound data transfer objects
  mappers/        # Domain → DTO conversions

persistence/      # Panache repository implementations + JPA entities
  (entities)      # JPA entities — separate from domain aggregates
  (repositories)  # PanacheRepository<Entity> with toDomain()/toEntity() converters

infrastructure/   # JAX-RS resources + request parameter classes
```

### Key DDD Patterns

- **Aggregates** use static factory methods (`Cfp.create()`, `ConferenceSession.create()`); UUIDs are generated inside the aggregate. No public setters.
- **Value objects** are immutable records with validation in their compact constructors (`EmailAddress`, `ProgrammingLanguage`, `ConferenceSessionFormat`, `ConferenceTrack`).
- **Commands** are immutable records (`CreateConferenceSessionCommand`) validated at construction.
- **`SubmissionContext`** is a record passed into `ConferenceSession.create()` to provide CFP dates and existing sessions; creation fails if the CFP is not currently open.
- **Repositories** own all mapping between domain aggregates and JPA entities — domain layer never touches persistence annotations.

### Service Naming Convention (ArchUnit enforced)

| Suffix | Layer | Rule |
|---|---|---|
| `*ApplicationService` | `application` package | Orchestrates use cases; may inject repositories |
| `*DomainService` | `domain` package | Stateless domain logic; must NOT inject repositories or use CDI events/ORM |
| `*Service` (bare) | — | **Forbidden** — indicates layer confusion |

### ArchUnit Tests

`src/test/java/.../cfp/architecture/` contains 8 architecture rule tests that are run with the normal test suite:

- **`DomainPurityArchTest`** — domain classes must not import `jakarta.persistence`, `jakarta.ws.rs`, or Firestore
- **`AggregateArchTest`** — aggregates may not have public fields or setters
- **`ValueObjectArchTest`** — value objects must have no setters; all fields must be final
- **`ServiceArchTest`** — enforces `*ApplicationService`/`*DomainService` naming and placement
- **`BoundedContextArchTest`** — cross-context access only through public `domain.services` or `domain.events`

Breaking any of these is a build failure — check them first when refactoring.

### Persistence Notes

- Database: **PostgreSQL** via Hibernate ORM + Panache
- Seed data is in `src/main/resources/import.sql` (one default CFP, 5 formats, 8 tracks)
- `Duration` values are stored as nanoseconds (Java `Duration` serialization)
- Enum columns use `STRING` strategy (`@Enumerated(EnumType.STRING)`)
