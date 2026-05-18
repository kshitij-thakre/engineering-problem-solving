# Least Recently Used (LRU) Cache

## 1. Problem Statement
Design a data structure that follows the constraints of a **Least Recently Used (LRU) cache**.

Implement the `LRUCache` class:
* `LRUCache(int capacity)`: Initialize the LRU cache with positive size `capacity`.
* `int get(int key)`: Return the value of the `key` if the key exists, otherwise return `-1`.
* `void put(int key, int value)`: Update the value of the `key` if the `key` exists. Otherwise, add the `key-value` pair to the cache. If the number of keys exceeds the `capacity` from this operation, **evict** the least recently used key.

The functions `get` and `put` must run in $O(1)$ average time complexity.

---

## 2. Design Architecture: HashMap + Doubly Linked List

To achieve absolute constant $O(1)$ time for both lookups and mutations, we combine two data structures:
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

## 3. Real-World Mobile Engineering Use Cases

### 1. Image Buffering & Bitmap Allocator Caches (Glide, Coil, Kingfisher)
* Raw bitmaps are the primary source of Out-Of-Memory (OOM) crashes in mobile apps. An image loader library retains decoded bitmaps in memory using an **LRU Cache** bound to the device's heap limits (e.g. allocating 15% of available RAM). When memory boundaries are reached, older off-screen bitmaps are evicted and collected, allowing active scrolling lists to render seamlessly.

### 2. HTTP Network API Response Caching
* To minimize dynamic cellular data consumption and support offline load states, the HTTP client caches response payloads locally using an LRU cache with ETag checks, evicting the least accessed offline payloads.

---

## 4. Complexity & Tradeoffs

* **Time Complexity:** $O(1)$ average time for both `get` and `put` methods. Hash Map lookups and Doubly Linked List pointer updates are constant-time operations.
* **Space Complexity:** $O(C)$ where $C$ is the cache capacity. The maximum memory consumed is directly proportional to the defined capacity limit, which offers predictable heap allocation bounds.
* **Tradeoffs:** We trade memory ($O(C)$ storage for both the HashMap and Doubly Linked List structure) to guarantee sub-millisecond, execution-safe lookups.

---

## 5. Visual Diagrams

Refer to the complete interactive flowchart of cache hits, misses, and list element relocations inside [cache_eviction_flow.md](./cache_eviction_flow.md).

---

## 6. Implementation References

* **Kotlin Implementation:** [`lru_cache_kotlin.kt`](./lru_cache_kotlin.kt)
* **Dart Implementation:** [`lru_cache_dart.dart`](./lru_cache_dart.dart)
