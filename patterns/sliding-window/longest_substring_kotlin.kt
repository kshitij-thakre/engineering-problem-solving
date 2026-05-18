package patterns.sliding_window

import kotlin.math.max

/**
 * Calculates the length of the longest substring without repeating characters.
 *
 * This implementation utilizes a dynamic sliding window with an index tracking Map.
 *
 * Time Complexity: O(N) where N is the length of the string s.
 * Space Complexity: O(min(M, N)) where M is the size of the character alphabet map.
 */
class LongestSubstringSolver {

    fun lengthOfLongestSubstring(s: String): Int {
        var maxLength = 0
        var left = 0
        // Maps characters to their last seen index + 1
        val lastSeenMap = HashMap<Char, Int>()

        for (right in 0 until s.length) {
            val char = s[right]
            
            // If the character is already in our map and its index falls within the active window
            if (lastSeenMap.containsKey(char)) {
                // Instantly slide the left boundary forward to avoid repetitions
                left = max(left, lastSeenMap[char]!!)
            }

            // Update the index of the character to the right + 1 (the next starting index)
            lastSeenMap[char] = right + 1
            
            // Recompute maximum sliding window size
            maxLength = max(maxLength, right - left + 1)
        }

        return maxLength
    }
}

fun main() {
    val solver = LongestSubstringSolver()
    println(solver.lengthOfLongestSubstring("abcabcbb")) // Output: 3
    println(solver.lengthOfLongestSubstring("bbbbb"))    // Output: 1
    println(solver.lengthOfLongestSubstring("pwwkew"))   // Output: 3
}
