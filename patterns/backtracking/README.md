# Pattern: Backtracking

## Mobile Engineering Context
**Backtracking** is a systematic method for finding solutions to search problems by building candidates incrementally and abandoning ("backtracking") any candidate that fails to satisfy the constraints.
In mobile architectures, backtracking is used by advanced navigation routers (reconstructing state paths and matching deep-links to dynamic screen sub-graphs) and layout systems (placing widgets dynamically within complex, size-elastic grid views).

---

## Real-World Mobile Relevance

### 1. Dynamic Layout Grid Placement
* **How it works**: Calculating the optimal visual arrangement of variable-sized widgets inside a grid where widgets can wrap and span multiple columns. The engine tries a placement configuration; if a subsequent widget fails to fit, it backtracks and tries an alternative width/height combination.

### 2. Navigation State Reconstruction
* **How it works**: Rebuilding the app navigation backstack when resolving deep links to deep sub-views.

---

## Code Example: Standard Backtracking Template (Permutations Generation)

### Dart
```dart
class PermutationGenerator {
  List<List<int>> generate(List<int> nums) {
    List<List<int>> results = [];
    _backtrack(nums, [], Set<int>(), results);
    return results;
  }

  void _backtrack(List<int> nums, List<int> currentPath, Set<int> visited, List<List<int>> results) {
    if (currentPath.length == nums.length) {
      results.add(List.from(currentPath));
      return;
    }

    for (int num in nums) {
      if (visited.contains(num)) continue;

      currentPath.add(num);
      visited.add(num);

      _backtrack(nums, currentPath, visited, results); // Recursive step

      // Backtrack: Remove element and unmark visited
      currentPath.removeLast();
      visited.remove(num);
    }
  }
}
```

### Kotlin
```kotlin
class PermutationGenerator {
    fun generate(nums: IntArray): List<List<Int>> {
        val results = ArrayList<List<Int>>()
        backtrack(nums, ArrayList(), HashSet(), results)
        return results
    }

    private fun backtrack(
        nums: IntArray,
        currentPath: ArrayList<Int>,
        visited: HashSet<Int>,
        results: ArrayList<List<Int>>
    ) {
        if (currentPath.size == nums.size) {
            results.add(ArrayList(currentPath))
            return
        }

        for (num in nums) {
            if (visited.contains(num)) continue

            currentPath.add(num)
            visited.add(num)

            backtrack(nums, currentPath, visited, results) // Recursive step

            // Backtrack
            currentPath.removeAt(currentPath.size - 1)
            visited.remove(num)
        }
    }
}
```
