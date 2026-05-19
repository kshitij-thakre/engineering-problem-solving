import 'dart:async';
import 'dart:collection';

/**
 * 1. Asynchronous Bounded Queue using StreamController.
 */
class AsynchronousQueue<T> {
  final int capacity;
  final ListQueue<T> _buffer = ListQueue<T>();
  final ListQueue<Completer<void>> _waitingProducers = ListQueue<Completer<void>>();
  final ListQueue<Completer<T>> _waitingConsumers = ListQueue<Completer<T>>();

  AsynchronousQueue(this.capacity);

  /// Pushes an item to the queue. Returns a Future that completes when successfully inserted.
  Future<void> produce(T item) async {
    if (_buffer.length == capacity) {
      final completer = Completer<void>();
      _waitingProducers.addLast(completer);
      await completer.future; // Suspend producer asynchronously
    }

    if (_waitingConsumers.isNotEmpty) {
      final completer = _waitingConsumers.removeFirst();
      completer.complete(item); // Deliver directly to waiting consumer
    } else {
      _buffer.addLast(item);
    }
  }

  /// Removes and returns an item. Suspends asynchronously if queue is empty.
  Future<T> consume() async {
    if (_buffer.isNotEmpty) {
      final item = _buffer.removeFirst();
      if (_waitingProducers.isNotEmpty) {
        final completer = _waitingProducers.removeFirst();
        completer.complete(); // Wake up waiting producer
      }
      return item;
    } else {
      final completer = Completer<T>();
      _waitingConsumers.addLast(completer);
      return completer.future; // Suspend consumer asynchronously
    }
  }
}

void main() async {
  final queue = AsynchronousQueue<int>(2);

  // Start Consumer Loop
  Future.microtask(() async {
    for (int i = 1; i <= 5; i++) {
      final item = await queue.consume();
      print("Consumed: $item");
    }
  });

  // Start Producer Loop
  Future.microtask(() async {
    for (int i = 1; i <= 5; i++) {
      await queue.produce(i);
      print("Produced: $i");
    }
  });
}
