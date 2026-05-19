# Backend Systems & Scalable Infrastructure

This module covers backend architectural patterns, dynamic bidirectional connection management, API routing layers, asynchronous task queues, high-velocity telemetry ingestion pipelines, and task scheduling systems.

---

## 1. WebSocket Architecture & Persistent Connections

Unlike traditional REST endpoints which close connection sockets after a single request-response cycle, WebSockets establish a persistent, bidirectional, full-duplex TCP connection:

```
Client                        API Gateway / Load Balancer               WebSocket Server
  |                                       |                                    |
  |--- HTTP Upgrade Request ------------->|                                    |
  |    (Headers: Upgrade: websocket)      |--- Forward Upgrade Request ------->|
  |                                       |                                    |
  |<-- 101 Switching Protocols -----------|<-- Accept Handshake ---------------|
  |                                       |                                    |
  |============ ESTABLISHED FULL-DUPLEX TCP CONNECTION (WS FRAME STREAM) =======|
```

### Connection Management & State
* **State Preservation**: Because WebSocket servers retain connection state in memory (e.g. active socket handles, session descriptors), scaling out requires:
  1. **Sticky Sessions**: Load balancers route subsequent reconnects from the same client to the same server node.
  2. **Pub/Sub Broker (e.g. Redis)**: When Node A wants to send a message to Client 2 whose socket is connected to Node B, Node A publishes to a Redis channel. Node B subscribes to the channel and delivers the message to Client 2.
* **Heartbeats**: Ping/Pong control frames are executed periodically to detect silent connection drops and prune dead connection slots.

---

## 2. API Gateway Concepts

An **API Gateway** is a reverse proxy that acts as the single entry point for client requests, separating clients from internal microservices.

### Core Architecture Responsibilities
1. **Routing**: Inspects request paths and forwards traffic to the correct downstream microservice.
2. **Security & Authentication**: Validates client access tokens (e.g. parsing JWTs at the gate) so internal services do not need to repeat token verification logic.
3. **Cross-Cutting Concerns**: Eases monitoring by logging every incoming request, appending correlation IDs to trace calls across microservices, and injecting CORS headers automatically.
4. **Rate Limiting**: Enforces rate limits (e.g., using Token Bucket redis-backed algorithms) to prevent malicious scraping or service overload.

---

## 3. Asynchronous Task Queues

Task Queues handle heavy background processes asynchronously, separating instant API responses from slow backend workflows (e.g. generating PDFs, processing media uploads, dispatching notifications).

### Execution Pipelines
* **Task Submission**: The API server accepts a request, registers a task entry in a queue (backed by Redis or RabbitMQ), returns an instant `202 Accepted` response with a task ID to the client, and yields control.
* **Task Processing**: Independent background workers pull tasks from the queue sequentially, execute the work, and write the completion state back to a database. Clients can query the completion state using long-polling or await WebSocket callbacks.

---

## 4. Telemetry & Analytics Ingestion Pipelines

Modern enterprise services ingest high-velocity data points (analytics, system metrics, performance traces) continuously. High-velocity writes can overwhelm relational databases.

### Scalable Ingestion Pipeline
1. **Ingestion Gate**: Lightweight HTTP endpoints receive telemetry logs in batches.
2. **Buffering (e.g., Kafka / Kinesis)**: Writes events directly to a high-throughput, partitioned distributed commit log, acting as a shock absorber.
3. **Stream Processing (e.g., Flink / Spark)**: Background worker jobs parse events from the log stream, filter duplicates, aggregate values in sliding time windows, and write outputs to a Time-Series Database (e.g., InfluxDB, Prometheus) or analytical store (e.g., ClickHouse).

---

## 📂 Module Directory Index

* **[WebSocket Connection Scaling](./websocket_architecture.md)**: Horizontal scaling using Redis Pub/Sub, load balancing, sticky sessions, and ping/pong heartbeat tracking.
* **[API Gateway Core Concepts](./api_gateway_concepts.md)**: Path routing, JWT authentication delegation, reverse proxy setups, and rate limit intercepts.
* **[Asynchronous Task Queues](./task_queues.md)**: Job decoupling, worker scaling patterns, and Redis (Celery/BullMQ) vs. RabbitMQ brokers.
* **[Telemetry Ingestion Pipelines](./telemetry_ingestion.md)**: Log ingestion gateways, commit log buffers (Kafka), stream window processors, and ClickHouse column-stores.
* **[Task Scheduler Systems](./scheduler_systems.md)**: Priority heap task execution and tie-breaking algorithms.
* **[Kotlin Scheduler Implementation](./task_scheduler_kotlin.kt)**: Thread-safe JVM priority scheduler using Kotlin/Java `PriorityQueue`.
* **[Dart Scheduler Implementation](./task_scheduler_dart.dart)**: Custom binary max-heap task scheduler built from scratch.
