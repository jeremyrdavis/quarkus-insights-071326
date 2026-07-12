# DDD Conventions

## Aggregates

Aggregates are the consistency boundary. Rules:

1. **Static factory methods only** — `Cfp.create(...)`, `ConferenceSession.create(...)`, `Presenter.create(...)`. Never call `new Aggregate()` directly from application code.
2. **UUIDs generated inside the aggregate** — `this.id = UUID.randomUUID()` in the factory method. Never assign from outside.
3. **No public setters** — state changes go through domain methods. (ArchUnit's `AggregateArchTest` enforces this.)
4. **No public fields** — all fields are `private` or `protected`. (Also ArchUnit-enforced.)

### `SubmissionContext`

`ConferenceSession.create()` (formerly `SessionProposal.create()`) requires a `SubmissionContext` record:

```java
public record SubmissionContext(
    LocalDate cfpOpenDate,
    LocalDate cfpCloseDate,
    List<ConferenceSessionFormat> formats,
    List<ConferenceTrack> tracks,
    List<SessionProposal> currentSessions
) {}
```

The factory validates that `today` falls within `[cfpOpenDate, cfpCloseDate]`. Creation throws if the CFP is closed.

---

## Value Objects

Value objects are `record` types. Rules:

1. **Immutable** — all fields are `final` (automatic for records). No setters.
2. **Validation in compact constructor** — throw `IllegalArgumentException` for invalid state.
3. **No identity** — equality is structural.

Examples: `EmailAddress`, `ProgrammingLanguage`, `ConferenceSessionFormat`, `ConferenceTrack`.

(ArchUnit's `ValueObjectArchTest` checks that no field has a setter and all fields are final.)

---

## Commands

Commands are `record` types in the `application` package. Rules:

1. **Immutable records** — constructed once, never mutated.
2. **Validated in compact constructor** — validate all fields; throw on invalid input.
3. **Non-empty collection check** — e.g. `programmingLanguagesUsed` must not be empty.

Example:
```java
public record CreateSessionProposalCommand(
    UUID cfpId,
    String title,
    ...
    List<ProgrammingLanguage> programmingLanguagesUsed
) {
    public CreateSessionProposalCommand {
        Objects.requireNonNull(cfpId, "cfpId is required");
        if (programmingLanguagesUsed == null || programmingLanguagesUsed.isEmpty()) {
            throw new IllegalArgumentException("At least one programming language is required");
        }
    }
}
```

---

## Service Naming Convention

| Suffix | Package | What it does |
|---|---|---|
| `*ApplicationService` | `application` | Orchestrates use cases; may inject repositories and emit CDI events |
| `*DomainService` | `domain` | Stateless domain logic; must not inject repositories or use ORM |
| `*Service` (bare) | — | **Forbidden** — indicates layer confusion; will fail `ServiceArchTest` |

`ServiceArchTest` (ArchUnit) fails the build if a class matching `*Service` does not end in `ApplicationService` or `DomainService`.

---

## Repository Pattern

Repositories live in `persistence` and own all mapping between domain aggregates and JPA entities.

- **`toDomain(Entity)`** — converts a JPA entity to a domain aggregate
- **`toEntity(Aggregate)`** — converts a domain aggregate to a JPA entity

Domain aggregates never have JPA annotations. JPA entities never appear in domain or application code.

### Cascade Delete

Use `getEntityManager().remove(entity)` — **not** `delete("id = ?1", id)`. Bulk JPQL deletes bypass Hibernate's cascade logic and will violate foreign key constraints when child records (`formats`, `tracks`) exist.

```java
@Transactional
public boolean deleteCfp(UUID cfpId) {
    CfpEntity entity = find("id", cfpId).<CfpEntity>firstResult();
    if (entity == null) return false;
    getEntityManager().remove(entity);
    return true;
}
```

---

## ArchUnit Test Inventory

All tests run as part of the normal `mvn test` suite in `src/test/java/.../cfp/architecture/`.

| Test class | What it checks |
|---|---|
| `DomainPurityArchTest` | Domain classes must not import `jakarta.persistence`, `jakarta.ws.rs`, or Firestore |
| `AggregateArchTest` | Aggregates have no public fields or setters |
| `ValueObjectArchTest` | Value objects have no setters; all fields are final |
| `ServiceArchTest` | `*Service` classes must end in `ApplicationService` or `DomainService` and live in the correct package |
| `BoundedContextArchTest` | Cross-context (`conference` → `cfp`) access only through public `domain` APIs |

Breaking any of these is a build failure — check them before refactoring names or moving classes.
