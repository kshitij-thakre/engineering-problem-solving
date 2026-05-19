# Least Recently Used (LRU) Cache

This document details the design architecture, problem constraints, complexity tradeoffs, and real-world system applications of a Least Recently Used (LRU) cache.

---

## 1. Problem Statement

Design a data structure that follows the constraints of a **Least Recently Used (LRU) cache**.

Implement the `LRUCache` class:
* `LRUCache(int capacity)`: Initialize the LRU cache with positive size `capacity`.
* `int get(int key)`: Return the value of the `key` if the key exists, otherwise return `-1`.
* `void put(int key, int value)`: Update the value of the `key` if the `key` exists. Otherwise, add the `key-value` pair to the cache. If the number of keys exceeds the `capacity` from this operation, **evict** the least recently used key.

The functions `get` and `put` must run in $O(1)$ average time complexity.

---

## 2. Design Architecture: HashMap + Doubly Linked List

To achieve constant $O(1)$ time for both lookups and mutations, we combine two data structures:
1. **Hash Map (`Map<Int, Node>`)**: Provides instant $O(1)$ average time key lookups. The value in the map is a pointer reference to a Node in a Doubly Linked List.
2. **Doubly Linked List (`Node`)**: Provides constant time $O(1)$ insertions and removals. Nodes at the **head** represent the *most recently accessed* elements, while nodes at the **tail** represent the *least recently accessed* elements.

```
       [Head] <--> [Node 1] <--> [Node 2] <--> [Node 3] <--> [Tail]
          ^                                                     ^
          | (Most Recently Used)             (Least Recently Used) |
```

### Hit/Miss & Eviction Actions

* **`get(key)`**:
  * **Miss**: Key is not in the Hash Map. Return `-1`.
  * **Hit**: Key exists in the Hash Map. Find the corresponding node reference, detach it from its current position in the Doubly Linked List, and splice it directly at the **Head** (making it the most recently used). Return its value.
* **`put(key, value)`**:
  * **Key Exists**: Update the node's value and move it to the **Head**.
  * **New Key**:
    * Create a new Node, write it to the Hash Map, and insert it at the **Head**.
    * If the cache size exceeds the `capacity`, retrieve the node at the **Tail**, delete its key from the Hash Map, and remove the node from the Doubly Linked List (eviction).

---

## 3. Real-World Systems Engineering Use Cases

### 1. Operating System Page Swapping & Virtual Memory
Operating systems map virtual memory addresses to physical RAM. When physical RAM is depleted, the kernel's virtual memory manager uses an LRU replacement policy to swap out inactive memory pages (writing them to swap file disk storage) to free up physical memory frames for active processes.

### 2. Database Buffer Pools (e.g., PostgreSQL `shared_buffers`)
Relational database management systems (RDBMS) minimize expensive disk spindle reads by maintaining a dedicated memory space called a Buffer Pool. Recently read disk blocks are stored in an LRU memory buffer. Subsequent SQL queries accessing the same rows hit the buffer in memory ($O(1)$) rather than executing random disk I/O.

### 3. Mobile Client Bitmap Allocators (Coil, Glide, Kingfisher)
Mobile viewports render graphics in memory-limited sandboxes. Raw decoded bitmaps require massive heap memory footprint ($width \times height \times 4\text{ bytes}$). Image loaders maintain a RAM LRU Cache. When heap allocations hit safe limits, the least-recently-seen bitmaps are evicted and garbage collected, preventing Out-Of-Memory (OOM) crashes.

---

## 4. Complexity & Tradeoffs

* **Time Complexity:** $O(1)$ average time for both `get` and `put` operations.
* **Space Complexity:** $O(C)$ where $C$ is the cache capacity.
* **Tradeoffs:** We trade memory ($O(C)$ metadata overhead for doubly-linked node pointers and Hash Map buckets) to guarantee constant-time execution, protecting execution paths from linear list scans.

---

## 5. Visual Diagrams

Refer to the complete interactive flowchart of cache hits, misses, and list element relocations inside [cache_eviction_flow.md](./cache_eviction_flow.md).

---

## 6. Implementation References

* **Kotlin Implementation:** [`lru_cache_kotlin.kt`](./lru_cache_kotlin.kt)
* **Dart Implementation:** [`lru_cache_dart.dart`](./lru_cache_dart.dart)
