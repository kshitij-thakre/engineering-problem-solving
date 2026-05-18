package mobile_engineering_dsa.scheduling

import java.util.PriorityQueue

data class Task(
    val id: String,
    val name: String,
    val priority: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class TaskScheduler {
    // PriorityQueue comparison logic:
    // 1. Higher priority first (descending).
    // 2. If priorities match, older timestamp first (ascending).
    private val heap = PriorityQueue<Task> { a, b ->
        if (a.priority != b.priority) {
            b.priority.compareTo(a.priority)
        } else {
            a.timestamp.compareTo(b.timestamp)
        }
    }

    @Synchronized
    fun schedule(task: Task) {
        heap.offer(task)
    }

    @Synchronized
    fun executeNext(): Task? {
        return heap.poll()
    }

    val size: Int
        @Synchronized get() = heap.size
}

fun main() {
    val scheduler = TaskScheduler()
    scheduler.schedule(Task("1", "Log Telemetry", 1))
    scheduler.schedule(Task("2", "Chat Sync", 10))
    scheduler.schedule(Task("3", "Photo Upload", 5))
    scheduler.schedule(Task("4", "Immediate Alert", 10)) // Same priority as Chat Sync

    // Chat Sync has the same priority as Immediate Alert, but was scheduled first (older timestamp)
    println(scheduler.executeNext()?.name) // Output: Chat Sync
    println(scheduler.executeNext()?.name) // Output: Immediate Alert
    println(scheduler.executeNext()?.name) // Output: Photo Upload
    println(scheduler.executeNext()?.name) // Output: Log Telemetry
}
