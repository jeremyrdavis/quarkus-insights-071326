# ArchUnit Rules

This project uses [ArchUnit](https://www.archunit.org/) to enforce its DDD /
hexagonal architecture as part of the normal test suite. Breaking any of these
rules is a **build failure**, so they are the first thing to check when
refactoring.

The rules live in two packages:

- `src/test/java/.../cfp/architecture/` — the general architecture rules (applied
  across the whole `io.arrogantprogrammer` base package)
- `src/test/java/.../communications/architecture/` — rules specific to the
  `communications` bounded context

All tests analyse production classes only (`ImportOption.DoNotIncludeTests`), and
most reference the configured `base-package` property rather than hard-coding it.

A few rules are **frozen** (`FreezingArchRule`): the current set of violations is
baselined so existing coupling is tolerated, but any *new* violation fails the
build.

---

## 1. Domain purity — `DomainPurityArchTest`

Keeps the domain layer free of technical concerns so the business model stays
pure and portable.

| Rule | Plain-language meaning |
|---|---|
| `domain_model_does_not_depend_on_infrastructure_or_persistence` | Aggregates, value objects, and events may not depend on anything in `infrastructure` or `persistence`. |
| `domain_is_free_of_web_and_orm_frameworks` | Nothing in `domain` may import ORM (`jakarta.persistence`), JAX-RS (`jakarta.ws.rs`), or Firestore types. |
| `domain_does_not_depend_on_infrastructure` | No `domain` class may reach into `infrastructure` (DTOs, dispatchers, adapters). |
| `domain_does_not_depend_on_persistence` | No `domain` class may reach into the `persistence` layer. |

**Why it matters:** the domain expresses business rules; it must not know how
data is stored or exposed over the wire.

---

## 2. Aggregate encapsulation — `AggregateArchTest`

Aggregate roots (in `..domain.aggregates..`) expose behaviour through named
methods, never through raw mutable state.

| Rule | Plain-language meaning |
|---|---|
| `aggregates_have_no_public_instance_fields` | Aggregate state is private; it's changed via domain methods, not public fields. |
| `aggregates_have_no_setters` | No `setX(...)` methods — aggregates enforce invariants through intention-revealing methods. |
| `aggregates_do_not_return_dtos` | Aggregate methods must not return application-layer or `*DTO` types; they return domain objects or primitives. |

---

## 3. Value-object immutability — `ValueObjectArchTest`

Value objects (records and enums in `..domain.valueobjects..`) are immutable.

| Rule | Plain-language meaning |
|---|---|
| `value_objects_have_no_setters` | No `setX(...)` methods. |
| `value_object_fields_are_final` | All non-static instance fields are `final`; "modifying" a value returns a new instance. |

---

## 4. Domain events — `DomainEventArchTest`

Ensures domain events are consistently marked and located.

| Rule | Plain-language meaning |
|---|---|
| `domain_events_reside_in_events_packages` | Any class implementing the `shared.domain.DomainEvent` marker must live in a `..domain.events..` package. |
| `event_types_in_events_packages_are_domain_events` | Conversely, any `*Event` class already in a `domain/events` package must implement the `DomainEvent` marker. |

**Note:** a `*Event` that is really a value object (e.g. describing a fixture)
belongs under `valueobjects`, not `events`.

---

## 5. Service layering — `ServiceArchTest`

Every service must declare which layer owns its logic in its name. A bare
`*Service` is treated as a layer-confusion smell.

| Rule | Plain-language meaning |
|---|---|
| `services_declare_their_layer_in_their_name` | A class ending in `Service` must end in `ApplicationService` or `DomainService`. |
| `application_services_live_in_application` | `*ApplicationService` classes reside in `application` (or the `admin` aggregator). |
| `domain_services_live_in_domain` | `*DomainService` classes reside in `domain`. |
| `domain_services_do_not_depend_on_repositories` | Domain services must not inject/depend on `*Repository` types. |
| `domain_services_do_not_use_cdi_events_or_orm` | Domain services must not use CDI events (`jakarta.enterprise.event`) or ORM (`jakarta.persistence`). |

**Rule of thumb:** `*ApplicationService` orchestrates use cases and may inject
repositories; `*DomainService` is stateless domain logic with no infrastructure.

---

## 6. Persistence technology — `PersistenceTechnologyArchTest`

The app persists exclusively via JPA/Hibernate/Panache, and persistence code
stays in the persistence layer.

| Rule | Plain-language meaning |
|---|---|
| `jpa_annotated_classes_must_reside_in_persistence_package` | Classes annotated `@Entity`, `@MappedSuperclass`, or `@Embeddable` must live in `..persistence..`. |
| `jpa_annotated_classes_only_allow_public_accessors` | Methods on those JPA-annotated classes must be public (accessed only via public accessors). |

---

## 7. Naming & placement conventions — `NamingConventionArchTest`

Locks in the codebase's deliberate, consistent naming style.

| Rule | Plain-language meaning |
|---|---|
| `jaxrs_resources_are_endpoints_in_infrastructure` | Non-interface classes with `@Path` must be named `*Resource` and live in `infrastructure` (or `admin`). |
| `dtos_live_in_infrastructure` | Classes ending in `DTO` live in the `application` layer (or `admin`). |
| `exception_mappers_are_providers_in_infrastructure` | `*ExceptionMapper` classes must be JAX-RS `@Provider`s in `infrastructure`. |
| `repository_interfaces_live_in_persistence_or_domain` | `*Repository` interfaces live in `persistence` or `domain/repositories`. |

**Note:** MicroProfile `@RegisterRestClient` interfaces also carry `@Path` but
are outbound clients, so the endpoint rule only applies to non-interfaces.

---

## 8. Bounded-context isolation (general) — `BoundedContextArchTest`

Keeps bounded contexts loosely coupled. Both rules are **frozen** (current
coupling is baselined; new coupling fails).

| Rule | Plain-language meaning |
|---|---|
| `contexts_are_free_of_cycles` | Top-level contexts must not form dependency cycles. |
| `contexts_reach_others_only_through_their_api` | A context may depend on another context only through its **public surface**: the ports/commands/exceptions in `<bc>.domain.services` or the published events in `<bc>.domain.events`. |

Reaching into another context's `application`, `domain.aggregates`,
`domain.valueobjects`, `domain.repositories`, `infrastructure`, or `persistence`
is forbidden.

**Exemptions:** cross-cutting modules (`shared`, `security`, `admin`,
`discovery`) are not treated as bounded contexts — `shared` holds the
`DomainEvent` marker and dispatcher used everywhere, and `admin` aggregates over
all contexts. Dependencies on another context's published `domain.events` are
also exempt from cycle detection, since observing another context's events is the
sanctioned subscribe direction.

---

## 9. Communications context isolation — `CommunicationsArchTest`

Enforces the specific integration contract between `communications` and `cfp`.

| Rule | Plain-language meaning |
|---|---|
| `communications_domain_is_free_of_frameworks` | The `communications.domain` must not depend on JPA, JAX-RS, CDI, Quarkus Mailer, or the scheduler. |
| `communications_reaches_cfp_only_through_published_events` | `communications` may depend on `cfp` **only** through `cfp.domain.events`. |
| `cfp_does_not_depend_on_communications` | The `cfp` context must never depend on `communications` (dependency flows one way). |

**Why it matters:** `cfp` publishes events; `communications` subscribes and turns
them into durable messages. The dependency arrow points only from
`communications` → `cfp.domain.events`, never the reverse.

---

## Running the rules

```bash
# Run the whole suite (includes all ArchUnit rules)
mvn test

# Run only the CFP architecture tests
mvn test -Dtest='io.arrogantprogrammer.quarkusinsights.cfp.architecture.*'

# Run a single rule set
mvn test -Dtest=DomainPurityArchTest
```
