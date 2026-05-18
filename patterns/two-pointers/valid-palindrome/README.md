# Valid Palindrome (LeetCode 125)

## Pattern
**Two Pointers** (Iterating from both ends toward the middle).

---

## Problem
Given a string `s`, determine if it is a palindrome, considering only alphanumeric characters and ignoring cases.

---

## Approach
1. Initialize two pointers: `left` at the beginning (`0`) and `right` at the end (`s.length - 1`).
2. Move `left` forward if the character at `left` is not alphanumeric.
3. Move `right` backward if the character at `right` is not alphanumeric.
4. Compare character at `left` with character at `right` in a case-insensitive manner.
5. If they match, increment `left` and decrement `right` and continue.
6. If they do not match, return `false`.
7. Return `true` if the pointers meet or cross.

---

## Time Complexity
**$O(N)$**: Each character is visited at most once, where $N$ is the length of the string.

## Space Complexity
**$O(1)$**: In-place evaluation without creating any additional strings or character lists.

---

## Why This Solution Works
By comparing characters from both ends of the string and skipping non-alphanumeric characters on the fly, we verify the symmetry of the string in a single pass without copying or allocating memory for filtered copies of the string.

---

## Mobile Engineering Relevance
In mobile systems, checking string validity and parsing text input occurs on the UI thread (or main thread) during text field listener events (`TextWatcher` in Android/Kotlin or `TextEditingController` in Flutter/Dart). 
* **Avoiding GC Pressure**: Allocating a new filtered string using regex (e.g., `s.replaceAll(RegExp(r'[^a-zA-Z0-9]'), '')`) creates a secondary string in memory. If this is done inside an `onChanged` keyboard listener on every keystroke, it can rapidly fill up the heap and trigger the garbage collector (GC), resulting in dropped frames (jank) in the user interface.
* **In-place Two-Pointer check** avoids allocation, maintaining $O(1)$ auxiliary space and protecting the 60fps/120fps rendering pipeline.

---

## Tradeoffs
* **Speed vs. Regex simplicity**: A Regex-based replacement is simple to write (`s.replaceAll(...)`) but comes at the cost of multiple memory allocations and string allocations. The two-pointer approach is slightly longer to write but is highly optimized for performance-critical main-thread mobile environments.

---

## Code Solution

### Dart
```dart
class ValidPalindrome {
  bool isAlphanumeric(int charCode) {
    return (charCode >= 48 && charCode <= 57) || // 0-9
           (charCode >= 65 && charCode <= 90) || // A-Z
           (charCode >= 97 && charCode <= 122);  // a-z
  }

  bool isPalindrome(String s) {
    int left = 0;
    int right = s.length - 1;

    while (left < right) {
      int leftChar = s.codeUnitAt(left);
      int rightChar = s.codeUnitAt(right);

      if (!isAlphanumeric(leftChar)) {
        left++;
        continue;
      }
      if (!isAlphanumeric(rightChar)) {
        right--;
        continue;
      }

      // Case-insensitive comparison
      String leftStr = s[left].toLowerCase();
      String rightStr = s[right].toLowerCase();

      if (leftStr != rightStr) {
        return false;
      }

      left++;
      right--;
    }
    return true;
  }
}

void main() {
  final validator = ValidPalindrome();
  print(validator.isPalindrome("A man, a plan, a canal: Panama")); // true
  print(validator.isPalindrome("race a car")); // false
}
```

### Kotlin
```kotlin
class ValidPalindrome {
    private fun isAlphanumeric(ch: Char): Boolean {
        return ch in 'a'..'z' || ch in 'A'..'Z' || ch in '0'..'9'
    }

    fun isPalindrome(s: String): Boolean {
        var left = 0
        var right = s.length - 1

        while (left < right) {
            val leftChar = s[left]
            val rightChar = s[right]

            if (!isAlphanumeric(leftChar)) {
                left++
                continue
            }
            if (!isAlphanumeric(rightChar)) {
                right--
                continue
            }

            if (leftChar.lowercaseChar() != rightChar.lowercaseChar()) {
                return false
            }

            left++
            right--
        }
        return true
    }
}

fun main() {
    val validator = ValidPalindrome()
    println(validator.isPalindrome("A man, a plan, a canal: Panama")) // true
    println(validator.isPalindrome("race a car")) // false
}
```
