# DDD Review Checklist

A checklist for reviewing changes for **Domain-Driven Design conformance**. It is
meant to be applied by `/code-review` (or a human reviewer) as an *advisory*
lens, complementing — not duplicating — the mechanical rules.

## How this relates to ArchUnit

The [ArchUnit rules](archunit-rules.md) are the **hard gate**: deterministic,
build-blocking, and cheap. They enforce *structure* — package placement, naming,
dependency direction, immutability. This checklist covers the **semantic**
concerns that a structural rule cannot express: whether the logic is in the right
place, whether the model actually carries behavior, and whether the code speaks
the domain's language.

| Layer | Question it answers | Enforcement |
|---|---|---|
| ArchUnit | "Is it in the right package, named right, pointing the right way?" | Build fails |
| This checklist | "Does it *model the domain* correctly?" | Advisory review finding |

**Rule of thumb for reviewers:** don't re-report anything ArchUnit already
catches (a setter on an aggregate, JPA in the domain, a bare `*Service`). Focus
on the judgment calls below. When in doubt, cite the specific convention in
[`ddd-conventions.md`](ddd-conventions.md).

---

## 1. Aggregates carry behavior (no anemic model)

- [ ] Business logic and invariants live **on the aggregate**, not in an
  `*ApplicationService` that reaches in and mutates data. If a service reads an
  aggregate's fields, computes something, and writes fields back, that logic
  probably belongs on the aggregate as an intention-revealing method.
- [ ] The aggregate is more than a data holder — it exposes **domain methods**
  (`accept()`, `reviewSessionProposal(...)`, `close()`), not just getters.
- [ ] State transitions are guarded **inside** the aggregate (e.g. a proposal can
  only be accepted from a valid prior status), not by callers checking status
  first.

> ArchUnit already guarantees no public setters/fields; this item is about
> whether the *behavior* actually lives there.

## 2. Aggregate boundaries and consistency

- [ ] A single use case mutates **one aggregate** per transaction. Modifying two
  aggregates atomically is a smell — prefer referencing the other by ID and
  coordinating via a domain event.
- [ ] Aggregates reference other aggregates **by identity (UUID)**, not by
  holding a direct object reference across the boundary.
- [ ] Invariants that span the boundary are handled through the
  outbox/event flow, not a cross-aggregate transaction.

## 3. Factory methods and construction invariants

- [ ] New aggregates are created via **static factory methods**
  (`Cfp.create(...)`), never `new Aggregate()` from application code.
- [ ] UUIDs are generated **inside** the aggregate factory, never assigned from
  outside.
- [ ] Creation-time invariants are enforced in the factory (e.g.
  `ConferenceSession.create()` rejecting a closed CFP via `SubmissionContext`),
  not left to the caller.

## 4. Value objects are true value objects

- [ ] Concepts with rules but no identity are modeled as **value objects**
  (records), not bare `String`/`int` primitives (primitive obsession). E.g. an
  email is an `EmailAddress`, not a `String`.
- [ ] Validation lives in the **compact constructor** and throws on invalid
  state — the type is impossible to construct in an invalid form.
- [ ] "Modifying" a value returns a **new instance**; nothing mutates in place.

## 5. Commands validate at the edge

- [ ] Command records validate **all** fields in their compact constructor
  (null checks, non-empty collections), so downstream code can trust them.
- [ ] Commands are immutable and carry intent — named for the use case
  (`CreateSessionProposalCommand`), not generic DTOs.

## 6. Logic sits in the correct layer

- [ ] Domain logic is in the **domain** (aggregate or `*DomainService`), not
  leaked into an application service, endpoint, or repository.
- [ ] `*ApplicationService` only **orchestrates**: load → call domain → persist →
  publish. It should read like a thin use-case script, not contain business
  rules.
- [ ] `*DomainService` is used only for **stateless domain logic that spans
  aggregates** and genuinely doesn't belong on one aggregate — not as a dumping
  ground for logic that an aggregate should own.

## 7. Repositories and persistence mapping

- [ ] Repositories return **domain aggregates**, never JPA entities or DTOs, to
  callers.
- [ ] All entity↔aggregate mapping (`toDomain`/`toEntity`) stays in the
  repository; no mapping logic leaks into application services.
- [ ] Deletes that must cascade use `getEntityManager().remove(entity)`, **not**
  bulk JPQL `delete(...)` (which bypasses cascade and breaks FK constraints —
  see [`ddd-conventions.md`](ddd-conventions.md)).
- [ ] No query/persistence detail (JPQL, `PanacheRepository`, entity types)
  appears above the persistence layer.

## 8. Domain events

- [ ] Events are **immutable** and named in the **past tense**
  (`SessionProposalAcceptedEvent`) — they describe something that already
  happened.
- [ ] Events implement the `shared.domain.DomainEvent` marker and live in
  `..domain.events..`.
- [ ] An event carries **enough context** for consumers to act without calling
  back into the source context.
- [ ] For the CFP → communications flow, changes preserve the **at-least-once**,
  idempotent-by-`source_event_id` semantics (see
  `session-proposal-accepted-communications-spec.md`). A new event path should go
  through the outbox, not fire a CDI event directly from the use case.

## 9. Ubiquitous language

- [ ] Type and method names use the **domain vocabulary** (CFP, session proposal,
  presenter, track, format, review, accept) — matching the terms in
  [`ddd-conventions.md`](ddd-conventions.md).
- [ ] Avoid generic CRUD verbs (`update`, `process`, `handle`, `manage`) where an
  intention-revealing domain verb exists.
- [ ] Naming is consistent with existing code — the same concept isn't called two
  different things (e.g. watch the `ConferenceSession` / `SessionProposal`
  rename; don't reintroduce the stale term).

## 10. Bounded-context integrity

- [ ] Cross-context access goes only through the other context's **public
  surface** — its `domain.services` ports or `domain.events` — never its
  `application`, `persistence`, aggregates, or value objects.
- [ ] `communications` depends on `cfp` **only** through `cfp.domain.events`; the
  dependency never points the other way.
- [ ] New cross-context coupling is questioned — the `BoundedContextArchTest`
  baseline is frozen, so any new edge is deliberate, not incidental.

---

## Using this checklist with `/code-review`

Ad-hoc:

```
/code-review high
Review this diff for Domain-Driven Design conformance using
docs/ddd-review-checklist.md. Flag anemic aggregates, business logic in the
wrong layer, aggregate-boundary violations, and ubiquitous-language drift.
Skip anything the ArchUnit rules already enforce.
```

Findings from this checklist are **advisory** — they inform the review, they do
not block the build. The build gate remains the ArchUnit suite
([`archunit-rules.md`](archunit-rules.md)).
