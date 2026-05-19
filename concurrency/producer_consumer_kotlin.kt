package mobile_engineering_dsa.concurrency

import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

/**
 * 1. Classical Thread-Safe Bounded Queue using ReentrantLock and Condition Variables.
 */
class ThreadSafeQueue<T>(private val capacity: Int) {
    private val list = LinkedList<T>()
    private val lock = ReentrantLock()
    private val notFull = lock.newCondition()
    private val notEmpty = lock.newCondition()

    fun produce(item: T) {
        lock.lock()
        try {
            while (list.size == capacity) {
                // Buffer is full; block producing thread until consumed
                notFull.await()
            }
            list.add(item)
            // Signal consuming threads that elements are ready
            notEmpty.signalAll()
        } finally {
            lock.unlock()
        }
    }

    fun consume(): T {
        lock.lock()
        try {
            while (list.isEmpty()) {
                // Buffer is empty; block consuming thread until produced
                notEmpty.await()
            }
            val item = list.removeFirst()
            // Signal producing threads that space has opened
            notFull.signalAll()
            return item
        } finally {
            lock.unlock()
        }
    }
}

/**
 * 2. Modern Coroutine Bounded Channel (Non-blocking Suspend version).
 */
class CoroutineQueue<T>(capacity: Int) {
    private val channel = Channel<T>(capacity)

    suspend fun produce(item: T) {
        channel.send(item) // Suspends coroutine when full, releases OS thread
    }

    suspend fun consume(): T {
        return channel.receive() // Suspends coroutine when empty, releases OS thread
    }
}

fun main() = runBlocking {
    val queue = CoroutineQueue<Int>(2)

    // Launch consumer coroutine
    launch {
        for (i in 1..5) {
            val item = queue.consume()
            println("Consumed: $item")
        }
    }

    // Launch producer coroutine
    launch {
        for (i in 1..5) {
            queue.produce(i)
            println("Produced: $i")
        }
    }
}
