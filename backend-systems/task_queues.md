# Backend Systems: Asynchronous Task Queues

This document details the architectural separation of client requests from heavy calculations, message broker patterns, background worker processes, and execution reliability.

---

## 1. Decoupling Execution: Request-Response vs. Background Jobs

Traditional API requests expect immediate execution. If a route triggers a heavy transaction (e.g. compressing high-resolution user assets, converting video codecs, bulk syncing client catalogs), executing it synchronously on the web server thread blocks the thread pool, causing incoming connection requests to queue and time out.

### The Decoupled Pattern
Instead of processing inline, we push the request payload to an asynchronous **Task Queue**:

```
Client                              Web API Server                           Task Queue (Redis/RabbitMQ)
  |                                       |                                              |
  |--- POST /assets/upload -------------->|                                              |
  |                                       |--- Push Job: {id: 101, type: COMPRESS} ---->|
  |                                       |                                              |
  |<-- HTTP 202 Accepted {job_id: 101} ----|                                              |
  |                                                                                      |
  |============ CLIENT POLLS OR AWAITS SOCKET NOTIFICATION FOR COMPLETION =============|
```

1. The client sends a request.
2. The Web API server validates input, generates a unique job ID, serializes the metadata payload, and appends it to a message broker.
3. The server immediately returns an **HTTP 202 Accepted** status containing the `job_id` back to the client.
4. Independent background workers pull tasks from the queue and perform the actual work asynchronously.

---

## 2. Broker Architectures: Redis (Celery/BullMQ) vs. RabbitMQ

Backend services choose brokers based on throughput and delivery guarantees:

### 1. Redis-Backed Queues (e.g. BullMQ, Celery)
* **Under the Hood**: Uses Redis data structures (Sorted Sets for priority/delayed tasks, Streams/Lists for sequential pipelines).
* **Pros**: Sub-millisecond enqueue speeds, low deployment footprint.
* **Cons**: All queues must fit in RAM. If Redis instances crash without persistent disk saves enabled, uncompleted tasks in memory are lost.

### 2. AMQP Message Brokers (e.g. RabbitMQ)
* **Under the Hood**: Standard Advanced Message Queuing Protocol broker. Messages pass through **Exchanges** before arriving at **Queues**.
* **Pros**: Explicit delivery acknowledgments, complex routing logic, persistent disk storage guarantees.
* **Cons**: Higher setup and memory cost.

---

## 3. Worker Scaling & Queue Concurrency

To handle variable task volume, we scale worker processes horizontally.
* **Auto-Scaling (HPA)**: In containerized environments (Kubernetes), worker pods scale up/down based on the depth of the task queue (e.g. if queue length $>100$ items, spin up additional worker containers).
* **Fair Dispatching**: The broker must distribute tasks evenly. In RabbitMQ, this is configured via `basic.qos(prefetch_count=1)`, which prevents a single worker from buffering multiple tasks while other workers sit idle.
