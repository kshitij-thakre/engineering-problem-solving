# Pattern: Dynamic Programming

## Mobile Engineering Context
**Dynamic Programming (DP)** solves complex problems by breaking them down into overlapping subproblems, solving each subproblem exactly once, and storing their results in a cache (memoization).
In mobile development, calculating text layouts (e.g. word-wrapping, paragraph breaking, and multi-font sizing calculations) requires heavy computational layout measuring. Android's dynamic layout system and Flutter's text layout engines utilize dynamic programming and memoization to speed up text drawing.

---

## Real-World Mobile Relevance

### Text Layout and Line-Breaking Engines
* **How it works**: Calculating where to break a paragraph into individual lines while minimizing empty space (Knuth-Plass Line-Breaking Algorithm) uses dynamic programming.
* **Why it matters**: Recalculating line-breaks on every single frame during scroll animations would choke the CPU. Storing intermediate layout measure calculations inside a memoization cache protects high frame-rate rendering.

---

## Code Example: Fibonacci Sequence with Memoization (Caching Subproblems)

### Dart
```dart
class FibonacciMemoizer {
  final Map<int, int> _cache = {};

  int fib(int n) {
    if (n <= 1) return n;
    
    if (_cache.containsKey(n)) {
      return _cache[n]!;
    }
    
    // Cache the subproblem result
    _cache[n] = fib(n - 1) + fib(n - 2);
    return _cache[n]!;
  }
}
```

### Kotlin
```kotlin
class FibonacciMemoizer {
    private val cache = HashMap<Int, Int>()

    fun fib(n: Int): Int {
        if (n <= 1) return n
        if (cache.containsKey(n)) {
            return cache[n]!!
        }
        
        // Cache the subproblem result
        val result = fib(n - 1) + fib(n - 2)
        cache[n] = result
        return result
    }
}
```
