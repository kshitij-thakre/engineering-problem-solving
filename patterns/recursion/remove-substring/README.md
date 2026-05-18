# Remove All Occurrences of a Substring (LeetCode 1910)

## Pattern
**Recursion / Iteration / Stack-Based String Manipulation**

---

## Problem
Given two strings `s` and `part`, perform the following operation on `s` until all occurrences of the substring `part` are removed:
* Find the leftmost occurrence of the substring `part` and remove it from `s`.

Return `s` after removing all occurrences.

---

## Approach

### Approach 1: Simple Iterative / Recursive Replace
1. While `s` contains `part`, search for its first index.
2. Slice `s` around that index to remove `part`, or call `replaceFirst()`.
3. While simple, this is inefficient for large strings because it creates new string allocations on each match, yielding $O(N^2 / M)$ worst-case time complexity.

### Approach 2: Optimized Stack-Based Single-Pass (Linear Time)
1. Initialize a dynamic character stack (or StringBuilder/StringBuffer).
2. Iterate through each character of `s` and append it to the stack.
3. Once the stack's length is at least the length of `part`, check if the trailing characters match `part`.
4. If they match, pop `part.length` characters from the stack.
5. Continue until the end of `s`. This yields $O(N)$ time complexity and prevents heavy string copy allocations.

---

## Time Complexity
* **Approach 1 (Simple)**: $O(N^2 / M)$ where $N$ is the length of `s` and $M$ is the length of `part`.
* **Approach 2 (Stack-based)**: $O(N)$ since each character is pushed and popped from the stack at most once.

## Space Complexity
* **Approach 1 (Simple)**: $O(N^2 / M)$ due to repeated string allocations.
* **Approach 2 (Stack-based)**: $O(N)$ for storing the characters of the resulting string.

---

## Why This Solution Works
The stack accumulates characters sequentially. By evaluating only the end of the stack as new characters arrive, we intercept and destroy occurrences of `part` immediately as they are formed. This handles nested occurrences (e.g. `daabcbaabcbc` with `part = abc`) perfectly in a single linear sweep.

---

## Mobile Engineering Relevance
Mobile platforms frequently parse dynamic streams (e.g., Markdown parsers, BBCode converters, HTML/XML tag stripper utilities, chat message markdown sanitization).
* **Memory Optimization during Parsing**: When a chat app parses incoming user messages, doing raw string replication (Approach 1) creates high heap memory volatility. 
* **Avoiding GC Jank**: Running Approach 2 in a custom text parser keeps the memory footprint linear and minimizes temporary string allocations. This ensures smooth scrolling in text-heavy lists like WhatsApp or Slack chat bubbles.

---

## Tradeoffs
* **Approach 1** is highly readable and extremely fast to write (3 lines of code). For short strings (e.g. user input profiles of $<100$ characters), the overhead is negligible.
* **Approach 2** is significantly faster for long text payloads (e.g. downloading a large markdown blog post and stripping specific formatting elements on device) but introduces additional code complexity.

---

## Code Solution

### Dart

#### Optimized Stack-Based Approach
```dart
class SubstringStripper {
  String removeOccurrences(String s, String part) {
    List<String> stack = [];
    int partLen = part.length;

    for (int i = 0; i < s.length; i++) {
      stack.add(s[i]);

      if (stack.length >= partLen) {
        bool match = true;
        for (int j = 0; j < partLen; j++) {
          if (stack[stack.length - partLen + j] != part[j]) {
            match = false;
            break;
          }
        }
        if (match) {
          // Pop partLen elements
          for (int j = 0; j < partLen; j++) {
            stack.removeLast();
          }
        }
      }
    }
    return stack.join('');
  }
}

void main() {
  final stripper = SubstringStripper();
  print(stripper.removeOccurrences("daabcbaabcbc", "abc")); // Output: "dab"
}
```

### Kotlin

#### Optimized StringBuilder Stack-Based Approach
```kotlin
class SubstringStripper {
    fun removeOccurrences(s: String, part: String): String {
        val sb = StringBuilder()
        val partLen = part.length

        for (i in s.indices) {
            sb.append(s[i])
            if (sb.length >= partLen) {
                var match = true
                for (j in 0 until partLen) {
                    if (sb[sb.length - partLen + j] != part[j]) {
                        match = false
                        break
                    }
                }
                if (match) {
                    sb.delete(sb.length - partLen, sb.length)
                }
            }
        }
        return sb.toString()
    }
}

fun main() {
    val stripper = SubstringStripper()
    println(stripper.removeOccurrences("daabcbaabcbc", "abc")) // Output: "dab"
}
```
