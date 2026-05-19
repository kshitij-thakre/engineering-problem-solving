# Concurrency, Synchronization & Thread Coordination

This module covers concurrent programming paradigms, low-level execution thread synchronization, memory visibility models, and asynchronous data pipelines.

---

## 1. Concurrency vs. Parallelism

Before designing multi-threaded software, it is vital to distinguish between concurrency and parallelism:
* **Concurrency**: Managing multiple tasks by interleaving their execution on a single core (logical concurrency). It is about **structure**—how a program is decomposed into independent execution threads.
* **Parallelism**: Executing multiple tasks simultaneously on multiple physical CPU cores. It is about **execution**—speeding up computations by distributing work.

```
Concurrency (1 CPU Core):   [Task A][Task B][Task A][Task B]  --> Interleaved
Parallelism (2 CPU Cores):  Core 1: [Task A][Task A]
                            Core 2: [Task B][Task B]          --> Simultaneous
```

---

## 2. Low-Level Synchronization Primitives

When threads run in parallel with access to a shared memory heap, race conditions can corrupt state. We protect critical sections of code using synchronization primitives:

### Mutex (Mutual Exclusion)
* **Definition**: A lock that guarantees only *one* thread can enter a critical section at any given time.
* **Lock Ownership**: The thread that acquires the mutex is the only thread that can release it.
* **Mobile/Async Relevance**: Classical OS mutexes block the calling thread. In modern runtimes, non-blocking coroutine-based mutexes (like Kotlin's `Mutex`) suspend the execution scope without blocking the underlying OS thread, freeing CPU cores.

### Semaphore
* **Definition**: A concurrency controller that manages access to a limited pool of identical resources using a counter.
  * **Binary Semaphore**: Counter is initialized to 1 (behaves similarly to a Mutex but lacks lock ownership; any thread can release it).
  * **Counting Semaphore**: Counter represents $N$ available resource permits. When a thread requests a resource (`acquire()`), the counter decrements. If the counter reaches 0, subsequent threads block until a permit is released (`release()`).
* **Use Case**: Rate-limiting connections (e.g. allowing at most 5 concurrent network connections to a server to prevent socket starvation).

### Condition Variables
* **Definition**: Associated with a Mutex to allow threads to suspend execution (wait) until a specific state condition is met.
* **Mechanics**: A thread releases the associated mutex and blocks. When another thread modifies the state, it signals the condition variable, waking the blocked thread to re-acquire the lock.
* **Use Case**: Building bounded buffers (e.g. classical [Producer-Consumer Queue](./producer_consumer.md)).

---

## 3. Lock-Free Concurrency & Atomic Operations

Thread locking (Mutexes, Semaphores) introduces execution overhead, thread context switches, and the risk of **deadlocks** or **priority inversions**. High-performance systems bypass locking using **Lock-Free Concurrency**.

### Atomic Operations
* **Definition**: Operations that execute as a single, indivisible step. The hardware guarantees that no other thread can observe the operation in a partially completed state.
* **Hardware Support**: Modern CPUs implement the **Compare-And-Swap (CAS)** instruction:
  $$\text{CAS}(\text{address}, \text{expectedVal}, \text{newVal}) \to \text{boolean}$$
* **Kotlin/JVM Implementation**:
  ```kotlin
  import java.util.concurrent.atomic.AtomicInteger

  val counter = AtomicInteger(0)
  counter.incrementAndGet() // Uses native CPU CAS loop to increment safely without locks
  ```

### Memory Visibility & Volatile
In multi-core processors, each core has private cache levels (L1, L2). If Core 1 writes a variable to its cache, Core 2 reading from RAM will see stale data.
* **Volatile (`@Volatile` in Kotlin, `volatile` in Java)**: Tells the compiler and CPU to disable cache optimization for this variable. Every write is flushed directly to main RAM, and every read is fetched directly from main RAM.
* **Crucial Rule**: Volatile only solves memory visibility; it does *not* solve race conditions during compound operations (like `counter++` which involves a read, modify, and write step).

---

## 4. Thread Coordination Paradigms

Coordinating thread lifecycles and gating task completions is vital for boot/initialization phases:

* **Barriers & Latches (e.g. `CountDownLatch` / `CyclicBarrier`)**:
  * **CountDownLatch**: A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes. The latch count decrements via `countDown()`. Once it hits 0, waiting threads resume.
  * **CyclicBarrier**: Allows a set of threads to all wait for each other to reach a common barrier point before continuing. It can be reused after the waiting threads are released.
* **Future/Promise Join**: Aggregating parallel async workers (e.g. `Future.wait()` in Dart, `awaitAll()` in Kotlin Coroutines).

---

## 5. Asynchronous Data Pipelines

Async pipelines manage continuous data streams asynchronously, employing operators for transformation, filtering, and flow control.

### Reactive Streams (Streams & Flows)
* **Kotlin `Flow`**: Cold streams that execute only when collected. Built natively on coroutines for structured concurrency.
* **Dart `Stream`**: Push-based event models. `StreamController` handles event sinks, while broadcast streams allow multiple subscribers.

### Backpressure Control
When a producer generates data faster than a consumer can process it, memory builds up. We regulate this using **Backpressure Strategies**:
1. **Buffer**: Store incoming elements in a queue (e.g. bounded channel) up to a capacity, suspending the producer when full.
2. **Drop / Latest**: Discard incoming events when the consumer is busy, optionally keeping only the newest event (e.g. throttle/debounce user search inputs).
3. **Control Flow / Pull-Model**: Consumer explicitly requests $N$ items from the producer when ready.

---

## 📂 Module Directory Index

* **[Producer-Consumer Queue](./producer_consumer.md)**: Standard thread-safe queue implementation in Dart and Kotlin.
* **[Async Task Scheduler](./async_task_scheduler.md)**: Asynchronous task dispatch and scheduling queue structures.
* **[Synchronization & Locks](./synchronization_and_locks.md)**: Code examples of custom Mutex structures and database lock protections.
* **[Async Serial Job Queue](./async_job_queue.md)**: Serialized task loop queue.
* **[Coroutine Dispatchers](./coroutine_dispatchers.md)**: Coroutine dispatcher pools and context boundaries.
* **[Dart Event Queue](./dart_event_queue.md)**: Single-threaded event loops, event precedence, and isolates.
