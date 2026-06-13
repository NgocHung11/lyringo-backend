# ADR 0001: Backend architecture

## Status

Accepted

## Context

Lyringo starts with a single developer and should avoid operational complexity from microservices.
At the same time, the codebase should be easy to split into services later.

## Decision

Use a modular monolith with Clean Architecture / Hexagonal Architecture.

Business modules:

- Auth
- Users
- Music

Layering inside each module:

- `domain`: pure business model and rules
- `application`: use cases, ports, commands, DTOs
- `infrastructure`: database, storage, security, external implementations
- `interfaceadapter`: REST/GraphQL adapters

## Consequences

- Business logic is framework-independent.
- Spring annotations stay outside `domain` and `application`.
- JPA entities are separate from domain entities.
- Module boundaries are easier to preserve and test.
- More mapping code is required, but this is accepted to protect long-term maintainability.
