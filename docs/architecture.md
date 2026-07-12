# Architecture Overview

## Bounded Contexts

The application has two bounded contexts:

| Context | Package | Purpose |
|---|---|---|
| `cfp` | `io.arrogantprogrammer.quarkusinsights.cfp` | Primary: CFP lifecycle, session proposals, presenters, tracks, formats |
| `conference` | `io.arrogantprogrammer.quarkusinsights.conference` | Nascent: cross-context integrations |

**Cross-context rule**: `conference` may only access `cfp` through the public `domain` package. Direct access to `cfp.application`, `cfp.persistence`, or `cfp.infrastructure` is forbidden (enforced by ArchUnit's `BoundedContextArchTest`).

---

## Layer Structure

Each bounded context follows a strict four-layer hexagonal architecture:

```
infrastructure/          ← JAX-RS resources, parameter classes (Jakarta EE annotations OK)
    ↓
application/             ← Use-case orchestration (*ApplicationService), commands, DTOs, mappers
    ↓
domain/                  ← Pure Java: aggregates, value objects, domain services
    ↑
persistence/             ← Panache repositories, JPA entities (adapts domain ↔ DB)
```

### What belongs where

| Layer | Can import | Cannot import |
|---|---|---|
| `domain` | Java stdlib only | `jakarta.persistence`, `jakarta.ws.rs`, ORM, CDI |
| `application` | `domain`, CDI | `jakarta.ws.rs`, persistence annotations |
| `persistence` | `domain`, JPA, Panache | `jakarta.ws.rs` |
| `infrastructure` | `application`, JAX-RS | direct `domain` aggregate mutation |

---

## REST API Contract

| Method | Path | Description |
|---|---|---|
| GET | `/cfp` | List all CFPs |
| POST | `/cfp` | Create a CFP |
| GET | `/cfp/{id}` | Get a CFP |
| PUT | `/cfp/{id}` | Update a CFP |
| DELETE | `/cfp/{id}` | Delete a CFP (cascades to formats, tracks) |
| GET | `/presenters/{email}` | Get presenter by email |
| POST | `/presenters` | Create a presenter |
| PUT | `/presenters/{email}` | Update a presenter |
| DELETE | `/presenters/{email}` | Delete a presenter |
| POST | `/session-proposals` | Submit a session proposal |

All endpoints return JSON. Delete returns 204 No Content on success, 404 if not found.

---

## Frontend (Quinoa + React)

### Dev Mode

```
Browser ←→ :8080 (Quarkus) ←→ :5173 (Vite subprocess)
                   ↕
              REST API
```

Quinoa starts Vite as a subprocess and proxies all non-API requests to it. The browser only ever talks to `:8080`. Hot Module Replacement (HMR) works through this proxy.

### Production Mode

```
Browser ←→ :8080 (Quarkus)
              ↕
    /q/ui/  → static files from dist/
    /cfp    → REST API
```

Quinoa embeds the Vite `dist/` output in the Quarkus JAR at build time. No separate frontend server.

### SPA Routing

`quarkus.quinoa.enable-spa-routing=true` makes Quarkus return `index.html` for any path that does not match a REST route. This allows React Router client-side navigation (direct URL access to `/cfp/new`, `/submit`, etc.) without a 404 from Quarkus.

---

## Port Topology

| Port | Service | When |
|---|---|---|
| 8080 | Quarkus (REST + Quinoa proxy) | Always |
| 5173 | Vite dev server | Dev mode only (subprocess managed by Quinoa) |
| 5432 | PostgreSQL | Dev Services (Docker, ephemeral per dev run) |

---

## Data Model Summary

```
Cfp
 ├── formats: List<ConferenceSessionFormat>   (value objects, @OneToMany cascade ALL)
 └── conferenceTracks: List<ConferenceTrack>  (value objects, @OneToMany cascade ALL)

SessionProposal
 ├── cfpId: UUID
 ├── presenterEmail: EmailAddress
 ├── conferenceSessionFormat: ConferenceSessionFormat (embedded)
 ├── conferenceTrack: ConferenceTrack (embedded)
 └── programmingLanguagesUsed: List<ProgrammingLanguage>

Presenter
 └── emailAddress: EmailAddress (value object)
```

`Duration` fields (session formats) are stored as nanoseconds (Java `Duration` → `long`). In JSON they appear as ISO-8601 strings (e.g. `"PT45M"`) via `JavaTimeModule`.
