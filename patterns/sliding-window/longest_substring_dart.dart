import 'dart:math';

/**
 * Calculates the length of the longest substring without repeating characters.
 *
 * Time Complexity: O(N)
 * Space Complexity: O(min(M, N))
 */
class LongestSubstringSolver {
  int lengthOfLongestSubstring(String s) {
    int maxLength = 0;
    int left = 0;
    // Maps characters to their last seen index + 1
    final Map<String, int> lastSeenMap = {};

    for (int right = 0; right < s.length; right++) {
      final String char = s[right];

      if (lastSeenMap.containsKey(char)) {
        // Adjust the left boundary to the right of the duplicated element
        left = max(left, lastSeenMap[char]!);
      }

      // Record the position of the character (+ 1 to shift index bounds)
      lastSeenMap[char] = right + 1;

      // Recompute dynamic window size
      maxLength = max(maxLength, right - left + 1);
    }

    return maxLength;
  }
}

void main() {
  final solver = LongestSubstringSolver();
  print(solver.lengthOfLongestSubstring("abcabcbb")); // Output: 3
  print(solver.lengthOfLongestSubstring("bbbbb"));    // Output: 1
  print(solver.lengthOfLongestSubstring("pwwkew"));   // Output: 3
}
