/**
 * A highly optimized constant-time O(1) LRU Cache in Dart
 * using a custom Doubly Linked List and Map.
 */
class LRUCache {
  final int capacity;
  final Map<int, Node> _cache = {};
  late final Node _head;
  late final Node _tail;

  LRUCache(this.capacity) {
    _head = Node(0, 0); // Dummy Head sentinel
    _tail = Node(0, 0); // Dummy Tail sentinel
    _head.next = _tail;
    _tail.prev = _head;
  }

  /**
   * Fetches the value for [key], moving the target Node to Head.
   */
  int get(int key) {
    final Node? node = _cache[key];
    if (node == null) return -1;
    _moveToHead(node);
    return node.value;
  }

  /**
   * Sets or updates [key] with [value]. Handles tail node eviction.
   */
  void put(int key, int value) {
    final Node? node = _cache[key];
    if (node != null) {
      node.value = value;
      _moveToHead(node);
    } else {
      final Node newNode = Node(key, value);
      _cache[key] = newNode;
      _addNode(newNode);

      if (_cache.length > capacity) {
        // Evict least recently used (node right before tail)
        final Node evicted = _popTail();
        _cache.remove(evicted.key);
      }
    }
  }

  // ----------------------------------------------------
  // Doubly Linked List Operations
  // ----------------------------------------------------

  void _addNode(Node node) {
    node.prev = _head;
    node.next = _head.next;

    _head.next?.prev = node;
    _head.next = node;
  }

  void _removeNode(Node node) {
    final Node? prev = node.prev;
    final Node? next = node.next;

    prev?.next = next;
    next?.prev = prev;
  }

  void _moveToHead(Node node) {
    _removeNode(node);
    _addNode(node);
  }

  Node _popTail() {
    final Node res = _tail.prev!;
    _removeNode(res);
    return res;
  }
}

class Node {
  int key;
  int value;
  Node? prev;
  Node? next;

  Node(this.key, this.value);
}

void main() {
  final cache = LRUCache(2);
  cache.put(1, 1);
  cache.put(2, 2);
  print(cache.get(1));    // Output: 1
  cache.put(3, 3);        // Evicts key 2
  print(cache.get(2));    // Output: -1 (Not found)
  cache.put(4, 4);        // Evicts key 1
  print(cache.get(1));    // Output: -1 (Not found)
  print(cache.get(3));    // Output: 3
  print(cache.get(4));    // Output: 4
}
