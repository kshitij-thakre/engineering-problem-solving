# Longest Substring Without Repeating Characters

## 1. Problem Statement
Given a string `s`, find the length of the longest substring without repeating characters.

* **Example 1:**
  * Input: `s = "abcabcbb"`
  * Output: `3` (Explanation: The answer is `"abc"`, with the length of `3`).
* **Example 2:**
  * Input: `s = "bbbbb"`
  * Output: `1` (Explanation: The answer is `"b"`, with the length of `1`).

---

## 2. Pattern Explanation: Dynamic Sliding Window

Instead of using a brute-force $O(N^2)$ or $O(N^3)$ approach which checks all possible substrings, we utilize a **Dynamic Sliding Window** with two pointers (`left` and `right`) and a hash map/frequency index structure. 

The sliding window defines the current substring bounds under inspection. As the `right` pointer moves forward, it expands the window. When we encounter a repeating character already in our window, we dynamically shrink the window by moving the `left` pointer to the right of the last seen index of that character. This ensures we never perform redundant recalculations, yielding an $O(N)$ linear time complexity.

```mermaid
graph TD
    subgraph SlidingWindow ["Dynamic Sliding Window Flow"]
        Start([Start: left=0, right=0]) --> ReadChar[Read char at right pointer]
        ReadChar --> CheckDup{Is char in map & >= left?}
        CheckDup -- Yes --> ShiftLeft[Move left = last_seen_index + 1]
        ShiftLeft --> UpdateMap[Update char index in map]
        CheckDup -- No --> UpdateMap
        UpdateMap --> CalcLen[Calculate maxLength = max(maxLength, right - left + 1)]
        CalcLen --> MoveRight[Increment right pointer]
        MoveRight --> CheckEnd{Is right < string length?}
        CheckEnd -- Yes --> ReadChar
        CheckEnd -- No --> End([End: Return maxLength])
    end
```

---

## 3. Real-World Mobile Engineering Use Cases

### 1. Text Input Stream Watchers & Autocomplete Suggestion Parsing
* When users type inside a text field (e.g. searching, tags tagging, or username inputs), we parse token inputs in real-time. If we want to check for unique token separators or strip out duplicated input patterns on the fly, a sliding window scans the text buffer with minimal CPU overhead, avoiding frame-drops in the main UI thread.

### 2. Sensor Event Streams (Gesture Recognition & Shake Detection)
* Mobile sensor streams (accelerometer, gyroscope) fire high-frequency events (often 60–100Hz). Recognizing gestures (like a shake or swipe) involves analyzing a continuous sequence of unique coordinate states. If a repeating sensor state is encountered within the active window, the window boundary shifts to analyze a fresh sequence segment.

---

## 4. Complexity & Tradeoffs

* **Time Complexity:** $O(N)$, where $N$ is the length of the string. The `right` pointer travels from start to finish, and the `left` pointer moves forward monotonically. Each character is visited at most twice.
* **Space Complexity:** $O(\min(M, K))$, where $M$ is the size of the character set (e.g. 26 for English alphabet, 128 for ASCII) and $K$ is the length of the string, which represents the memory allocated to store unique characters in our frequency index map.
* **Tradeoffs:** We trade memory ($O(M)$ auxiliary map space) to gain sub-millisecond execution times ($O(N)$ time instead of $O(N^2)$ brute-force). On mobile platforms, $O(M)$ space is negligible (a few bytes), making this tradeoff extremely favorable.

---

## 5. Visual Walkthrough (s = "abcabcbb")

```
Step 1: left = 0, right = 0, char = 'a'
        Window: [a]bcabcbb
        Map: {'a': 0}
        maxLength = 1

Step 2: left = 0, right = 1, char = 'b'
        Window: [ab]cabcbb
        Map: {'a': 0, 'b': 1}
        maxLength = 2

Step 3: left = 0, right = 2, char = 'c'
        Window: [abc]abcbb
        Map: {'a': 0, 'b': 1, 'c': 2}
        maxLength = 3

Step 4: left = 0, right = 3, char = 'a' (Duplicate found at index 0)
        Shift left to: map['a'] + 1 = 1
        Window: a[bca]bcbb
        Map: {'a': 3, 'b': 1, 'c': 2}
        maxLength = max(3, 3 - 1 + 1) = 3

Step 5: left = 1, right = 4, char = 'b' (Duplicate found at index 1)
        Shift left to: map['b'] + 1 = 2
        Window: ab[cab]cbb
        Map: {'a': 3, 'b': 4, 'c': 2}
        maxLength = max(3, 4 - 2 + 1) = 3
```

---

## 6. Implementation Reference

* **Kotlin Implementation:** [`longest_substring_kotlin.kt`](./longest_substring_kotlin.kt)
* **Dart Implementation:** [`longest_substring_dart.dart`](./longest_substring_dart.dart)
