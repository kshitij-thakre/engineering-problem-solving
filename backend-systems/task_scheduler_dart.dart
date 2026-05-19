class Task {
  final String id;
  final String name;
  final int priority;
  final int timestamp;

  Task(this.id, this.name, this.priority, this.timestamp);
}

/**
 * A custom binary max-heap task scheduler in Dart.
 * Operates in O(log N) schedule and execution complexity.
 */
class TaskScheduler {
  final List<Task> _heap = [];

  void schedule(Task task) {
    _heap.add(task);
    _siftUp(_heap.length - 1);
  }

  Task? executeNext() {
    if (_heap.isEmpty) return null;
    if (_heap.length == 1) return _heap.removeLast();

    final Task maxTask = _heap[0];
    _heap[0] = _heap.removeLast();
    _siftDown(0);
    return maxTask;
  }

  int get size => _heap.length;

  // ----------------------------------------------------
  // Heap Restructuring Invariant Code
  // ----------------------------------------------------

  void _siftUp(int index) {
    while (index > 0) {
      final int parentIndex = (index - 1) ~/ 2;
      if (_compare(_heap[index], _heap[parentIndex]) > 0) {
        _swap(index, parentIndex);
        index = parentIndex;
      } else {
        break;
      }
    }
  }

  void _siftDown(int index) {
    final int length = _heap.length;
    while (true) {
      int largest = index;
      final int leftChild = 2 * index + 1;
      final int rightChild = 2 * index + 2;

      if (leftChild < length && _compare(_heap[leftChild], _heap[largest]) > 0) {
        largest = leftChild;
      }
      if (rightChild < length && _compare(_heap[rightChild], _heap[largest]) > 0) {
        largest = rightChild;
      }

      if (largest != index) {
        _swap(index, largest);
        index = largest;
      } else {
        break;
      }
    }
  }

  int _compare(Task a, Task b) {
    if (a.priority != b.priority) {
      return a.priority.compareTo(b.priority);
    }
    // Older creation timestamp gets priority (ascending comparison)
    return b.timestamp.compareTo(a.timestamp);
  }

  void _swap(int i, int j) {
    final Task temp = _heap[i];
    _heap[i] = _heap[j];
    _heap[j] = temp;
  }
}

void main() {
  final scheduler = TaskScheduler();
  final int now = DateTime.now().millisecondsSinceEpoch;

  scheduler.schedule(Task("1", "Log Telemetry", 1, now));
  scheduler.schedule(Task("2", "Chat Sync", 10, now + 10));
  scheduler.schedule(Task("3", "Photo Upload", 5, now + 20));
  scheduler.schedule(Task("4", "Immediate Alert", 10, now + 30));

  print(scheduler.executeNext()?.name); // Output: Chat Sync (older timestamp than Alert)
  print(scheduler.executeNext()?.name); // Output: Immediate Alert
  print(scheduler.executeNext()?.name); // Output: Photo Upload
  print(scheduler.executeNext()?.name); // Output: Log Telemetry
}
