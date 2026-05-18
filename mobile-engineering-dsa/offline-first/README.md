# Mobile Engineering: Offline-First Synchronization

## Context
Offline-first synchronization ensures that the mobile application remains functional and mutatable even during network disconnection (e.g., flight mode, cell dead zones). All modifications are performed locally first and merged with the cloud when connections return.

---

## Architectural Relevance

Refer to the complete synchronization system and conflict resolution blueprints inside [system-design-mobile/offline-sync-engine.md](../../system-design-mobile/offline-sync-engine.md).

---

## Code Example: Local Outbox Task Dispatcher

This models a local task outbox queue that processes mutations when the network connection is established.

### Dart
```dart
class OutboxTask {
  final String id;
  final String action; // e.g. "CREATE_POST", "LIKE_COMMENT"
  final String payload;
  bool isSyncing = false;

  OutboxTask(this.id, this.action, this.payload);
}

class OutboxSyncEngine {
  final List<OutboxTask> _outbox = [];

  void addMutation(String action, String payload) {
    final task = OutboxTask(DateTime.now().toIso8601String(), action, payload);
    _outbox.add(task);
    triggerSync();
  }

  Future<void> triggerSync() async {
    for (var task in _outbox) {
      if (task.isSyncing) continue;
      
      task.isSyncing = true;
      try {
        await _networkPost(task.action, task.payload);
        // Remove task on successful sync
        _outbox.removeWhere((t) => t.id == task.id);
        break; // Break loop to avoid concurrent modification issues, call triggerSync again
      } catch (e) {
        task.isSyncing = false;
        print("Failed to sync task: ${task.id}, retrying later...");
        break; // Network failed, pause queue processing
      }
    }
  }

  Future<void> _networkPost(String action, String payload) async {
    // Simulated network delay
    await Future.delayed(Duration(milliseconds: 200));
  }
}
```

### Kotlin
```kotlin
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.delay

class OutboxTask(
    val id: String,
    val action: String,
    val payload: String,
    var isSyncing: Boolean = false
)

class OutboxSyncEngine {
    private val outbox = CopyOnWriteArrayList<OutboxTask>()

    fun addMutation(action: String, payload: String) {
        val task = OutboxTask(System.currentTimeMillis().toString(), action, payload)
        outbox.add(task)
    }

    suspend fun triggerSync() {
        for (task in outbox) {
            if (task.isSyncing) continue
            task.isSyncing = true
            try {
                networkPost(task.action, task.payload)
                outbox.remove(task)
            } catch (e: Exception) {
                task.isSyncing = false
                println("Failed to sync task: ${task.id}, pausing sync queue...")
                break // Network failed, pause queue
            }
        }
    }

    private suspend fun networkPost(action: String, payload: String) {
        delay(200) // Simulated delay
    }
}
```
