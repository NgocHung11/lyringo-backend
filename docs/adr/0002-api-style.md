# ADR 0002: API style

## Status

Accepted

## Context

The first backend scope is Auth, User, and Music. These APIs are simple enough for REST and include flows such as login, refresh token, upload URL creation, and playback URL creation.

## Decision

Start with REST API.

GraphQL can be added later as another interface adapter when frontend read queries become complex.
Both REST controllers and future GraphQL resolvers must call the same application use cases.

## Consequences

- REST keeps the first implementation simple.
- Upload and auth flows remain straightforward.
- Future GraphQL support will not require rewriting business logic.
