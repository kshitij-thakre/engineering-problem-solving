# Contributing to Scalable Systems & Engineering Patterns Handbook

Thank you for your interest in contributing to the **Scalable Systems & Engineering Patterns Handbook**! This repository serves as an open-source engineering handbook connecting algorithms, runtime mechanics, and distributed systems design to real-world software engineering.

---

## 1. Repository Scope

We welcome contributions across any of our core pillars:
1. **Patterns**: General computer science algorithms mapped to systems-level resource optimizations.
2. **Runtime Systems**: Memory allocations (stack vs. heap), GC optimizations, event loops, and async state machines.
3. **Concurrency**: Mutexes, semaphores, atomics, lock-free patterns, and flow control.
4. **Distributed Systems**: Retry systems, circuit breakers, rate limiting, and eventual consistency.
5. **Backend Systems**: WebSocket architectures, API gateways, and ingestion pipelines.
6. **Mobile Systems**: Client-side offline-first consistency, rendering passes, and bitmap caching.

---

## 2. Technical Code Standards

Every code contribution must meet our production-grade standard:
* **Clean Code**: Ensure your variables use clear, explanatory terminology. Avoid short variable naming.
* **Annotated Comments**: Comment complex pointer swaps, heap operations, and concurrency state transitions.
* **Dual Implementations**: Where applicable, provide both **Kotlin** and **Dart** implementations side-by-side to demonstrate garbage collector and memory-visibility tradeoffs.
* **Formatting**: Format code according to language guidelines (`dart format` and ktlint conventions).

---

## 3. Markdown Formatting Standards

* Mathematical equations must use LaTeX formatting (e.g. $O(\log N)$).
* Visualize architectural components using **Mermaid** sequence, state, or flow diagrams.
* Ground your explanations in technical data, avoiding marketing fluff or introductory tutorials.
* Structure problems using the standard layout: Problem, Pattern, Approach, Complexity, Systems Relevance, Code.
