# Mobile Engineering: Memory Management & GC Optimization

## Context
Mobile devices have limited physical RAM, and their execution engines run in dynamic heaps where objects are allocated and garbage-collected. Running heavy operations that trigger excessive allocations (like regex-replacements inside keystroke listeners or retaining massive bitmaps in cache memory) will trigger heavy Garbage Collection (GC) sweeps, freezing the UI thread and dropping frames.

---

## Architectural Relevance

Refer to the complete memory management and leak prevention blueprints inside [interview-notes/memory-management.md](../../interview-notes/memory-management.md).

---

## Code Example: Object Pool Pattern to Avoid GC Pressure

An **Object Pool** recycles expensive object instances rather than constantly creating and discarding them, completely bypassing the Garbage Collector.

### Dart
```dart
class ExpensivePayload {
  List<int> data = List.filled(1000, 0);

  void reset() {
    data.fillRange(0, data.length, 0);
  }
}

class PayloadPool {
  final List<ExpensivePayload> _pool = [];

  ExpensivePayload acquire() {
    if (_pool.isEmpty) {
      return ExpensivePayload();
    }
    return _pool.removeLast();
  }

  void release(ExpensivePayload payload) {
    payload.reset();
    _pool.add(payload); // Recycle instance
  }
}
```

### Kotlin
```kotlin
import java.util.Stack

class ExpensivePayload {
    val data = IntArray(1000)

    fun reset() {
        data.fill(0)
    }
}

class PayloadPool {
    private val pool = Stack<ExpensivePayload>()

    @Synchronized
    fun acquire(): ExpensivePayload {
        return if (pool.isEmpty()) {
            ExpensivePayload()
        } else {
            pool.pop()
        }
    }

    @Synchronized
    fun release(payload: ExpensivePayload) {
        payload.reset()
        pool.push(payload) // Recycle instance
    }
}
```
