# quarkus-insights-cfp

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quarkus-insights-cfp-0.0.1-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplified JPA/Hibernate data access layer with active record and repository patterns
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)


[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

## Invariants (Business Rules)
- Presenters can submit a maximum of 4 sessions

## Durable acceptance communications

Accepting a session proposal notifies the presenter by email through a durable event pipeline
(full spec: `session-proposal-accepted-communications-spec.md`):

- **CFP outbox** — accepting a proposal writes the aggregate change *and* a `SessionProposalAcceptedEvent`
  row in the `cfp_outbox_event` table in one transaction. Nothing is emailed inline.
- **Synchronous CDI handoff** — a scheduled publisher drains the outbox and fires the event *synchronously*.
  The `communications` context observes it and records the message in the **same transaction** that marks the
  outbox row `PUBLISHED`; if recording fails, the publication rolls back and is retried.
- **Communications idempotency** — the `communications` record is keyed by the source event ID (unique
  constraint), so a re-published event never creates a second message or a duplicate delivery.
- **Separate email-delivery scheduler** — a second scheduler claims pending deliveries and calls SMTP
  *outside* any database transaction, then records `DELIVERED`, `RETRY_SCHEDULED`, or `PERMANENTLY_FAILED`.
  Email failures retry independently of the CFP proposal/outbox state.
- **At-least-once, not exactly-once** — external email delivery is **at least once**. A crash after SMTP
  accepts a message but before the database records success may cause a **duplicate email**.

## Listening

I built most of this project on a car trip listening to the following albums:
- Phish - _The Gorge '98'_
- Bruce Springsteen and the E Street Band - _The Legendary 1979 No Nukes Concert_
- Anonymous 4 - _The Origin of Fire: Music and Visions of Hildegard von Bingen_
- Fugazi - _Fugazi Live at Metropolitan University Leeds UK 10/31/02_

My trip started in Maine, and New England made me think of Phish.  Springsteen was an obvious choice by the time we hit New Jersey.  I deviated from the pattern in Pennsylvania, but returned to it when outside of D.C. with Fugazi.