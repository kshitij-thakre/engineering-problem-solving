# Mobile Engineering DSA & Systems Handbook

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple.svg)](https://kotlinlang.org)
[![Dart](https://img.shields.io/badge/Dart-3.0-blue.svg)](https://dart.dev)
[![Flutter](https://img.shields.io/badge/Flutter-3.x-cyan.svg)](https://flutter.dev)
[![Architecture](https://img.shields.io/badge/Architecture-Clean-brightgreen.svg)]()
[![System Design](https://img.shields.io/badge/System%20Design-Mobile-orange.svg)]()

A production-grade, pattern-oriented DSA and problem-solving platform specifically designed for scalable mobile engineering, Flutter/Dart isolates, JVM/Kotlin thread-safety, asynchronous streaming concurrency, and low-latency system design.

---

## 🚀 The Philosophy: Why This Repository Exists

Most standard DSA repositories feel like a random dump of competitive programming tricks or solved LeetCode archives. However, **senior mobile engineers do not write competitive code; they build highly responsive client systems.**

Mobile engineering sits at a unique intersection of severe environmental constraints:
* **Frame Rate Budgets**: Smooth rendering requires updating the UI every $16.6\text{ms}$ ($60\text{Hz}$) or $8.3\text{ms}$ ($120\text{Hz}$). Blocking the main thread for even a single frame triggers **stutter** (dropped frames).
* **Power & Thermal Limits**: Infinite background polling or running redundant threads will trigger OS battery-saver terminations.
* **Network Volatility**: Mobile networks drop connection, transition between Wi-Fi and 5G towers, and suffer high latency.
* **Heap Constraints**: Decoding massive raw images/bitmaps can instantly trigger Out-Of-Memory (OOM) crashes if memory allocation is unmanaged.

This repository serves as a **handbook connecting classical algorithms directly to high-performance client systems** in idiomatic **Dart (Flutter)** and **Kotlin (Android)**.

---

## 🌟 Featured Engineering Problems

### 1. [LRU Cache](./mobile-engineering-dsa/caching/lru_cache.md) (HashMap + Doubly Linked List)
* **Goal**: Maintain constant $O(1)$ lookups and cache eviction under predictable memory limits.
* **Client System Mapping**: Bitmap rendering engines (Glide, Coil, Kingfisher) hold decoded UI textures in heap using LRU allocations, evicting off-screen images automatically to prevent OOM spikes.

### 2. [Thread-Safe Producer-Consumer Queue](./mobile-engineering-dsa/concurrency/producer_consumer.md) (Bounded Suspension Channels)
* **Goal**: Safe, non-blocking transfer of data streams between asynchronous producers and background workers.
* **Client System Mapping**: Local log telemetry managers. UI interactions write diagnostic events to a bounded buffer, while a single background coroutine drains the buffer to batch uploads without freezing the UI.

### 3. [Priority Task Scheduler](./mobile-engineering-dsa/scheduling/task_scheduler.md) (Binary Max-Heap)
* **Goal**: Logarithmic $O(\log N)$ task insertions and priority extractions, resolving ties via chronological timestamps.
* **Client System Mapping**: Network task managers. Instant chat messages override telemetry flushes, queuing tasks in priority order and respecting device power configurations.

### 4. [BFS Navigation Graph](./patterns/graphs/bfs_navigation_system.md) (Unweighted Shortest-Path Traversal)
* **Goal**: Graph traversal that resolves screen pathways and constructs valid view backstacks.
* **Client System Mapping**: Deep Link Routers. When parsing deep URLs, the engine runs a BFS to build the backstack sequence so the system back button returns the user through standard logical view hierarchies.

### 5. [Offline-First Sync Engine](./system-design-mobile/offline_sync_engine.md) (Changelog Delta Sync)
* **Goal**: Eventual consistency between client-side SQLite databases and server repositories.
* **Client System Mapping**: Persistent outboxes, dynamic conflict-resolution (LWW, CRDTs), and HTTP call retries executing with randomized Exponential Backoff and Jitter.

---

## 🗺️ Learning & Progression Roadmap

```
  +-------------------------------------------------------------+
  |                   LEVEL 1: CS FUNDAMENTALS                  |
  |   Dynamic Sliding Windows | Monotonic Deques | Two Pointers |
  +------------------------------+------------------------------+
                                 |
                                 v
  +-------------------------------------------------------------+
  |            LEVEL 2: ASYNC CONCURRENCY & RUNTIMES            |
  |  Dart Event Loops | Isolate SendPorts | Kotlin Dispatchers  |
  +------------------------------+------------------------------+
                                 |
                                 v
  +-------------------------------------------------------------+
  |              LEVEL 3: MEMORY & CACHING SYSTEMS              |
  |     LRU Bitmap Allocators | LFU Buckets | GC Protection     |
  +------------------------------+------------------------------+
                                 |
                                 v
  +-------------------------------------------------------------+
  |          LEVEL 4: SCHEDULING & DATA RETRY PIPELINES         |
  |   Binary Max-Heaps | Exponential Backoff with Jitter | DBs  |
  +------------------------------+------------------------------+
                                 |
                                 v
  +-------------------------------------------------------------+
  |                 LEVEL 5: MOBILE SYSTEM DESIGN               |
  |   P2P WebRTC Signals | Silent Push Decryption | Paginated   |
  +-------------------------------------------------------------+
```

1. **Fundamentals**: Start with [Sliding Window Maximum](./patterns/sliding-window/sliding_window_maximum.md) (monotonic deque) and [Longest Substring](./patterns/sliding-window/longest_substring_without_repeating.md).
2. **Async & Concurrency**: Deep-dive into [Producer-Consumer Queue](./mobile-engineering-dsa/concurrency/producer_consumer.md), [Dart Runtime Mechanics](./dart-runtime/README.md), and [JVM Kotlin coroutines](./kotlin-runtime/README.md).
3. **Caching**: Study [LRU Cache](./mobile-engineering-dsa/caching/lru_cache.md) and [LFU Cache](./mobile-engineering-dsa/caching/lfu_cache.md).
4. **Scheduling**: Understand [Heap Priority Queues](./mobile-engineering-dsa/scheduling/priority_queue.md) and [WorkManager Constraints](./mobile-engineering-dsa/scheduling/work_manager_concepts.md).
5. **System Design**: Build architectural blueprints for [Chat Clients](./system-design-mobile/chat_system_design.md), [Push Delivery Pipelines](./system-design-mobile/notification_delivery_pipeline.md), and [WebRTC Calling](./system-design-mobile/video_calling_architecture.md).

---

## 🛠️ Complete Directory Structure

* **[`patterns/`](./patterns/)**: Classical algorithmic paradigms (arrays, sliding-window, trie searches, topological sorting) annotated with structural diagrams.
* **[`mobile-engineering-dsa/`](./mobile-engineering-dsa/)**: Multi-platform bounded buffers, priority task schedulers, outbox mutation synchronization, and LFU caching engines.
* **[`dart-runtime/`](./dart-runtime/README.md)**: Deep-dive into single-threaded Isolate sandboxes, Microtask vs. Event queues, Future FSM compiler transformations, and Cheney's generational Garbage Collector.
* **[`kotlin-runtime/`](./kotlin-runtime/README.md)**: Deep-dive into JVM stack frames, garbage-collected heaps, Continuation-Passing Style coroutine state machine compilation, and atomic CAS loops.
* **[`system-design-mobile/`](./system-design-mobile/)**: Enterprise-grade, multi-device mobile architecture blueprints covering offline synchronization, WebRTC signaling, list prefetching boundaries, and secure enclaves.
* **[`interview-notes/`](./interview-notes/)**: Senior-level cheat sheets detailing Flutter's three-tree layout pipelines, memory leak analysis, inline optimizations, and runtime dispatcher selections.

---

## 📄 License & Metadata
* **License**: [MIT License](./LICENSE)
* **Target Audience**: Senior Mobile Engineers, Flutter Architects, Android Platform Specialists, and Recruitment Leads looking for production-grade algorithms.
