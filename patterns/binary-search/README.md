# Pattern: Binary Search

## Mobile Engineering Context
Binary search operates on sorted structures to find a target in $O(\log N)$ logarithmic time rather than $O(N)$ linear time. In mobile applications, binary search is essential for rapidly indexing items in massive sorted offline logs, searching for keyframes in media buffering streams, or calculating index placements inside highly virtualization-optimized screen lists.

---

## Real-World Mobile Relevance

### 1. Media Timeline Keyframe Selections
* **How it works**: During video or audio playback, seeking to a specific millisecond timestamp requires scanning a sorted array of structural keyframes. Binary search enables $O(\log N)$ seeks, keeping interactions fluid.

### 2. Log Index Seeking
* **How it works**: Searching a local file containing 50,000 sorted offline logs to locate entries for a specific timestamp.

---

## Code Example: Classic Binary Search

### Dart
```dart
int binarySearch(List<int> sortedArray, int target) {
  int low = 0;
  int high = sortedArray.length - 1;

  while (low <= high) {
    int mid = low + ((high - low) ~/ 2); // Avoid integer overflow
    if (sortedArray[mid] == target) {
      return mid;
    } else if (sortedArray[mid] < target) {
      low = mid + 1;
    } else {
      high = mid - 1;
    }
  }
  return -1; // Target not found
}
```

### Kotlin
```kotlin
fun binarySearch(sortedArray: IntArray, target: Int): Int {
    var low = 0
    var high = sortedArray.size - 1

    while (low <= high) {
        val mid = low + (high - low) / 2 // Avoid overflow
        if (sortedArray[mid] == target) {
            return mid
        } else if (sortedArray[mid] < target) {
            low = mid + 1
        } else {
            high = mid - 1
        }
    }
    return -1
}
```
