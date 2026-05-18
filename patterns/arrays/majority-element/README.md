# Majority Element (LeetCode 169)

## Pattern
**Boyer-Moore Voting Algorithm** / **Array Iteration**

---

## Problem
Given an array `nums` of size `n`, return the majority element. The majority element is the element that appears more than `⌊n / 2⌋` times. You may assume that the majority element always exists in the array.

---

## Approach
While a HashMap can count frequencies in $O(N)$ time and $O(N)$ space, the **Boyer-Moore Voting Algorithm** solves the problem in $O(N)$ time and $O(1)$ space:
1. Initialize a `candidate` variable and a `count` variable set to 0.
2. Iterate through the array `nums`:
   * If `count` is 0, set the `candidate` to the current element.
   * If the current element equals the `candidate`, increment `count` by 1.
   * Otherwise, decrement `count` by 1.
3. The `candidate` remaining at the end of the loop is the majority element.

---

## Time Complexity
**$O(N)$**: A single linear pass through the array.

## Space Complexity
**$O(1)$**: Constant auxiliary memory used (only `candidate` and `count` variables).

---

## Why This Solution Works
The frequency of the majority element is greater than $N/2$. Therefore, even if all other elements join forces to decrement the count of the majority element, the majority element will still have a net positive count by the end of the iteration, ensuring it remains the final candidate.

---

## Mobile Engineering Relevance
Mobile apps often receive batches of events, offline telemetry logs, or state updates from remote servers.
* **Telemetry and Logs Processing**: Imagine a device background worker analyzing a batch of 5,000 analytics/metric logs to find the dominant active feature tag before syncing with the backend.
* **HashMap Memory Bloat**: Creating a HashMap/Dictionary would create thousands of small integer objects in memory. On low-end mobile devices, this triggers garbage collection (GC) cycles and wastes device RAM.
* **In-place Boyer-Moore** runs with zero extra object allocations, preserving battery life and device memory footprints during heavy background sync operations.

---

## Tradeoffs
* **Robustness vs. Simplicity**: Boyer-Moore assumes a majority element *always* exists. If a majority element is not guaranteed, a second pass is required to verify the frequency of the candidate, which still maintains $O(N)$ time and $O(1)$ space. The HashMap approach works for any frequency count (e.g. finding the top-k items) but has a high memory overhead.

---

## Code Solution

### Dart
```dart
class MajorityElement {
  /// Boyer-Moore Voting Algorithm - O(N) Time, O(1) Space
  int findMajority(List<int> nums) {
    int count = 0;
    int? candidate;

    for (int num in nums) {
      if (count == 0) {
        candidate = num;
      }
      count += (num == candidate) ? 1 : -1;
    }

    return candidate!;
  }
}

void main() {
  final solver = MajorityElement();
  print(solver.findMajority([3, 2, 3])); // Output: 3
  print(solver.findMajority([2, 2, 1, 1, 1, 2, 2])); // Output: 2
}
```

### Kotlin
```kotlin
class MajorityElement {
    /**
     * Boyer-Moore Voting Algorithm - O(N) Time, O(1) Space
     */
    fun findMajority(nums: IntArray): Int {
        var count = 0
        var candidate: Int? = null

        for (num in nums) {
            if (count == 0) {
                candidate = num
            }
            count += if (num == candidate) 1 else -1
        }

        return candidate ?: -1
    }
}

fun main() {
    val solver = MajorityElement()
    println(solver.findMajority(intArrayOf(3, 2, 3))) // Output: 3
    println(solver.findMajority(intArrayOf(2, 2, 1, 1, 1, 2, 2))) // Output: 2
}
```
