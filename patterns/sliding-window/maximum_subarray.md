# Maximum Subarray (Kadane's Algorithm)

## 1. Problem Statement
Given an integer array `nums`, find the subarray with the largest sum and return its sum.

* **Example 1:**
  * Input: `nums = [-2,1,-3,4,-1,2,1,-5,4]`
  * Output: `6` (Explanation: The subarray `[4,-1,2,1]` has the largest sum `6`).
* **Example 2:**
  * Input: `nums = [5,4,-1,7,8]`
  * Output: `23` (Explanation: The subarray `[5,4,-1,7,8]` has the largest sum `23`).

---

## 2. Pattern Explanation: Dynamic Sliding Window / Kadane's

The **Maximum Subarray** problem is resolved efficiently using **Kadane's Algorithm**, which acts as a dynamic sliding window. At each index, we evaluate whether to extend our existing sliding window by adding the current element, or discard the history and start a new window starting *at* the current element. This choice is formulated as:
$$\text{currentSum} = \max(\text{num}, \text{currentSum} + \text{num})$$

This single-pass evaluation solves the problem in $O(N)$ linear time and $O(1)$ constant space, completely bypassing the $O(N^2)$ brute-force approach.

```mermaid
graph TD
    subgraph KadanesFlow ["Kadane's Subarray Window Flow"]
        Start([Start: currentSum=0, maxSum=Int.MIN]) --> ReadNum[Read next number]
        ReadNum --> MaxCheck{Is num > currentSum + num?}
        MaxCheck -- Yes --> ResetWindow[Start new window: currentSum = num]
        MaxCheck -- No --> ExpandWindow[Expand window: currentSum = currentSum + num]
        ResetWindow --> MaxSumCheck[maxSum = max(maxSum, currentSum)]
        ExpandWindow --> MaxSumCheck
        MaxSumCheck --> Next{More numbers?}
        Next -- Yes --> ReadNum
        Next -- No --> End([End: Return maxSum])
    end
```

---

## 3. Real-World Mobile Engineering Use Cases

### 1. High-Frequency Log Telemetry Optimization
* Mobile analytical dashboards and crash reporters (like Firebase Crashlytics or custom telemetry trackers) collect network latency values or transaction timings. If we want to identify the consecutive period where the device experienced the worst combined network delays (e.g. dynamic sliding latency spikes) for server diagnostics, Kadane's algorithm runs locally in real-time with zero allocations.

### 2. Battery & Thread Load Leveling (Rolling Power Consumption)
* To monitor power draw spikes, systems measure cellular radio usage across dynamic intervals. By sliding a rolling calculation over continuous logs, the OS can determine the peak consumption interval to schedule background sync blocks off peak times.

---

## 4. Complexity & Tradeoffs

* **Time Complexity:** $O(N)$, where $N$ is the number of elements in the array. We scan the array exactly once.
* **Space Complexity:** $O(1)$ auxiliary space. We only allocate two scalar variables (`currentSum` and `maxSum`), which prevents garbage collection runs on the UI thread.
* **Tradeoffs:** The algorithm destroys intermediate state lists (we only record the maximum sum, not the exact indexes of the subarray). If the exact start and end indexes are required, we must track pointer variables, which still runs in $O(1)$ space but adds minor variable maintenance.

---

## 5. Visual Walkthrough (nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4])

```
Initial: currentSum = 0, maxSum = -infinity

Step 1 (num = -2): currentSum = max(-2, 0 + -2) = -2
                   maxSum = max(-infinity, -2) = -2

Step 2 (num =  1): currentSum = max(1, -2 + 1) = 1 (Discard old window [-2], start fresh at [1])
                   maxSum = max(-2, 1) = 1

Step 3 (num = -3): currentSum = max(-3, 1 + -3) = -2 (Extend window to [1, -3])
                   maxSum = max(1, -2) = 1

Step 4 (num =  4): currentSum = max(4, -2 + 4) = 4 (Discard window, start fresh at [4])
                   maxSum = max(1, 4) = 4

Step 5 (num = -1): currentSum = max(-1, 4 + -1) = 3 (Extend window to [4, -1])
                   maxSum = max(4, 3) = 4

Step 6 (num =  2): currentSum = max(2, 3 + 2) = 5 (Extend window to [4, -1, 2])
                   maxSum = max(4, 5) = 5

Step 7 (num =  1): currentSum = max(1, 5 + 1) = 6 (Extend window to [4, -1, 2, 1])
                   maxSum = max(5, 6) = 6  <-- PEAK SUM FOUND

Step 8 (num = -5): currentSum = max(-5, 6 + -5) = 1
                   maxSum = max(6, 1) = 6
```

---

## 6. Implementation

### Kotlin
```kotlin
fun maxSubArray(nums: IntArray): Int {
    var maxSum = nums[0]
    var currentSum = nums[0]

    for (i in 1 until nums.size) {
        val num = nums[i]
        // Either extend the subarray or start a new one from the current element
        currentSum = maxOf(num, currentSum + num)
        maxSum = maxOf(maxSum, currentSum)
    }

    return maxSum
}
```

### Dart
```dart
int maxSubArray(List<int> nums) {
  int maxSum = nums[0];
  int currentSum = nums[0];

  for (int i = 1; i < nums.length; i++) {
    int num = nums[i];
    // Decides whether to chain the value or start a new window
    currentSum = num > (currentSum + num) ? num : (currentSum + num);
    if (currentSum > maxSum) {
      maxSum = currentSum;
    }
  }

  return maxSum;
}
```
