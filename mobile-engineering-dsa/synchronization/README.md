# Mobile Engineering: Synchronization & Resource Locking

## Context
When multiple parallel background threads or asynchronous processes access shared resources (e.g. mutating a local array list, caching session credentials, or making concurrent updates to a local SQLite database), race conditions can corrupt data. Mobile platforms implement synchronization primitives to protect shared resources.

---

## Architectural Relevance

Refer to the complete thread-safety comparison inside [kotlin-runtime/README.md](../../kotlin-runtime/README.md).

---

## Code Example: Custom Mutex/Locking Mechanism

### Dart (Asynchronous Lock)
Because Dart is single-threaded, standard JVM locks like `synchronized` are not needed. Instead, we write a **Future-based Mutex** to serialize asynchronous actions sequentially.

```dart
import 'dart:async';

class AsyncMutex {
  Future<void> _lastTask = Future.value();

  // Protects a block: runs the async operation sequentially
  Future<T> lock<T>(Future<T> Function() criticalSection) {
    final completer = Completer<T>();
    
    // Chain onto the last task
    _lastTask = _lastTask.then((_) async {
      try {
        final result = await criticalSection();
        completer.complete(result);
      } catch (e) {
        completer.completeError(e);
      }
    });

    return completer.future;
  }
}
```

### Kotlin (Coroutine Mutex)
In Kotlin, we avoid blocking OS threads by using a non-blocking `Mutex`:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConcurrentDatabaseProtector {
    private val mutex = Mutex()
    private var recordsCounter = 0

    suspend fun secureIncrement() {
        // Suspends coroutine instead of blocking JVM threads
        mutex.withLock {
            recordsCounter++
        }
    }
}
```
