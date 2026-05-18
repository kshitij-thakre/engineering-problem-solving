# Thread-Safe Producer-Consumer Queue

## Pattern
**Thread Synchronization / Async Messaging Channel / Event-Loop Queuing**

---

## Problem
Design and implement a thread-safe, bounded/unbounded Producer-Consumer Queue where multiple producers can add tasks and multiple consumers can extract and process tasks concurrently, preventing race conditions, deadlocks, and busy-waiting.

---

## Approach

### Kotlin (JVM/Coroutines Approach)
On multi-threaded runtimes like Kotlin/JVM, multiple threads access the queue simultaneously.
1. Use **Kotlin Coroutines and `Channel`** (or a Java `LinkedBlockingQueue` inside a synchronized block) to represent the pipeline.
2. Producers suspend when the channel is full (if bounded).
3. Consumers suspend when the channel is empty, preventing CPU-hogging busy-waiting.
4. This ensures structured concurrency, avoiding standard thread locks (`synchronized`/`ReentrantLock`) which block OS threads.

### Dart (Event-Loop / Stream Approach)
Dart is single-threaded and runs on an **Event Loop** inside an **Isolate**. However, concurrency is achieved asynchronously via:
1. **`StreamController` / Streams**: Acts as an asynchronous queue.
2. **Producers** push tasks into the `StreamController`.
3. **Consumers** listen to the stream asynchronously. Tasks are processed sequentially or concurrently via asynchronous microtasks or asynchronous event triggers without blocking the main rendering loop.
4. For CPU-bound tasks, Dart uses **`Isolates`** (separate threads with their own heap memory) communicating via ports.

---

## Time Complexity
* **Enqueue (Produce)**: **$O(1)$** - Constant-time insertion to the tail of the queue.
* **Dequeue (Consume)**: **$O(1)$** - Constant-time extraction from the head of the queue.

## Space Complexity
**$O(N)$**: Where $N$ is the number of queued tasks.

---

## Why This Solution Works
By utilizing suspension (Kotlin Coroutines) and asynchronous event streams (Dart Streams), the consumer and producer threads/event-loops are suspended or idle when no work is available. They wake up automatically upon event dispatch, avoiding CPU spin-locks.

---

## Mobile Engineering Relevance
Concurrency is the cornerstone of keeping mobile apps responsive.
* **UI Thread Protection**: The primary threat to mobile UX is blocking the Main/UI thread. Doing networking, image processing, or database writes on the UI thread drops frames, causing user-visible stutter.
* **Event Dispatcher**: The producer-consumer queue is the direct model for the Flutter Event Loop and the Android Main Looper.
* **Analytics & Logging Pipelines**: When a user taps multiple buttons, the app generates analytics events. A background Producer pushes events to an offline storage queue. A consumer job running on a background worker threads them, batching them into network payloads and shipping them to the server every 10 seconds.

---

## Tradeoffs
* **Thread Locking vs. Suspension**: Traditional lock-based synchronization (e.g. `java.util.concurrent.LinkedBlockingQueue`) blocks the operating system thread, which is resource-heavy. Coroutine suspension and asynchronous event loops yield much higher throughput and use significantly less system overhead, which is optimal for mobile devices.

---

## Code Solution

### Dart
In Dart, we implement a producer-consumer system using a custom asynchronous queue backed by a `StreamController`. For heavy processing, we spawn a separate `Isolate` to avoid janking the main UI thread.

```dart
import 'dart:async';

class AsyncProducerConsumerQueue<T> {
  final _controller = StreamController<T>();
  late final Stream<T> _stream;

  AsyncProducerConsumerQueue() {
    _stream = _controller.stream.asBroadcastStream();
  }

  // Producer adds tasks to the queue
  void produce(T task) {
    if (!_controller.isClosed) {
      _controller.add(task);
    }
  }

  // Consumer listens to the queue
  StreamSubscription<T> consume(void Function(T task) onTask) {
    return _stream.listen(onTask);
  }

  void dispose() {
    _controller.close();
  }
}

class Task {
  final String id;
  final String payload;
  Task(this.id, this.payload);

  @override
  String toString() => 'Task(id: $id, payload: $payload)';
}

void main() async {
  print("=== Dart Event-Loop Producer-Consumer Queue ===");
  final queue = AsyncProducerConsumerQueue<Task>();

  // Consumer 1 Setup
  queue.consume((task) {
    print("[Consumer 1] Processing: $task");
  });

  // Consumer 2 Setup (Parallel consumption)
  queue.consume((task) {
    print("[Consumer 2] Processing: $task");
  });

  // Producers dispatching tasks
  queue.produce(Task("1", "Process user login event"));
  queue.produce(Task("2", "Compress profile avatar"));
  queue.produce(Task("3", "Write offline cache data"));

  await Future.delayed(Duration(milliseconds: 100));
  queue.dispose();
}
```

### Kotlin
In Kotlin, we utilize structured concurrency via Kotlin Coroutines and a `Channel` representing the concurrent queue.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class Task(val id: String, val payload: String) {
    override fun toString(): String = "Task(id='$id', payload='$payload')"
}

class ConcurrentQueue(capacity: Int) {
    private val channel = Channel<Task>(capacity)

    // Suspending Producer
    suspend fun produce(task: Task) {
        println("[Producer] Dispatching: $task")
        channel.send(task)
    }

    // Suspending Consumer
    suspend fun consume(): Task {
        return channel.receive()
    }
}

fun main() = runBlocking {
    println("=== Kotlin Coroutine Producer-Consumer Queue ===")
    val queue = ConcurrentQueue(capacity = 5)

    // Launch Consumer 1 in a background coroutine
    val consumer1 = launch(Dispatchers.Default) {
        while (isActive) {
            val task = queue.consume()
            println("[Consumer 1] Thread ${Thread.currentThread().name} is processing: $task")
            delay(150) // Simulate processing time
        }
    }

    // Launch Consumer 2 in another background coroutine
    val consumer2 = launch(Dispatchers.Default) {
        while (isActive) {
            val task = queue.consume()
            println("[Consumer 2] Thread ${Thread.currentThread().name} is processing: $task")
            delay(200) // Simulate processing time
        }
    }

    // Dispatch tasks (Producers)
    launch {
        queue.produce(Task("1", "Download server JSON"))
        queue.produce(Task("2", "Decode SVG asset"))
        queue.produce(Task("3", "Encrypt API access tokens"))
        queue.produce(Task("4", "Save SQLite transaction"))
        queue.produce(Task("5", "Upload app telemetry"))
    }

    // Wait and clean up
    delay(1000)
    consumer1.cancel()
    consumer2.cancel()
}
```
