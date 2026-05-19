# Backend Systems: API Gateway Concepts

This document details the architectural role, request routing, rate limiting integration, security delegation, and performance optimizations of an **API Gateway**.

---

## 1. Single Point of Entry Reverse Proxy

An **API Gateway** acts as a reverse proxy, positioning itself between external client requests and internal microservices. This abstraction layer prevents exposing microservice IP addresses directly to public client DNS layers, consolidating traffic routing.

```
                  +--------------------------------+
                  |         Client Devices         |
                  +---------------+----------------+
                                  |
                                  | HTTP / WebSocket Requests
                                  v
                  +---------------+----------------+
                  |          API Gateway           |
                  |  - Auth Delegation             |
                  |  - Path Routing                |
                  |  - Rate Limiting               |
                  +---------------+----------------+
                                  |
            +---------------------+---------------------+
            |                     |                     |
     +------v------+       +------v------+       +------v------+
     |   Identity  |       |     Chat    |       |   Inventory |
     | Microservice|       | Microservice|       | Microservice|
     +-------------+       +-------------+       +-------------+
```

---

## 2. Request Routing & Path Mapping

The Gateway inspects incoming request URLs, paths, and headers, translating them into upstream service endpoints:
* A request to `https://api.app.com/v1/auth/login` maps to `http://identity-service:8080/login`.
* A request to `https://api.app.com/v1/messages/sync` maps to `http://chat-service:8082/sync`.

This decoupled routing layer allows backend engineering teams to reorganize, split, or refactor microservices without requiring mobile clients to update their hardcoded API endpoints.

---

## 3. Distributed Security & Authentication Delegation

Verifying JSON Web Tokens (JWTs) on every microservice introduces duplicate cryptographic validation logic and database hits. The API Gateway delegates authentication:
1. The client attaches the `Authorization: Bearer <JWT>` header to the request.
2. The API Gateway intercepts the request, validates the signature using the Identity Service's public key (retrieved via JWKS), and extracts the token payload (e.g. `user_id`, `scopes`).
3. The Gateway strips the raw JWT and injects custom headers (e.g., `X-User-Id: 98721`) before forwarding the request to downstream services.
4. Downstream microservices treat the internal network as trusted, reading `X-User-Id` directly, bypassing JWT signature calculations.

---

## 4. Rate Limiting Integration

To defend backend databases from denial-of-service attempts and credential stuffing, the Gateway enforces rate limits. It uses a high-throughput cache (like Redis) running a Token Bucket or Leaky Bucket algorithm.
* Every incoming request checks: `rate_limiter:user_123` or `rate_limiter:ip_192.168.1.1`.
* If request count exceeds limits, the Gateway returns an immediate **HTTP 429 Too Many Requests** response, saving microservice resource threads.
