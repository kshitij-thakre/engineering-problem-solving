# Runtime Systems & Execution Environments

This module covers the internals of language runtime systems, memory allocation regions, garbage collection heuristics, async state machines, and concurrency architectures.

---

## 1. Memory Allocation: Heap vs. Stack

Programs allocate memory in two primary runtime regions, each with distinct performance characteristics:

```
+-----------------------------------+-----------------------------------+
|            STACK MEMORY           |            HEAP MEMORY            |
+-----------------------------------+-----------------------------------+
| Private to each execution thread. | Shared globally across threads.   |
| Fast LIFO (Last In, First Out).   | Dynamic allocation; slower.       |
| Stores primitives & references.   | Stores dynamic object instances.  |
| Managed via CPU Stack Frames.     | Managed via Garbage Collector.    |
+-----------------------------------+-----------------------------------+
```

### Stack Frame Execution
Every method call allocates a new **Stack Frame** storing:
* Return instruction address.
* Method parameters.
* Local primitive variables.

When the method returns, the Stack Frame is popped instantly. This requires zero GC overhead.

### Heap Fragmentation & Reference Trees
Heap objects can be allocated dynamically at any time. Because they outlive the method scopes that created them, they are accessed via reference pointers stored on the Stack. Over time, dynamic allocations create memory fragmentation, which language runtimes resolve via compaction.

---

## 2. Garbage Collection (GC) Architectures

Garbage Collection is the automatic reclamation of unreachable heap memory. Language runtimes implement different GC algorithms depending on execution targets:

### Cheney's Semispace Copy GC (Dart/V8 Young Space)
* **Goal**: Optimize for high-frequency allocation of short-lived objects (e.g. temporary layout nodes, UI widget templates).
* **Mechanics**:
  1. The young heap is split into two halves: *Active* and *Inactive* semispaces.
  2. Objects are allocated sequentially in the Active space.
  3. When Active space fills up, the GC copies *only live objects* to the Inactive space, compacting them sequentially, and swaps the active/inactive status of the spaces.
* **Performance**: Extremely fast ($<1$ms) since it only scales with the number of *surviving* objects, not total garbage.

### Mark-Sweep-Compact GC (JVM / Tenured Heap Spaces)
* **Goal**: Optimize for long-lived, larger heap structures.
* **Mechanics**:
  1. **Mark**: Traverse the object graph starting from "GC Roots" (thread stacks, static pointers) and mark all reachable nodes.
  2. **Sweep**: Reclaim memory allocations of unmarked nodes.
  3. **Compact**: Shift surviving objects together to reclaim contiguous free blocks.
* **Performance**: Slower because it stops execution threads (Stop-The-World pause).

---

## 3. Asynchronous Execution Models

Modern programming models achieve concurrency without blocking OS threads by using asynchronous execution loops:

### Single-Threaded Event Loop (Dart, Node.js, V8)
Runs a single execution thread with an event queue. Asynchronous tasks (like networking) are delegated to the host OS. When complete, their completion callbacks are pushed back to the event queue to execute on the main thread.
* **Benefit**: No synchronization locks or race conditions.
* **Drawback**: Computational bottlenecks (e.g., parsing a huge JSON string) block the main loop, halting rendering.

### Multi-Threaded Dispatch (JVM / Go / Rust)
Tasks are scheduled across a pool of background threads. Runtimes manage thread transitions and thread pools internally.
* **Benefit**: Exploit multi-core processor parallelism.
* **Drawback**: Synchronization locks and memory visibility must be managed explicitly to prevent race conditions.

---

## 4. Async State Machine Transformations

When you write asynchronous code using `async` and `await`, the compiler does not block the thread. Instead, it compiles the code into a **State Machine**:

```
[Function Start] ---> Run synchronously to first `await`
                      |
                      v
             [Suspension Point] ---> Return incomplete Future/Promise to caller;
                      |              Yield control back to Event Loop / Thread
                      v
             [Callback Resume]  ---> Event loop re-schedules task completion;
                                     Restore local variables; Resume next state
```

### Continuation-Passing Style (CPS)
In Kotlin, suspending functions are compiled using CPS. The compiler appends a hidden `Continuation` parameter (representing the execution state and callback) to the method signature, converting sequential code into a non-blocking state machine.

---

## 📂 Module Directory Index

* **[JVM & Coroutine Internals](./jvm_and_coroutine_internals.md)**: Deep-dive into JVM stack frames, heap allocation, CPS transformation, and coroutine execution.
* **[Dart Isolate & Event Loop](./dart_isolate_and_event_loop.md)**: Detailed mechanics of single-threaded Isolates, Event vs. Microtask queues, and generational semispace GCs.
* **[Object Pooling](./object_pooling.md)**: The Object Pool pattern implementation to bypass GC allocations for high-frequency objects.
