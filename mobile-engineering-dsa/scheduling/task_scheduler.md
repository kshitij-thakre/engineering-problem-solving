# Priority-Based Task Scheduler

## 1. Problem Statement
Design a **Priority-Based Task Scheduler** that handles incoming tasks containing distinct execution priorities.

Implement the `TaskScheduler` class:
* `void schedule(Task task)`: Adds a Task to the scheduler. A Task has `id`, `name`, `priority` (integer, higher means more urgent), and `timestamp`.
* `Task executeNext()`: Extracts and returns the Task holding the highest priority. If priorities are identical, the task with the older creation timestamp is executed first. Returns `null` if no tasks exist.

The functions must operate in $O(\log N)$ or $O(1)$ complexity.

---

## 2. Design Architecture: Binary Heap (Priority Queue)

To satisfy logarithmic time insertion and deletion, we build a **Binary Max-Heap**:
1. **Array-Backed Binary Tree**: We represent our tree structure inside a flat Array, where for any index `i`:
   * Left Child is located at: `2 * i + 1`
   * Right Child is located at: `2 * i + 2`
   * Parent is located at: `(i - 1) / 2`
2. **Priority Heap Invariant**: A parent node must always hold a higher priority value than its child nodes.
3. **Operations**:
   * **`schedule(task)`**: Append the task to the end of the array, and run **`siftUp`** (bubble up) until the priority heap invariant is satisfied. Runtime: $O(\log N)$.
   * **`executeNext()`**: Swap the root element (maximum priority) with the last element of the array, pop the last element, and run **`siftDown`** (bubble down) from the root. Runtime: $O(\log N)$.

```
               [Priority 10: Sync Chats]
              /                         \
    [Priority 5: Photo Upload]       [Priority 1: Diagnostic Logs]
```

---

## 3. Real-World Mobile Engineering Use Cases

### 1. Bounded Network Upload Queue & Asset Uploaders
* Mobile apps execute multiple asynchronous network uploads. When a user sends chat messages, uploads photos, and flushes diagnostics logs simultaneously, the app schedules them inside a Max-Heap Priority Queue. Chat messages (priority 10) are pushed to the front and sent instantly. Large photos (priority 5) are deferred, and logs (priority 1) are held until the queue is clear, preserving cellular bandwidth.

### 2. Backoff Network Retry Managers
* If API calls fail due to network volatility, the scheduler registers retry tasks with scheduled backoff timestamps, popping the next due task efficiently.

---

## 4. Complexity & Tradeoffs

* **Time Complexity:** $O(\log N)$ for both `schedule` and `executeNext` operations. Finding the highest priority task takes $O(1)$ time (reading the root index).
* **Space Complexity:** $O(N)$ memory to store tasks inside the Heap list.
* **Tradeoffs:** Binary Heaps do not offer fast key lookups ($O(N)$ scan time). If dynamic key priority modification is required (e.g. upgrading a task's priority on the fly), we must maintain an auxiliary HashMap mapping task IDs to heap array indices, keeping modifications in logarithmic time.

---

## 5. Implementation References

* **Kotlin Implementation:** [`task_scheduler_kotlin.kt`](./task_scheduler_kotlin.kt)
* **Dart Implementation:** [`task_scheduler_dart.dart`](./task_scheduler_dart.dart)
