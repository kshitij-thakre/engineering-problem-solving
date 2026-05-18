# Dart Event Queue & Isolate Concurrency

## 1. The Single Isolate Thread Model
Dart is a single-threaded runtime. However, it handles high-volume asynchronous operations (such as making HTTP requests, reading local files, or listening to user keystrokes) using an **Event Loop**.

All Dart code executes inside an **Isolate**. Each isolate contains its own private heap memory and a single execution thread, completely eliminating shared-memory synchronization locks or deadlocks.

```mermaid
graph TD
    subgraph Isolate ["Dart Isolate Sandbox"]
        Heap[Heap Memory]
        Thread[Thread of Execution]
        
        subgraph Loops ["The Event Loop"]
            Micro[Microtask Queue (High Priority)]
            Events[Event Queue (Standard Priority)]
        end
    end

    Thread --> Loops
    Micro --> Thread
    Events --> Thread
```

---

## 2. Event Precedence Rules

The Event Loop continuously runs, pulling tasks from two queues:
1. **Microtask Queue**: Stores critical internal operations (such as Future completion callbacks). It is given absolute priority.
2. **Event Queue**: Stores external inputs (such as I/O events, gestures, paint triggers, and timer events).

**The Precedence Law**: The Dart engine will empty the *entire* Microtask Queue before pulling a single item from the Event Queue. If a microtask schedules another microtask, the Event Queue is starved, causing the application UI to freeze.

---

## 3. Parallelism using Isolate Ports

For extremely heavy computational tasks (like image resizing, large database imports, or complex JSON serialization), executing them on the main isolate will block the rendering loop, causing visual stutter.

To achieve parallel execution, Dart spawns secondary **Isolates**:
* Isolates do not share heap memory.
* Isolates communicate strictly by passing messages over **Ports** (`SendPort` / `ReceivePort`).

### Isolate Communication Blueprint
```dart
import 'dart:isolate';

void heavyWorker(SendPort mainSendPort) {
  final workerReceivePort = ReceivePort();
  // Send the worker's port back to the main isolate
  mainSendPort.send(workerReceivePort.sendPort);

  workerReceivePort.listen((message) {
    // Process heavy computation
    final result = message * 2; 
    mainSendPort.send(result); // Return result
  });
}
```
