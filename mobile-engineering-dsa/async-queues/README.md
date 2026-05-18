# Mobile Engineering: Asynchronous Queues & Background Workers

## Context
When a mobile application performs operations that take time (e.g. compressing an image file, downloading a PDF, or syncing local changes to the cloud), executing them on the main thread freezes the interface and drops frames. We use **Asynchronous Queues** to schedule operations in background workers.

---

## Architectural Relevance

Refer to the complete concurrency model and thread-safe implementation inside [concurrency/README.md](../concurrency/README.md).

---

## Code Example: Bounded Task Job Queue

### Dart
```dart
import 'dart:collection';

class AsyncJobQueue {
  final Queue<Future<void> Function()> _queue = Queue();
  bool _isProcessing = false;

  void addJob(Future<void> Function() job) {
    _queue.add(job);
    _processNext();
  }

  void _processNext() async {
    if (_isProcessing || _queue.isEmpty) return;
    
    _isProcessing = true;
    final job = _queue.removeFirst();
    
    try {
      await job(); // Execute asynchronous job
    } catch (e) {
      print("Job failed: $e");
    } finally {
      _isProcessing = false;
      _processNext(); // Recurse to next job
    }
  }
}
```

### Kotlin
```kotlin
import java.util.LinkedList
import java.util.Queue
import kotlinx.coroutines.*

class AsyncJobQueue(private val scope: CoroutineScope) {
    private val queue: Queue<suspend () -> Unit> = LinkedList()
    private var isProcessing = false

    @Synchronized
    fun addJob(job: suspend () -> Unit) {
        queue.add(job)
        scope.launch {
            processNext()
        }
    }

    private suspend fun processNext() {
        val job: (suspend () -> Unit)?
        synchronized(this) {
            if (isProcessing || queue.isEmpty()) return
            isProcessing = true
            job = queue.poll()
        }

        if (job != null) {
            try {
                job.invoke()
            } catch (e: Exception) {
                println("Job failed: ${e.message}")
            } finally {
                synchronized(this) {
                    isProcessing = false
                }
                processNext()
            }
        }
    }
}
```
