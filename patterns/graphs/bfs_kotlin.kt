package patterns.graphs

import java.util.LinkedList
import java.util.Queue

/**
 * BFS navigation resolver. Calculates the exact sequence of screens
 * to traverse from a start view to a destination view.
 */
class NavigationGraph {
    private val adjList = HashMap<String, MutableList<String>>()

    fun addScreen(screen: String) {
        adjList.putIfAbsent(screen, ArrayList())
    }

    fun addRoute(from: String, to: String) {
        addScreen(from)
        addScreen(to)
        adjList[from]?.add(to)
    }

    /**
     * Resolves the shortest path sequence between two screens.
     * Returns the ordered list of screen transitions, or empty if unreachable.
     */
    fun findShortestRoute(start: String, end: String): List<String> {
        if (!adjList.containsKey(start) || !adjList.containsKey(end)) {
            return emptyList()
        }

        val queue: Queue<String> = LinkedList()
        val visited = HashSet<String>()
        val parentMap = HashMap<String, String>()

        queue.add(start)
        visited.add(start)

        var routeFound = false
        while (!queue.isEmpty()) {
            val current = queue.poll()
            if (current == end) {
                routeFound = true
                break
            }

            val neighbors = adjList[current] ?: continue
            for (neighbor in neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    parentMap[neighbor] = current
                    queue.add(neighbor)
                }
            }
        }

        if (!routeFound) return emptyList()

        // Reconstruct path backtracking from end to start
        val path = LinkedList<String>()
        var step = end
        path.addFirst(step)
        while (parentMap.containsKey(step)) {
            step = parentMap[step]!!
            path.addFirst(step)
        }
        return path
    }
}

fun main() {
    val nav = NavigationGraph()
    nav.addRoute("Home", "Details")
    nav.addRoute("Home", "Settings")
    nav.addRoute("Details", "Cart")
    nav.addRoute("Cart", "Checkout")
    nav.addRoute("Settings", "Profile")

    val route = nav.findShortestRoute("Home", "Checkout")
    println(route) // Output: [Home, Details, Cart, Checkout]
}
