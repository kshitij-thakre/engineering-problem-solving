# Graph Algorithms: BFS, DFS, and Topological Sort

## Pattern
**Graph Traversal & Topological Ordering**

---

## Problem
Design and implement:
1. **Breadth-First Search (BFS)**: Traverse a graph level-by-level (shortest path).
2. **Depth-First Search (DFS)**: Traverse a graph by going deep along each branch before backtracking.
3. **Topological Sort**: Order the vertices of a Directed Acyclic Graph (DAG) linearly such that for every directed edge $u \to v$, vertex $u$ comes before $v$.

---

## Approach

### 1. BFS (Breadth-First Search)
* Uses a **Queue** (FIFO).
* Mark node as visited, enqueue. While the queue is not empty, dequeue a node, explore all its unvisited neighbors, mark them visited, and enqueue them.

### 2. DFS (Depth-First Search)
* Uses recursion (implicit call stack) or an explicit **Stack** (LIFO).
* Visit the current node, mark it as visited, and recursively call DFS for all its unvisited neighbors.

### 3. Topological Sort (Kahn's Algorithm)
* Uses **In-degrees** (number of incoming edges to a node).
* Calculate in-degree for all vertices.
* Enqueue all vertices with in-degree 0 (no dependencies).
* While the queue is not empty:
  * Dequeue vertex $u$, add it to the topological ordering.
  * For each neighbor $v$ of $u$, decrement its in-degree by 1.
  * If in-degree of $v$ becomes 0, enqueue $v$.
* If the size of the topological ordering is less than $V$, the graph contains a cycle (cannot be sorted).

```mermaid
graph TD
    subgraph DependencyGraph [Topological Sort: Resolving Dependencies]
        Database[Database Module] --> Network[Network Client]
        UserRepository[User Repository] --> Database
        UserRepository --> Network
        AuthViewModel[Auth ViewModel] --> UserRepository
    end
    
    subgraph ExecutionOrder [Kahn's Algorithm Resolution Order]
        O1[Network Client / In-Degree 0] --> O2[Database Module / In-Degree 0 after Network Client]
        O2 --> O3[User Repository]
        O3 --> O4[Auth ViewModel]
    end
```

---

## Time Complexity
* **BFS / DFS**: **$O(V + E)$** where $V$ is the number of vertices and $E$ is the number of edges.
* **Topological Sort (Kahn's)**: **$O(V + E)$** as each node and edge is processed exactly once.

## Space Complexity
* **BFS / DFS**: **$O(V)$** to store visited tracking maps and queue/stack frames.
* **Topological Sort**: **$O(V)$** to store in-degrees and queue nodes.

---

## Why This Solution Works
Kahn's Algorithm simulates structural dependency resolution. By consistently executing nodes with zero dependencies, it guarantees that dependent nodes are only executed after all their precursors have completed.

---

## Mobile Engineering Relevance
Graphs are highly prevalent in the architecture of modularized modern mobile apps.
* **Dependency Injection (DI) Engines**: Frameworks like Dagger/Hilt (Kotlin) and GetIt/Injectable (Flutter/Dart) construct dependency graphs. When the app launches, DI engines run a Topological Sort to initialize instances (e.g. `NetworkClient` $\to$ `Database` $\to$ `Repository` $\to$ `ViewModel`) in correct order. Cycle detection warns developers of circular dependencies (e.g., A depends on B, B depends on A), which would crash the runtime.
* **Navigation & Deep Linking**: Mobile application pages and screen routing configurations act as a graph. Resolving nested deep-links (e.g., opening a chat details screen inside a specific group inside a workspace tab) is solved using BFS to find the shortest routing traversal path.
* **Asset Prefetching & Download Managers**: In offline-first systems, syncing content (like downloading offline courses where Module 1 must be downloaded before Module 2) uses graph traversal to sequence network download pipelines.

---

## Tradeoffs
* **Recursion vs. Iteration in DFS**: On Android (Kotlin JVM), deep recursion can lead to `StackOverflowError` if the graph depth is extremely large. In those rare edge cases, an iterative DFS using a custom Heap Stack is preferred. In Dart, stack limits are generous, but iterative approaches are still useful for resource-restricted operations.

---

## Code Solution

### Dart
```dart
import 'dart:collection';

class Graph {
  final int vertices;
  final Map<int, List<int>> adjacencyList = {};

  Graph(this.vertices) {
    for (int i = 0; i < vertices; i++) {
      adjacencyList[i] = [];
    }
  }

  void addEdge(int source, int destination) {
    adjacencyList[source]?.add(destination);
  }

  // 1. Breadth-First Search (BFS)
  List<int> bfs(int startVertex) {
    List<int> result = [];
    Set<int> visited = {};
    Queue<int> queue = Queue();

    visited.add(startVertex);
    queue.add(startVertex);

    while (queue.isNotEmpty) {
      int current = queue.removeFirst();
      result.add(current);

      for (int neighbor in adjacencyList[current] ?? []) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          queue.add(neighbor);
        }
      }
    }
    return result;
  }

  // 2. Depth-First Search (DFS)
  List<int> dfs(int startVertex) {
    List<int> result = [];
    Set<int> visited = {};
    _dfsHelper(startVertex, visited, result);
    return result;
  }

  void _dfsHelper(int vertex, Set<int> visited, List<int> result) {
    visited.add(vertex);
    result.add(vertex);

    for (int neighbor in adjacencyList[vertex] ?? []) {
      if (!visited.contains(neighbor)) {
        _dfsHelper(neighbor, visited, result);
      }
    }
  }

  // 3. Topological Sort (Kahn's Algorithm)
  List<int> topologicalSort() {
    Map<int, int> inDegree = {for (var i = 0; i < vertices; i++) i: 0};

    // Step 1: Calculate In-degrees
    for (int u in adjacencyList.keys) {
      for (int v in adjacencyList[u] ?? []) {
        inDegree[v] = (inDegree[v] ?? 0) + 1;
      }
    }

    // Step 2: Enqueue vertices with in-degree 0
    Queue<int> queue = Queue();
    inDegree.forEach((vertex, degree) {
      if (degree == 0) queue.add(vertex);
    });

    List<int> order = [];

    // Step 3: Process queue
    while (queue.isNotEmpty) {
      int u = queue.removeFirst();
      order.add(u);

      for (int v in adjacencyList[u] ?? []) {
        inDegree[v] = (inDegree[v] ?? 0) - 1;
        if (inDegree[v] == 0) {
          queue.add(v);
        }
      }
    }

    if (order.length != vertices) {
      throw Exception("Dependency Graph has circular dependencies (Cycle detected)!");
    }

    return order;
  }
}

void main() {
  print("=== Dart Graph Traversals ===");
  final graph = Graph(6);
  
  // Model Dependency graph: DI initialization
  // 5 and 4 are basic leaf services (e.g. AuthClient, HTTPClient)
  // 2 depends on 5. 0 depends on 4, 2. 1 depends on 4. 3 depends on 1, 2.
  graph.addEdge(5, 2);
  graph.addEdge(5, 0);
  graph.addEdge(4, 0);
  graph.addEdge(4, 1);
  graph.addEdge(2, 3);
  graph.addEdge(3, 1);

  print("BFS (from 5): ${graph.bfs(5)}");
  print("DFS (from 5): ${graph.dfs(5)}");
  print("Topological Sort Order: ${graph.topologicalSort()}");
}
```

### Kotlin
```kotlin
import java.util.LinkedList
import java.util.Queue

class Graph(val vertices: Int) {
    private val adjacencyList: MutableMap<Int, MutableList<Int>> = HashMap()

    init {
        for (i in 0 until vertices) {
            adjacencyList[i] = ArrayList()
        }
    }

    fun addEdge(source: Int, destination: Int) {
        adjacencyList[source]?.add(destination)
    }

    // 1. Breadth-First Search (BFS)
    fun bfs(startVertex: Int): List<Int> {
        val result = ArrayList<Int>()
        val visited = HashSet<Int>()
        val queue: Queue<Int> = LinkedList()

        visited.add(startVertex)
        queue.add(startVertex)

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            result.add(current)

            for (neighbor in adjacencyList[current] ?: emptyList()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }
        return result
    }

    // 2. Depth-First Search (DFS)
    fun dfs(startVertex: Int): List<Int> {
        val result = ArrayList<Int>()
        val visited = HashSet<Int>()
        dfsHelper(startVertex, visited, result)
        return result
    }

    private fun dfsHelper(vertex: Int, visited: HashSet<Int>, result: ArrayList<Int>) {
        visited.add(vertex)
        result.add(vertex)

        for (neighbor in adjacencyList[vertex] ?: emptyList()) {
            if (!visited.contains(neighbor)) {
                dfsHelper(neighbor, visited, result)
            }
        }
    }

    // 3. Topological Sort (Kahn's Algorithm)
    fun topologicalSort(): List<Int> {
        val inDegree = IntArray(vertices)

        // Step 1: Calculate In-degrees
        for (u in 0 until vertices) {
            for (v in adjacencyList[u] ?: emptyList()) {
                inDegree[v]++
            }
        }

        // Step 2: Enqueue vertices with in-degree 0
        val queue: Queue<Int> = LinkedList()
        for (i in 0 until vertices) {
            if (inDegree[i] == 0) {
                queue.add(i)
            }
        }

        val order = ArrayList<Int>()

        // Step 3: Process queue
        while (queue.isNotEmpty()) {
            val u = queue.poll()
            order.add(u)

            for (v in adjacencyList[u] ?: emptyList()) {
                inDegree[v]--
                if (inDegree[v] == 0) {
                    queue.add(v)
                }
            }
        }

        if (order.size != vertices) {
            throw IllegalStateException("Dependency Graph has circular dependencies (Cycle detected)!")
        }

        return order
    }
}

fun main() {
    println("=== Kotlin Graph Traversals ===")
    val graph = Graph(6)
    
    // Model dependency tree
    graph.addEdge(5, 2)
    graph.addEdge(5, 0)
    graph.addEdge(4, 0)
    graph.addEdge(4, 1)
    graph.addEdge(2, 3)
    graph.addEdge(3, 1)

    println("BFS (from 5): ${graph.bfs(5)}")
    println("DFS (from 5): ${graph.dfs(5)}")
    println("Topological Sort Order: ${graph.topologicalSort()}")
}
```
