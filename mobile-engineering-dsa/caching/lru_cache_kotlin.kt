package mobile_engineering_dsa.caching

/**
 * A highly optimized, constant-time O(1) LRU Cache implementation
 * using a custom Doubly Linked List and HashMap.
 */
class LRUCache(private val capacity: Int) {

    // Node representation in the Doubly Linked List
    class Node(var key: Int, var value: Int) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val cache = HashMap<Int, Node>()
    private val head = Node(0, 0) // Dummy Head sentinel
    private val tail = Node(0, 0) // Dummy Tail sentinel

    init {
        head.next = tail
        tail.prev = head
    }

    /**
     * Retrieves key value, shifting corresponding node to Head (MRU).
     */
    fun get(key: Int): Int {
        val node = cache[key] ?: return -1
        moveToHead(node)
        return node.value
    }

    /**
     * Inserts or updates key. Handles eviction of Tail node when capacity exceeded.
     */
    fun put(key: Int, value: Int) {
        val node = cache[key]
        if (node != null) {
            node.value = value
            moveToHead(node)
        } else {
            val newNode = Node(key, value)
            cache[key] = newNode
            addNode(newNode)

            if (cache.size > capacity) {
                // Evict the least recently used element (immediately preceding tail)
                val tailNode = popTail()
                cache.remove(tailNode.key)
            }
        }
    }

    // ----------------------------------------------------
    // Doubly Linked List Structural Operations
    // ----------------------------------------------------

    private fun addNode(node: Node) {
        // Always insert directly after the dummy head sentinel
        node.prev = head
        node.next = head.next

        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node) {
        val prev = node.prev
        val next = node.next

        prev?.next = next
        next?.prev = prev
    }

    private fun moveToHead(node: Node) {
        removeNode(node)
        addNode(node)
    }

    private fun popTail(): Node {
        val res = tail.prev!!
        removeNode(res)
        return res
    }
}

fun main() {
    val cache = LRUCache(2)
    cache.put(1, 1)
    cache.put(2, 2)
    println(cache.get(1))    // Output: 1
    cache.put(3, 3)          // Evicts key 2
    println(cache.get(2))    // Output: -1 (Not found)
    cache.put(4, 4)          // Evicts key 1
    println(cache.get(1))    // Output: -1 (Not found)
    println(cache.get(3))    // Output: 3
    println(cache.get(4))    // Output: 4
}
