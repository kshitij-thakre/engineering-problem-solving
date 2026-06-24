# Scalable Systems & Engineering Patterns Handbook

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple.svg)](https://kotlinlang.org)
[![Dart](https://img.shields.io/badge/Dart-3.0-blue.svg)](https://dart.dev)
[![System Design](https://img.shields.io/badge/System%20Design-Universal-orange.svg)]()
[![Build](https://img.shields.io/badge/Build-Production-brightgreen.svg)]()

A production-grade, pattern-oriented handbook connecting classical computer science algorithms, runtime engine mechanics, and distributed resilience systems to real-world software engineering across backend systems and mobile platforms.

---

## 🚀 The Philosophy: Why This Repository Exists

Most standard DSA and system design repositories are split into two disjoint categories:
1. **Competitive Programming Archives**: Solve arbitrary, puzzle-style LeetCode challenges that do not map to real-world code.
2. **Abstract System Design Templates**: Show general block-diagram architectures without connecting them to actual runtime threads, heap limits, and compiler behaviors.

**This handbook bridges the gap.**

We believe senior software engineers, platform architects, and systems developers need a deep, unified understanding of:
* **Algorithmic Optimizations**: Monotonic deques, graphs, heaps, and sliding window boundaries.
* **Runtime Internals**: Generational Garbage Collection, heap fragmentation, stack frame allocation, single-threaded event loops, and coroutine Continuation state machines.
* **Concurrency Primitives**: Lock-free Compare-And-Swap (CAS), condition variables, counting semaphores, and reactive flow backpressure.
* **Distributed Resiliency**: State-driven circuit breakers, randomized exponential backoff and jitter, dynamic rate limiting, and write-replication consistency models.
* **Engineering Specializations**: Scaling stateful WebSockets, API Gateway reverse proxies, client rendering pipelines, outbox synchronization, and decoded image memory limitations.

---

## 🌟 Handbook Modules & Architecture

```
                                  +---------------------------------------+
                                  |     Scalable Systems Handbook Core    |
                                  +-------------------+-------------------+
                                                      |
              +---------------------------------------+---------------------------------------+
              |                                       |                                       |
    +---------v---------+                   +---------v---------+                   +---------v---------+
    |   Algorithmic CS  |                   |  Runtime Internals|                   |    Resilient DS   |
    |  - Monotonic Deq  |                   |  - Stack vs Heap  |                   |  - Circuit Break  |
    |  - Topo Sorting   |                   |  - Cheney's GC    |                   |  - Jitter Backoff |
    |  - Sliding Window |                   |  - CPS State Mach |                   |  - Token Bucket   |
    +---------+---------+                   +---------+---------+                   +---------+---------+
              |                                       |                                       |
              +---------------------------------------+---------------------------------------+
                                                      |
                                      +---------------+---------------+
                                      |                               |
                            +---------v---------+           +---------v---------+
                            |  Backend Scale    |           |   Mobile Systems  |
                            |  - Redis Pub/Sub  |           |  - Decoded Memory |
                            |  - Gateway Auth   |           |  - 3-Tree Render  |
                            |  - Task Queues    |           |  - Outbox Sync    |
                            +-------------------+           +-------------------+
```

---

## 📂 Repository Directory Index

* **[`patterns/`](./patterns/)**: Algorithmic paradigms (two pointers, sliding windows, heaps, trie searches, topological graphs) annotated with complexity charts.
* **[`runtime-systems/`](./runtime-systems/)**: Memory allocations (stack vs. heap), garbage collection compaction, V8/Dart event loops, and JVM coroutine internals.
* **[`concurrency/`](./concurrency/)**: Mutexes, semaphores, atomic operations, thread coordination, and thread-safe queues.
* **[`distributed-systems/`](./distributed-systems/)**: Resiliency structures including circuit breakers, retry policies, and dynamic rate limiting.
* **[`system-design/`](./system-design/)**: Caching topologies (LRU, LFU) and OAuth 2.0 PKCE secure session management.
* **[`backend-systems/`](./backend-systems/)**: Horizontal WebSocket architectures, API gateways, background task queues, and telemetry pipelines.
* **[`mobile-systems/`](./mobile-systems/)**: Low-level client pipelines: custom render passes, image memory metrics, video streaming, and push notification routes.
* **[`interview-notes/`](./interview-notes/)**: Technical prep cheat sheets on concurrency, memory leaks, and runtime systems.

---

## 📄 License & Target Audience
* **License**: [MIT License](./LICENSE)
* **Target Audience**: Senior Software Engineers, Systems Architects, Mobile Platform Leads, and Distributed Systems Engineers.

<!---LeetCode Topics Start-->
# LeetCode Topics
## Array
|  |
| ------- |
| [0027-remove-element](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/0027-remove-element) |
| [1470-shuffle-the-array](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/1470-shuffle-the-array) |
| [1920-build-array-from-permutation](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/1920-build-array-from-permutation) |
| [1929-concatenation-of-array](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/1929-concatenation-of-array) |
## Simulation
|  |
| ------- |
| [1920-build-array-from-permutation](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/1920-build-array-from-permutation) |
| [1929-concatenation-of-array](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/1929-concatenation-of-array) |
## Two Pointers
|  |
| ------- |
| [0027-remove-element](https://github.com/kshitij-thakre/engineering-problem-solving/tree/master/0027-remove-element) |
<!---LeetCode Topics End-->