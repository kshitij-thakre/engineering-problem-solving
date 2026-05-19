# Distributed Systems & Resiliency Patterns

This module covers distributed system fundamentals, reliability patterns, network resilience, message queues, eventual consistency models, and the CAP theorem.

---

## 1. The CAP Theorem

In any distributed data store, you can only guarantee two out of the following three properties simultaneously:

```
                  +-----------------------+
                  |      Consistency      |
                  |  All nodes see same   |
                  |    data at same time  |
                  +-----------+-----------+
                              |
                              |
              +---------------+---------------+
              |                               |
    +---------+---------+           +---------+---------+
    |   Availability    |           | Partition Tolerance|
    |  Every non-failing|           | System continues   |
    |  node returns response|       | despite network    |
    +-------------------+           | packet drops/delays|
                                    +-------------------+
```

### The Tradeoff during a Network Partition
When a network partition ($P$) occurs between nodes:
* **Consistency ($C$) over Availability ($A$)**: The system rejects updates or blocks read access to prevent serving divergent stale data, causing downtime (CP system).
* **Availability ($A$) over Consistency ($C$)**: Nodes continue accepting reads and writes locally. The system remains fully functional but nodes diverge, yielding data inconsistency temporarily (AP system).

---

## 2. Eventual Consistency & Replication Models

In high-scale distributed systems, enforcing strong consistency requires locking resources across multiple databases, which bottlenecks write throughput. We trade strong consistency for **Eventual Consistency**:

* **Eventual Consistency**: The system guarantees that if no new updates are made, all replicas will eventually converge to match.
* **Write Replication Pipelines**:
  * **Synchronous Replication**: The primary node writes locally and waits for all replicas to acknowledge the write before returning success. (Guarantees Consistency, reduces Availability/Write speed).
  * **Asynchronous Replication**: The primary node writes locally and returns instantly. A background channel replicates updates asynchronously to replicas. (Guarantees Availability, yields Eventual Consistency).

---

## 3. Network Resiliency & Self-Healing Patterns

Network calls are unreliable. Distributed services protect themselves from cascading network failures using resiliency patterns:

### Retry Systems with Exponential Backoff and Jitter
Repeatedly retrying failed network calls instantly can overwhelm downstream services (Retry Storm / Throttling).
* **Exponential Backoff**: Multiplies the delay before each retry attempts:
  $$\text{Delay} = \text{base} \times 2^{\text{attempt}}$$
* **Jitter**: Introduces random variation to the delay to stagger concurrent client retries:
  $$\text{DelayWithJitter} = \text{random}(0, \text{Delay})$$

### Circuit Breaker Pattern
Prevents an application from repeatedly trying to execute an operation that is highly likely to fail. It acts as an electrical circuit breaker:

```
   [Closed]  ---(Failure threshold exceeded)--->  [Open]
      ^                                             |
      |                                      (Cool down timeout)
      |                                             v
   [Closed]  <---(Success count met)---  [Half-Open]
```

1. **Closed**: Normal operations. Traffic flows. If failures cross a threshold (e.g. 50% failures), the breaker transitions to **Open**.
2. **Open**: Requests fail immediately *without* hitting the remote server, protecting resources and giving the server time to recover.
3. **Half-Open**: After a cooldown period, the breaker allows a limited number of requests through to test if the service has recovered. If they succeed, it returns to **Closed**; if they fail, it returns to **Open**.

---

## 4. Distributed Message Queues

Queues decouple microservices, serving as asynchronous shock absorbers between high-velocity write events and slower background processors:
* **Point-to-Point Queue**: Messages are delivered to exactly one consumer (e.g. RabbitMQ Task Queues).
* **Publish-Subscribe Log**: Messages are written to a persistent append-only log and split into partition boundaries. Multiple consumer groups can read the log independently by tracking their offset position (e.g. Apache Kafka).

---

## 📂 Module Directory Index

* **[Rate Limiting (Token Bucket)](./rate_limiting.md)**: Dynamic rate regulation and spam tap throttling.
* **[Circuit Breaker](./circuit_breaker.md)**: State machine-driven remote call failures and recovery pools.
* **[Offline Synchronization & Jitter Retries](../mobile-systems/offline_sync.md)**: Implementation of exponential backoff, jitter, and outbox queues.
