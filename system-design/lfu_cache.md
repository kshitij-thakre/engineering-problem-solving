# Least Frequently Used (LFU) Cache

This document details the design architecture, problem constraints, complexity tradeoffs, and real-world system applications of a Least Frequently Used (LFU) cache.

---

## 1. Problem Statement

Design and implement a data structure for a **Least Frequently Used (LFU) cache**.

Implement the `LFUCache` class:
* `LFUCache(int capacity)`: Initializes the cache with a positive size `capacity`.
* `int get(int key)`: Gets the value of the `key` if the key exists in the cache. Otherwise, returns `-1`.
* `void put(int key, int value)`: Associates the `key` with the `value` if the `key` is not already present, or updates it if present. When the cache reaches its `capacity`, it should invalidate and evict the **least frequently used** key before inserting a new item. If there is a tie (i.e., multiple keys with the same minimum frequency), the **least recently used** key among them should be evicted.

To satisfy the performance requirement, both `get` and `put` must run in $O(1)$ average complexity.

---

## 2. Design Architecture: Dual HashMaps + Frequency Buckets

To achieve absolute constant $O(1)$ time for both lookups and eviction, a single Doubly Linked List is not sufficient because elements can have varying access frequencies. Instead, we structure our cache using:
1. **`cache` Map (`Map<Int, Node>`)**: Maps a key to its corresponding Node containing `key`, `value`, and `frequency`.
2. **`freqMap` Map (`Map<Int, DoublyLinkedList>`)**: Maps a specific frequency (e.g. 1, 2, 5) to a dedicated Doubly Linked List containing all nodes currently holding that exact frequency. Inside each frequency list, nodes behave as an **LRU Cache** (nodes at the tail represent the least recently used for that frequency tier).
3. **`minFrequency` Tracker**: An integer variable tracking the globally lowest frequency in the cache. When eviction is triggered, we instantly retrieve the tail node from the `freqMap[minFrequency]` list.

```
                  +--------------------------------+
                  |  minFrequency Tracker = 1     |
                  +---------------+----------------+
                                  |
            +---------------------+---------------------+
            |                                           |
     +------v------+                             +------v------+
     | Frequency 1 |                             | Frequency 2 |
     +------+------+                             +------+------+
            |                                           |
  [Head] <-> [Node A] <-> [Node B] <-> [Tail]    [Head] <-> [Node C] <-> [Tail]
              (MRU)        (LRU for freq 1)
```

---

## 3. Real-World Systems Engineering Use Cases

### 1. Content Delivery Network (CDN) Edge Caching
CDNs cache server responses (HTML pages, scripts, static media assets) at edge nodes located geographically close to users. Because storage at edge caches is limited, CDN proxies use LFU eviction policies to retain highly viral assets (accessed thousands of times per minute) in high-speed RAM, while rarely accessed long-tail assets are evicted to disk or downstream origin servers.

### 2. Localized Database Configurations & Dictionaries
In global application architectures, system translation databases and static regional configuration schemas are queried constantly during API request processing. Since configurations are modified rarely but queried at high frequencies, caching them in an LFU buffer prevents cache churn during bulk transactions while evicting stale records, keeping active translation keys in memory.

---

## 4. Complexity & Tradeoffs

* **Time Complexity:** $O(1)$ for both `get` and `put` operations.
* **Space Complexity:** $O(C)$ where $C$ is the cache capacity.
* **Tradeoffs:** LFU requires significant runtime metadata overhead (maintaining two maps, pointer adjustments across lists, and frequency boundaries), but guarantees constant time bounds under high read load.

---

## 5. Implementation

### Kotlin
```kotlin
class LFUCache(private val capacity: Int) {
    class Node(val key: Int, var value: Int, var freq: Int = 1) {
        var prev: Node? = null
        var next: Node? = null
    }

    class DoublyLinkedList {
        private val head = Node(0, 0)
        private val tail = Node(0, 0)
        private var size = 0

        init {
            head.next = tail
            tail.prev = head
        }

        fun addNode(node: Node) {
            node.next = head.next
            node.prev = head
            head.next?.prev = node
            head.next = node
            size++
        }

        fun removeNode(node: Node) {
            node.prev?.next = node.next
            node.next?.prev = node.prev
            size--
        }

        fun removeTail(): Node? {
            if (size == 0) return null
            val tailNode = tail.prev!!
            removeNode(tailNode)
            return tailNode
        }

        fun isEmpty(): Boolean = size == 0
    }

    private val cache = HashMap<Int, Node>()
    private val freqMap = HashMap<Int, DoublyLinkedList>()
    private var minFrequency = 0

    fun get(key: Int): Int {
        val node = cache[key] ?: return -1
        updateFrequency(node)
        return node.value
    }

    fun put(key: Int, value: Int) {
        if (capacity <= 0) return

        val node = cache[key]
        if (node != null) {
            node.value = value
            updateFrequency(node)
        } else {
            if (cache.size >= capacity) {
                val minList = freqMap[minFrequency]!!
                val evictedNode = minList.removeTail()!!
                cache.remove(evictedNode.key)
            }

            val newNode = Node(key, value)
            cache[key] = newNode
            minFrequency = 1
            val list = freqMap.computeIfAbsent(1) { DoublyLinkedList() }
            list.addNode(newNode)
        }
    }

    private fun updateFrequency(node: Node) {
        val oldFreq = node.freq
        val oldList = freqMap[oldFreq]!!
        oldList.removeNode(node)

        if (oldFreq == minFrequency && oldList.isEmpty()) {
            minFrequency++
        }

        node.freq++
        val newList = freqMap.computeIfAbsent(node.freq) { DoublyLinkedList() }
        newList.addNode(node)
    }
}
```

### Dart
```dart
class Node {
  final int key;
  int value;
  int freq;
  Node? prev;
  Node? next;

  Node(this.key, this.value, {this.freq = 1});
}

class DoublyLinkedList {
  late final Node head;
  late final Node tail;
  int _size = 0;

  DoublyLinkedList() {
    head = Node(0, 0);
    tail = Node(0, 0);
    head.next = tail;
    tail.prev = head;
  }

  void addNode(Node node) {
    node.next = head.next;
    node.prev = head;
    head.next?.prev = node;
    head.next = node;
    _size++;
  }

  void removeNode(Node node) {
    node.prev?.next = node.next;
    node.next?.prev = node.prev;
    _size--;
  }

  Node? removeTail() {
    if (_size == 0) return null;
    final Node tailNode = tail.prev!;
    removeNode(tailNode);
    return tailNode;
  }

  bool get isEmpty => _size == 0;
}

class LFUCache {
  final int capacity;
  final Map<int, Node> _cache = {};
  final Map<int, DoublyLinkedList> _freqMap = {};
  int _minFrequency = 0;

  LFUCache(this.capacity);

  int get(int key) {
    final Node? node = _cache[key];
    if (node == null) return -1;
    _updateFrequency(node);
    return node.value;
  }

  void put(int key, int value) {
    if (capacity <= 0) return;

    final Node? node = _cache[key];
    if (node != null) {
      node.value = value;
      _updateFrequency(node);
    } else {
      if (_cache.length >= capacity) {
        final DoublyLinkedList minList = _freqMap[_minFrequency]!;
        final Node evicted = minList.removeTail()!;
        _cache.remove(evicted.key);
      }

      final Node newNode = Node(key, value);
      _cache[key] = newNode;
      _minFrequency = 1;
      _freqMap.putIfAbsent(1, () => DoublyLinkedList()).addNode(newNode);
    }
  }

  void _updateFrequency(Node node) {
    final int oldFreq = node.freq;
    final DoublyLinkedList oldList = _freqMap[oldFreq]!;
    oldList.removeNode(node);

    if (oldFreq == _minFrequency && oldList.isEmpty) {
      _minFrequency++;
    }

    node.freq++;
    _freqMap.putIfAbsent(node.freq, () => DoublyLinkedList()).addNode(node);
  }
}
```
