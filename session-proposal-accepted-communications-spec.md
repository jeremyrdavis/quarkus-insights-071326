# Implementation Specification: Durable Session Proposal Acceptance Communications

## 1. Objective

Implement an end-to-end, durable event flow in the existing Quarkus modular monolith:

1. The CFP subdomain accepts a session proposal.
2. The CFP aggregate state change and a `SessionProposalAcceptedEvent` outbox record are committed atomically.
3. A scheduled CFP outbox publisher reads the durable event and fires it as a synchronous CDI event.
4. The Communications subdomain observes the event and atomically records a Communications-owned `Communication` with a pending email delivery.
5. A separate Communications scheduler claims pending email deliveries and sends them through a Quarkus Mailer adapter.
6. Delivery success or failure is recorded for retry and observability.

The implementation must preserve the repository’s DDD and hexagonal architecture conventions: pure domain objects, JPA entities separate from aggregates, repositories responsible for mapping, application services responsible for orchestration, and infrastructure adapters responsible for CDI, scheduling, and email.

---

## 2. Architectural decisions

### 2.1 Subdomain ownership

The CFP subdomain owns the fact:

> A session proposal was accepted.

The Communications subdomain owns the policy:

> An accepted proposal warrants an email to the presenter.

The Communications subdomain also owns message composition, channel selection, delivery state, retries, and future channel expansion.

### 2.2 Internal versus published events

Keep the distinction explicit in the design, but do not create two separate event classes in this iteration.

- `SessionProposal.accept()` is the domain behavior.
- `SessionProposalAcceptedEvent` is the published event contract created by the CFP application service after the aggregate accepts the proposal.
- The published event is persisted to the CFP outbox in the same transaction as the aggregate update.
- A future iteration may introduce a separate internal domain event and mapper without changing the external contract.

### 2.3 Delivery guarantees

The implementation provides:

- Atomic persistence of CFP state and the CFP outbox event.
- At-least-once publication from the CFP outbox.
- Idempotent receipt by Communications.
- Durable, retryable email delivery.
- At-least-once external email sending.

The implementation does **not** claim exactly-once email delivery. A crash after SMTP accepts a message but before the database records success may cause a duplicate email.

### 2.4 CDI behavior

The CFP outbox publisher must use a normal synchronous CDI event:

```java
event.fire(sessionProposalAcceptedEvent);
```

The Communications listener must use:

```java
void onSessionProposalAccepted(
        @Observes SessionProposalAcceptedEvent event)
```

Do not use `fireAsync()`, `@ObservesAsync`, or `TransactionPhase.AFTER_SUCCESS` for the outbox-to-Communications handoff.

The CFP outbox row is already durable before the scheduled publisher runs. Synchronous observation is required so the Communications insert and CFP outbox `PUBLISHED` update participate in the same dispatcher transaction.

### 2.5 Scheduler scope

Use the lightweight Quarkus scheduler and `concurrentExecution = SKIP`.

This implementation targets a single running application instance. Cluster-safe scheduling, distributed locking, and multiple application replicas are out of scope.

---

## 3. Scope

### In scope

- Replace the accepted status terminology with `ACCEPTED`.
- Add explicit aggregate behavior for accepting a session proposal.
- Add `SessionProposalAcceptedEvent`.
- Add a CFP outbox table and repository.
- Persist the accepted event in the same transaction as the proposal update.
- Add a scheduled CFP outbox publisher.
- Add a Communications subdomain.
- Observe accepted events.
- Translate accepted events into Communications-owned records.
- Add a pending email delivery.
- Add a scheduled email-delivery worker.
- Add a Quarkus Mailer adapter.
- Add retries and failure state.
- Add unit, persistence, application, architecture, and end-to-end tests.

### Out of scope

- `SessionProposalDeclinedEvent`.
- `SessionProposalWaitlistedEvent`.
- SMS delivery.
- Social-media delivery.
- HTML or Qute templates.
- Kafka, AMQP, or other brokers.
- Quartz or clustered scheduling.
- Administrative REST endpoints for inspecting or replaying queues.
- Exactly-once SMTP delivery.
- A generic event framework supporting arbitrary event classes.

The code should leave clear extension points for these features without implementing them now.

---

## 4. Existing-code changes

### 4.1 Use consistent accepted terminology

Change:

```java
SessionProposalStatus.APPROVED
```

to:

```java
SessionProposalStatus.ACCEPTED
```

Update all Java code, tests, JSON fixtures, frontend status options, labels, and assertions.

No production data migration is required for this demo repository. Do not retain both enum constants.

### 4.2 Remove direct CDI publication from `CfpApplicationService`

Remove:

- The raw `Event reviewedEvent` injection.
- Direct `reviewedEvent.fire(event)` calls.
- `SessionProposalStatusChangedEvent` from the accepted flow.

Delete `SessionProposalStatusChangedEvent` after all references are removed, unless another existing feature still requires it. The final accepted flow must not publish that generic event.

### 4.3 Ensure proposals retain the presenter

The current proposal-creation flow receives a presenter email but does not consistently attach the `Presenter` aggregate to the new `SessionProposal`.

Correct the flow before implementing acceptance events:

1. Resolve the presenter using `CreateSessionProposalCommand.presenterEmail()`.
2. Convert the persistence entity to the domain `Presenter` through the repository.
3. Pass the resolved `Presenter` to `SessionProposal.create(...)`.
4. Fail with a clear not-found or validation exception when the presenter does not exist.
5. Persist the relationship so rehydrated proposals contain the presenter.

An accepted proposal must always have:

- Presenter ID.
- Presenter first name.
- Presenter last name.
- Presenter email.

Do not allow `SessionProposalAcceptedEvent.presenterEmail` to be null.

---

## 5. Published-event contract

### 5.1 Shared marker interface

Add:

```text
src/main/java/io/arrogantprogrammer/quarkusinsights/shared/domain/PublishedEvent.java
```

Suggested contract:

```java
public interface PublishedEvent extends DomainEvent {

    UUID eventId();

    Instant occurredAt();

    int eventVersion();
}
```

Do not add Jakarta, Jackson, JPA, or Quarkus annotations to this interface.

### 5.2 Event class

Add:

```text
src/main/java/io/arrogantprogrammer/quarkusinsights/cfp/domain/events/SessionProposalAcceptedEvent.java
```

Use a Java record:

```java
public record SessionProposalAcceptedEvent(
        UUID eventId,
        Instant occurredAt,
        int eventVersion,
        UUID proposalId,
        UUID cfpId,
        String proposalTitle,
        UUID presenterId,
        String presenterFirstName,
        String presenterLastName,
        String presenterEmail
) implements PublishedEvent {

    public static final String EVENT_TYPE =
            "cfp.session-proposal.accepted";

    public static final int CURRENT_VERSION = 1;
}
```

Requirements:

- Validate every component in the compact constructor.
- Strings must not be null or blank.
- `eventVersion` must be positive.
- Use `String` for the email address in the published contract rather than the CFP `EmailAddress` value object.
- `EVENT_TYPE` must be stable and must not be derived from the Java class name.
- Do not include JPA entities or aggregate references.
- The event must contain all information Communications needs to create the email without querying the CFP subdomain.

---

## 6. CFP aggregate behavior

Modify:

```text
cfp/domain/aggregates/SessionProposal.java
```

Add explicit behavior:

```java
public void accept()
```

Requirements:

- Reject an already accepted proposal with an `IllegalStateException` or the repository’s preferred domain exception.
- Set the status to `SessionProposalStatus.ACCEPTED`.
- Do not fire CDI events.
- Do not persist anything.
- Do not construct an outbox entity.
- Do not import Jakarta or Quarkus APIs.

Also add explicit methods for the existing status operations so the generic mutation can be removed:

```java
public void decline()
public void waitlist()
```

For this iteration:

- `accept()` results in a published event through the application service.
- `decline()` and `waitlist()` only change state.
- Declined and waitlisted published events are future work.

Remove or make obsolete:

```java
review(SessionProposalStatus newStatus)
```

The aggregate must not expose a generic status setter.

---

## 7. CFP application flow

Modify the review use case in `CfpApplicationService`, or extract a focused `SessionProposalReviewApplicationService` if that improves clarity while respecting the repository’s naming rules.

The accepted branch must execute in one `@Transactional` method:

```text
Load SessionProposal
    -> proposal.accept()
    -> save SessionProposal
    -> construct SessionProposalAcceptedEvent
    -> append serialized event to CFP outbox
    -> commit
```

Suggested structure:

```java
@Transactional
public SessionProposalDTO reviewSessionProposal(
        ChangeSessionProposalStatusCommand command) {

    SessionProposal proposal = loadProposal(command.proposalId());

    switch (command.newStatus()) {
        case ACCEPTED -> accept(proposal);
        case DECLINED -> proposal.decline();
        case WAITLISTED -> proposal.waitlist();
        case SUBMITTED -> throw new IllegalArgumentException(...);
    }

    sessionProposalRepository.save(proposal);
    return SessionProposalMapper.toDTO(proposal);
}
```

The accepted helper must:

1. Invoke `proposal.accept()`.
2. Create a new event ID using `UUID.randomUUID()`.
3. Capture `Instant.now()` once.
4. Build `SessionProposalAcceptedEvent` from the accepted aggregate snapshot.
5. Append the event to the CFP outbox.

The event and proposal update must commit or roll back together.

Do not call CDI from this transaction.

---

## 8. CFP outbox persistence

### 8.1 Package

Create:

```text
cfp/persistence/outbox/
```

Suggested files:

```text
CfpOutboxEventEntity.java
CfpOutboxEventRepository.java
CfpOutboxStatus.java
```

### 8.2 Table

Use table:

```text
cfp_outbox_event
```

Required columns:

| Column | Type | Requirements |
|---|---|---|
| `event_id` | UUID | Primary key |
| `event_type` | VARCHAR | Not null |
| `event_version` | INTEGER | Not null |
| `aggregate_id` | UUID | Proposal ID; not null |
| `occurred_at` | TIMESTAMP WITH TIME ZONE | Not null |
| `payload` | TEXT | JSON; not null |
| `status` | VARCHAR | `PENDING`, `PUBLISHED`, or `FAILED`; not null |
| `attempt_count` | INTEGER | Default 0; not null |
| `next_attempt_at` | TIMESTAMP WITH TIME ZONE | Not null |
| `published_at` | TIMESTAMP WITH TIME ZONE | Nullable |
| `last_error` | TEXT | Nullable |

Add indexes for:

- `(status, next_attempt_at)`
- `aggregate_id`

### 8.3 Entity behavior

`CfpOutboxEventEntity` may contain persistence-oriented methods:

```java
void markPublished(Instant publishedAt)
void recordFailure(String error, Instant nextAttemptAt, int maxAttempts)
boolean isPublishableAt(Instant now)
```

Do not put this JPA entity in the domain package.

### 8.4 Serialization

Use the Quarkus-managed Jackson `ObjectMapper`.

When appending `SessionProposalAcceptedEvent`:

- `event_type` = `SessionProposalAcceptedEvent.EVENT_TYPE`
- `event_version` = `SessionProposalAcceptedEvent.CURRENT_VERSION`
- `aggregate_id` = event proposal ID
- `payload` = serialized event JSON
- `status` = `PENDING`
- `attempt_count` = `0`
- `next_attempt_at` = `occurredAt`

Do not store a Java fully qualified class name.

### 8.5 Repository operations

Provide operations equivalent to:

```java
void append(SessionProposalAcceptedEvent event)

List<UUID> findDueEventIds(
        Instant now,
        int batchSize)

CfpOutboxEventEntity findRequired(UUID eventId)

void markPublished(UUID eventId, Instant publishedAt)

void recordFailure(
        UUID eventId,
        String error,
        Instant nextAttemptAt,
        int maxAttempts)
```

Repository queries must return the oldest due events first.

---

## 9. CFP outbox publisher

### 9.1 Scheduler

Create:

```text
cfp/infrastructure/scheduling/CfpOutboxScheduler.java
```

The scheduler must be a thin adapter:

```java
@Scheduled(
    every = "${cfp.outbox.publisher.every:1s}",
    concurrentExecution = SKIP
)
void publishPendingEvents()
```

Responsibilities:

- Ask the application service for due event IDs.
- Process at most the configured batch size.
- Do not contain JSON mapping, CDI event construction, domain policy, or retry calculations.

### 9.2 Application services

Use application services to preserve transaction boundaries. Suggested classes:

```text
cfp/application/outbox/CfpOutboxBatchApplicationService.java
cfp/application/outbox/CfpOutboxPublishApplicationService.java
cfp/application/outbox/CfpOutboxFailureApplicationService.java
```

`CfpOutboxBatchApplicationService`:

1. Loads due event IDs.
2. Calls `CfpOutboxPublishApplicationService.publish(eventId)` for each.
3. Catches failures per event.
4. Calls `CfpOutboxFailureApplicationService.recordFailure(...)`.
5. Continues processing the remaining batch.

`CfpOutboxPublishApplicationService.publish(...)` must use:

```java
@Transactional(Transactional.TxType.REQUIRES_NEW)
```

Within that transaction:

1. Reload the outbox row.
2. Verify it is still due and `PENDING`.
3. Deserialize based on `event_type` and `event_version`.
4. Fire a synchronous typed CDI event.
5. Mark the outbox row `PUBLISHED`.
6. Set `published_at`.
7. Commit.

For the initial implementation, support only:

```text
cfp.session-proposal.accepted / version 1
```

An unknown event type or unsupported version must fail explicitly and be recorded as an outbox failure.

### 9.3 Typed CDI publication

Inject:

```java
Event<SessionProposalAcceptedEvent>
```

Do not use a raw `Event`.

The event publication must remain synchronous. If the Communications observer throws, the exception must propagate so the dispatcher transaction rolls back.

### 9.4 Failure recording

Because the publication transaction rolls back on observer failure, failure metadata must be recorded in a separate `REQUIRES_NEW` transaction.

On failure:

- Increment `attempt_count`.
- Save a sanitized error message in `last_error`.
- Set `next_attempt_at` using a fixed or exponential delay.
- Set status to `FAILED` when `attempt_count` reaches the configured maximum.
- Otherwise retain `PENDING`.

Do not store full stack traces in the database.

---

## 10. Communications subdomain

Create top-level package:

```text
io.arrogantprogrammer.quarkusinsights.communications
```

Suggested structure:

```text
communications/
    application/
    domain/
        aggregates/
        valueobjects/
    infrastructure/
        email/
        events/
        scheduling/
    persistence/
```

Add Communications to `CLAUDE.md` as a bounded context/subdomain.

### 10.1 Domain concepts

Implement:

```text
Communication
CommunicationDelivery
CommunicationType
CommunicationChannel
CommunicationStatus or DeliveryStatus
```

Required enums:

```java
CommunicationType.SESSION_PROPOSAL_ACCEPTED
CommunicationChannel.EMAIL
DeliveryStatus.PENDING
DeliveryStatus.PROCESSING
DeliveryStatus.DELIVERED
DeliveryStatus.RETRY_SCHEDULED
DeliveryStatus.PERMANENTLY_FAILED
```

### 10.2 Communication aggregate

Suggested aggregate fields:

```text
Communication
    id
    sourceEventId
    type
    createdAt
    deliveries
```

Suggested factory:

```java
public static Communication forAcceptedSessionProposal(
        UUID sourceEventId,
        String presenterFirstName,
        String presenterEmail,
        String proposalTitle,
        Instant createdAt)
```

The factory must apply Communications policy:

- Create one `EMAIL` delivery.
- Use the presenter email as the destination.
- Compose the subject and body.
- Set the delivery status to `PENDING`.
- Set `nextAttemptAt` to `createdAt`.

Initial message:

Subject:

```text
Your session proposal was accepted
```

Plain-text body:

```text
Hi {presenterFirstName},

Your session proposal "{proposalTitle}" has been accepted.

Thank you for submitting.
```

The Communications domain must not depend on CFP aggregates, value objects, repositories, application services, persistence entities, or infrastructure classes.

The only CFP type imported by Communications should be the published `SessionProposalAcceptedEvent` in the inbound listener.

### 10.3 Delivery behavior

The aggregate or delivery entity must support behavior equivalent to:

```java
EmailMessage claimForDelivery(Instant now)
void markDelivered(Instant deliveredAt)
void scheduleRetry(
        String error,
        Instant nextAttemptAt,
        int maxAttempts)
```

Reject invalid transitions, including:

- Claiming an already delivered delivery.
- Marking a delivery delivered when it was not processing.
- Retrying a permanently failed delivery without an explicit future requeue operation.

---

## 11. Communications persistence

### 11.1 Tables

Use two tables.

#### `communication`

| Column | Type | Requirements |
|---|---|---|
| `id` | UUID | Primary key |
| `source_event_id` | UUID | Not null; unique |
| `communication_type` | VARCHAR | Not null |
| `created_at` | TIMESTAMP WITH TIME ZONE | Not null |

#### `communication_delivery`

| Column | Type | Requirements |
|---|---|---|
| `id` | UUID | Primary key |
| `communication_id` | UUID | Foreign key; not null |
| `channel` | VARCHAR | Not null |
| `destination` | VARCHAR | Not null |
| `subject` | VARCHAR | Not null |
| `body` | TEXT | Not null |
| `status` | VARCHAR | Not null |
| `attempt_count` | INTEGER | Default 0; not null |
| `next_attempt_at` | TIMESTAMP WITH TIME ZONE | Not null |
| `processing_started_at` | TIMESTAMP WITH TIME ZONE | Nullable |
| `delivered_at` | TIMESTAMP WITH TIME ZONE | Nullable |
| `last_error` | TEXT | Nullable |

Indexes:

- Unique index on `communication.source_event_id`.
- Index on `communication_delivery(status, next_attempt_at)`.
- Index on `communication_delivery.communication_id`.

### 11.2 Mapping

Follow the repository convention already documented by the project:

- Domain classes contain no JPA annotations.
- Persistence entities contain JPA annotations.
- Repositories own `toDomain()` and `toEntity()` mapping.
- Application services do not manually construct persistence entities.

### 11.3 Idempotency

The Communications listener must be idempotent by `source_event_id`.

Required behavior:

- If no Communication exists for the source event, create it.
- If one already exists, treat the event as successfully consumed and do nothing.
- Enforce idempotency in both application logic and a database unique constraint.

A duplicate event must not create a second email delivery and must not cause the CFP outbox publication to fail.

---

## 12. Communications event listener

Create:

```text
communications/infrastructure/events/SessionProposalEventListener.java
```

Suggested shape:

```java
@ApplicationScoped
public class SessionProposalEventListener {

    private final CommunicationsApplicationService communications;

    public SessionProposalEventListener(
            CommunicationsApplicationService communications) {
        this.communications = communications;
    }

    void onSessionProposalAccepted(
            @Observes SessionProposalAcceptedEvent event) {
        communications.recordAcceptedProposal(event);
    }
}
```

Requirements:

- Use synchronous `@Observes`.
- Do not use `@ObservesAsync`.
- Do not specify a transaction phase.
- Keep the listener thin.
- Do not compose email text in the listener.
- Do not use Quarkus Mailer in the listener.
- Do not send email in the listener.
- Do not query CFP repositories.
- Call a Communications application service.

`CommunicationsApplicationService.recordAcceptedProposal(...)` must use default `@Transactional` behavior so it joins the existing CFP outbox dispatcher transaction.

If persistence fails, the exception must propagate to the CFP publisher.

---

## 13. Email-delivery port and adapter

### 13.1 Application port

Create:

```text
communications/application/ports/EmailSender.java
```

Suggested contract:

```java
public interface EmailSender {

    void send(EmailMessage message);
}
```

`EmailMessage` should be an immutable record owned by Communications and contain:

```text
deliveryId
to
subject
body
```

Do not expose Quarkus `Mail` outside the infrastructure adapter.

### 13.2 Quarkus adapter

Create:

```text
communications/infrastructure/email/QuarkusEmailSender.java
```

Inject the imperative Quarkus `Mailer`.

Implementation behavior:

```java
mailer.send(
    Mail.withText(
        message.to(),
        message.subject(),
        message.body()));
```

Do not catch and suppress mailer exceptions. Let the delivery application service record them.

---

## 14. Communications delivery scheduler

### 14.1 Scheduler

Create:

```text
communications/infrastructure/scheduling/CommunicationDeliveryScheduler.java
```

Use:

```java
@Scheduled(
    every = "${communications.delivery.every:1s}",
    concurrentExecution = SKIP
)
```

The scheduler must call a Communications application service and contain no persistence or email composition logic.

### 14.2 Transaction boundaries

Do not hold a database transaction open while calling SMTP.

Implement the batch flow using separate proxied application-service methods:

```text
Find due delivery IDs

For each delivery:
    REQUIRES_NEW: claim delivery and mark PROCESSING
    No transaction: call EmailSender
    REQUIRES_NEW: mark DELIVERED

On send failure:
    REQUIRES_NEW: mark RETRY_SCHEDULED or PERMANENTLY_FAILED
```

Suggested services:

```text
CommunicationDeliveryBatchApplicationService
CommunicationDeliveryStateApplicationService
```

`CommunicationDeliveryStateApplicationService` may expose:

```java
@Transactional(REQUIRES_NEW)
Optional<EmailMessage> claim(UUID deliveryId, Instant now)

@Transactional(REQUIRES_NEW)
void markDelivered(UUID deliveryId, Instant deliveredAt)

@Transactional(REQUIRES_NEW)
void markFailed(
        UUID deliveryId,
        String error,
        Instant nextAttemptAt,
        int maxAttempts)
```

The nontransactional batch service calls these methods through a separate CDI bean so transaction interceptors are applied. Do not use self-invocation for `REQUIRES_NEW` methods.

### 14.3 Stale processing recovery

A delivery left in `PROCESSING` because the process crashed must become eligible again after a configurable timeout.

A due-delivery query must include:

- `PENDING` where `next_attempt_at <= now`.
- `RETRY_SCHEDULED` where `next_attempt_at <= now`.
- `PROCESSING` where `processing_started_at` is older than the configured stale-processing timeout.

Reclaiming a stale `PROCESSING` delivery may produce a duplicate email. This is accepted as part of at-least-once delivery.

### 14.4 Retry behavior

Use configurable values:

- Maximum attempts: default `5`.
- Initial retry delay: default `5s`.
- Stale-processing timeout: default `5m`.
- Batch size: default `20`.

A fixed delay is acceptable for the demo. Exponential backoff is optional.

After the maximum attempts:

```text
DeliveryStatus.PERMANENTLY_FAILED
```

The failure must not change CFP state or the CFP outbox row, because Communications already durably accepted the event.

---

## 15. Maven dependencies

Add Quarkus extensions:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-scheduler</artifactId>
</dependency>

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-mailer</artifactId>
</dependency>
```

Do not add Quartz, Kafka, AMQP, Flyway, or a third-party retry library.

Use the existing Quarkus-managed Jackson dependency already available through REST Jackson.

---

## 16. Configuration

Add defaults to `application.properties`:

```properties
# CFP outbox
cfp.outbox.publisher.every=1s
cfp.outbox.publisher.batch-size=20
cfp.outbox.publisher.max-attempts=5
cfp.outbox.publisher.retry-delay=5s

# Communications delivery
communications.delivery.every=1s
communications.delivery.batch-size=20
communications.delivery.max-attempts=5
communications.delivery.retry-delay=5s
communications.delivery.stale-processing-timeout=5m

# Email
quarkus.mailer.from=no-reply@conference.example

# Deterministic tests call application services directly
%test.quarkus.scheduler.enabled=false
%test.quarkus.mailer.mock=true
```

Use MicroProfile Config mappings or `@ConfigProperty`; do not scatter hard-coded timing and batch values through the code.

Do not configure real SMTP credentials in the repository.

---

## 17. Logging and observability

Use structured, concise logs.

CFP publisher logs:

- Event ID.
- Event type.
- Proposal ID.
- Attempt count.
- Published or failed result.

Communications listener logs:

- Source event ID.
- Created versus duplicate result.
- Communication ID.

Delivery worker logs:

- Communication ID.
- Delivery ID.
- Channel.
- Attempt count.
- Delivered, retry scheduled, or permanently failed.

Do not log:

- Full email bodies.
- SMTP credentials.
- Full stack traces into database columns.

Application logs may include stack traces at error level.

---

## 18. Tests

Schedulers must be disabled in normal tests. Invoke the application services directly.

### 18.1 CFP domain tests

Add tests for:

1. `accept()` changes `SUBMITTED` to `ACCEPTED`.
2. Accepting an already accepted proposal fails.
3. `decline()` changes status to `DECLINED`.
4. `waitlist()` changes status to `WAITLISTED`.
5. No aggregate method imports or depends on CDI/JPA.

### 18.2 Published-event tests

Add tests for:

1. Required fields reject null.
2. String fields reject blank values.
3. Event version must be positive.
4. `EVENT_TYPE` is exactly `cfp.session-proposal.accepted`.
5. Jackson round-trip serialization preserves all fields.

### 18.3 CFP application/outbox tests

Add tests proving:

1. Accepting a proposal updates its status and inserts one `PENDING` outbox row.
2. The event payload contains the proposal and presenter snapshot.
3. Proposal update and outbox insert roll back together when outbox persistence fails.
4. Declining or waitlisting does not insert an accepted event.
5. Reprocessing an already `PUBLISHED` outbox row does nothing.
6. Unknown event type or version records a failure.
7. An observer exception leaves the outbox row unpublished.
8. Successful synchronous observation creates the Communications record and marks the CFP row `PUBLISHED` in one transaction.

### 18.4 Communications domain tests

Add tests proving:

1. Accepted proposal policy creates one email delivery.
2. Subject and body match the specified text.
3. New delivery status is `PENDING`.
4. Claim changes status to `PROCESSING`.
5. Success changes status to `DELIVERED`.
6. Failure schedules retry.
7. Maximum attempts changes status to `PERMANENTLY_FAILED`.
8. Invalid status transitions fail.

### 18.5 Listener/idempotency tests

Add tests proving:

1. First receipt creates one Communication and one delivery.
2. Duplicate receipt creates no additional rows.
3. The database unique constraint prevents duplicates under a race.
4. Listener persistence failure propagates to the CFP publisher.

### 18.6 Mailer test

Use Quarkus `MockMailbox`.

Test flow:

1. Persist a pending email delivery.
2. Invoke the delivery batch application service.
3. Assert exactly one email was sent.
4. Assert recipient, subject, and body.
5. Assert delivery status is `DELIVERED`.

### 18.7 Failure/retry test

Replace or mock `EmailSender` to throw.

Assert:

1. Delivery becomes `RETRY_SCHEDULED`.
2. Attempt count increments.
3. `next_attempt_at` is in the future.
4. A later successful invocation marks it `DELIVERED`.
5. CFP proposal status and CFP outbox state remain unchanged during email retries.

### 18.8 Architecture tests

Extend or add ArchUnit coverage so:

- Communications domain does not depend on JPA, JAX-RS, Quarkus Mailer, scheduler, or CDI.
- Communications can depend on CFP only through `cfp.domain.events`.
- CFP does not depend on Communications.
- Infrastructure may depend inward on application/domain.
- Application does not depend on infrastructure.
- Repository mapping remains in persistence.
- New application-service classes follow the `*ApplicationService` convention.
- No bare `*Service` classes are introduced.

---

## 19. End-to-end acceptance scenario

Given:

- A registered presenter.
- A submitted session proposal owned by that presenter.

When:

1. The proposal status is changed to `ACCEPTED`.
2. The CFP outbox batch service is invoked.
3. The Communications delivery batch service is invoked.

Then:

- The proposal status is `ACCEPTED`.
- Exactly one CFP outbox row exists for the acceptance.
- The CFP outbox row is `PUBLISHED`.
- Exactly one Communications record exists for the event ID.
- Exactly one email delivery exists.
- The email is present in `MockMailbox`.
- The email recipient is the presenter email.
- The subject is `Your session proposal was accepted`.
- The delivery status is `DELIVERED`.

When the same CFP event is published a second time:

- No second Communication is created.
- No second delivery is created.
- The duplicate is treated as successfully consumed.

---

## 20. Failure acceptance scenario

Given an accepted proposal and a pending CFP outbox row:

### Communications persistence failure

When the event listener cannot persist the Communication:

- `Event.fire()` fails.
- The dispatcher transaction rolls back.
- The CFP outbox row is not `PUBLISHED`.
- No partial Communications record remains.
- Failure metadata is recorded in a separate transaction.
- The event is eligible for retry.

### SMTP failure

When Communications has already stored the event but email sending fails:

- The CFP outbox row remains `PUBLISHED`.
- The proposal remains `ACCEPTED`.
- The Communications delivery becomes `RETRY_SCHEDULED`.
- The delivery can be retried independently.
- A later success changes only the delivery state to `DELIVERED`.

---

## 21. Implementation sequence

Implement in this order:

1. Rename `APPROVED` to `ACCEPTED` across backend, frontend, fixtures, and tests.
2. Fix presenter resolution and persistence in proposal creation.
3. Add explicit aggregate methods: `accept`, `decline`, and `waitlist`.
4. Add `PublishedEvent`.
5. Add `SessionProposalAcceptedEvent`.
6. Add CFP outbox entity, repository, status, and serialization.
7. Change CFP acceptance use case to update proposal and append outbox event atomically.
8. Add Communications domain model and persistence mapping.
9. Add idempotent `CommunicationsApplicationService`.
10. Add synchronous `SessionProposalEventListener`.
11. Add CFP outbox publisher and retry handling.
12. Add `EmailSender` and Quarkus Mailer adapter.
13. Add Communications delivery claim/send/result flow.
14. Add schedulers.
15. Add configuration.
16. Add and update architecture tests.
17. Add end-to-end and failure tests.
18. Run formatting, compilation, unit tests, integration tests, and ArchUnit tests.

---

## 22. Agent execution requirements

The coding agent must:

- Inspect the existing repository before creating files.
- Follow existing constructor-injection or field-injection conventions consistently; prefer constructor injection for new code.
- Preserve the current REST API unless the `APPROVED` to `ACCEPTED` JSON value requires a coordinated frontend update.
- Avoid unrelated refactoring.
- Avoid introducing Lombok.
- Avoid exposing JPA entities outside persistence.
- Avoid raw types.
- Avoid swallowing observer, persistence, serialization, or mailer exceptions.
- Avoid sleeping or polling inside tests.
- Disable schedulers and invoke services directly in tests.
- Run the entire test suite after implementation.
- Update `CLAUDE.md` with the Communications bounded context and the durable event flow.
- Add a concise README section explaining:
  - CFP outbox.
  - Synchronous CDI handoff.
  - Communications idempotency.
  - Separate email-delivery scheduler.
  - At-least-once semantics and possible duplicate email delivery.

---

## 23. Definition of done

The implementation is complete only when:

- The project compiles.
- All existing tests pass.
- All new tests pass.
- ArchUnit tests pass.
- No generic `SessionProposalStatusChangedEvent` is used for acceptance.
- No direct CDI event is fired from the CFP review transaction.
- Accepting a proposal atomically creates one CFP outbox record.
- Publishing the outbox event atomically creates one Communications record and marks the event published.
- Duplicate event receipt is idempotent.
- Email sending occurs only from the Communications delivery workflow.
- Email failure is independently retryable.
- Quarkus `MockMailbox` verifies the final email.
- The code and documentation explicitly state that external email delivery is at least once, not exactly once.
