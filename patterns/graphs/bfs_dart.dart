import 'dart:collection';

/**
 * BFS navigation resolver. Calculates shortest transition sequence
 * between screens in Dart.
 */
class NavigationGraph {
  final Map<String, List<String>> _adjList = {};

  void addScreen(String screen) {
    _adjList.putIfAbsent(screen, () => []);
  }

  void addRoute(String from, String to) {
    addScreen(from);
    addScreen(to);
    _adjList[from]?.add(to);
  }

  /// Resolves shortest route sequence. Returns ordered list of views.
  List<String> findShortestRoute(String start, String end) {
    if (!_adjList.containsKey(start) || !_adjList.containsKey(end)) {
      return [];
    }

    final ListQueue<String> queue = ListQueue<String>();
    final Set<String> visited = {};
    final Map<String, String> parentMap = {};

    queue.add(start);
    visited.add(start);

    bool routeFound = false;
    while (queue.isNotEmpty) {
      final String current = queue.removeFirst();
      if (current == end) {
        routeFound = true;
        break;
      }

      final neighbors = _adjList[current] ?? [];
      for (final String neighbor in neighbors) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          parentMap[neighbor] = current;
          queue.add(neighbor);
        }
      }
    }

    if (!routeFound) return [];

    // Reconstruct path
    final ListQueue<String> path = ListQueue<String>();
    String step = end;
    path.addFirst(step);
    while (parentMap.containsKey(step)) {
      step = parentMap[step]!;
      path.addFirst(step);
    }
    return path.toList();
  }
}

void main() {
  final nav = NavigationGraph();
  nav.addRoute("Home", "Details");
  nav.addRoute("Home", "Settings");
  nav.addRoute("Details", "Cart");
  nav.addRoute("Cart", "Checkout");
  nav.addRoute("Settings", "Profile");

  final route = nav.findShortestRoute("Home", "Checkout");
  print(route); // Output: [Home, Details, Cart, Checkout]
}
