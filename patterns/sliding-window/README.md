# Pattern: Sliding Window

## Mobile Engineering Context
In mobile development, data streams from sensors (accelerometers, gyroscope), user typing listeners, and infinite lists are continuous. Computing metrics across the entire stream is slow. The **Sliding Window** pattern isolates a moving segment of the stream to perform computations in $O(1)$ or linear time relative to window size.

---

## Real-World Mobile Relevance

### 1. Infinite List Viewport Virtualization
* **How it works**: A list containing $10,000$ elements only renders the $10$ cells currently visible in the screen's "sliding viewport window" (plus $2$ buffer cells above and below). As the user scrolls, the window slides down, recycling old cells from the top and binding new data to them at the bottom.

### 2. Network Frame-Rate Smoothing (FPS Jitter)
* **How it works**: To measure scrolling smoothness, the app tracks the rendering time of the last $60$ frames. When frame $61$ arrives, it drops the oldest frame from the window and adds the new frame to calculate the rolling frame rate.

---

## Code Example: Maximum Sum Subarray of Size K (Stream Buffering)

### Dart
```dart
double findMaxAverage(List<int> nums, int k) {
  int sum = 0;
  for (int i = 0; i < k; i++) {
    sum += nums[i];
  }
  
  int maxSum = sum;
  for (int i = k; i < nums.length; i++) {
    sum += nums[i] - nums[i - k]; // Slide the window: Add new element, remove oldest
    if (sum > maxSum) {
      maxSum = sum;
    }
  }
  return maxSum / k;
}
```

### Kotlin
```kotlin
fun findMaxAverage(nums: IntArray, k: Int): Double {
    var sum = 0
    for (i in 0 until k) {
        sum += nums[i]
    }
    
    var maxSum = sum
    for (i in k until nums.size) {
        sum += nums[i] - nums[i - k] // Slide the window
        if (sum > maxSum) {
            maxSum = sum
        }
    }
    return maxSum.toDouble() / k
}
```
