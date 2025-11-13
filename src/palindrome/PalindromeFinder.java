package palindrome;

/**
 * Finds the longest palindromic substring in a string.
 * This implementation uses Manacher's Algorithm for O(n) time complexity.
 *
 * Placed in the default package as per the project structure.
 */
public class PalindromeFinder {

    /**
     * Preprocesses the string to handle even-length palindromes uniformly.
     * Manacher's algorithm works by expanding around a *center*.
     * Odd-length palindromes ("aba") have a clear center ('b').
     * Even-length palindromes ("abba") have a center *between* characters (between the two 'b's).
     *
     * This method inserts a '#' between each character and at the ends,
     * ensuring all palindromes (even or odd in the original string)
     * are now odd-length and have a clear character center in the new string.
     *
     * "aba"  -> "^#a#b#a#$" (Center 'b' is still 'b')
     * "abba" -> "^#a#b#b#a#$" (Center is now '#' between the 'b's)
     *
     * The '^' and '$' are "sentinel" characters. They are guaranteed
     * to not match any other character in the string, which cleverly
     * avoids the need for explicit array bounds checking during the
     * palindrome expansion phase (the 'while' loop).
     */
    private String preprocess(String s) {
        // Start with the beginning sentinel
        StringBuilder sb = new StringBuilder("^");

        // Insert '#' between each character of the original string
        for (int i = 0; i < s.length(); i++) {
            sb.append('#').append(s.charAt(i));
        }

        // Add the final '#' and the end sentinel
        sb.append("#$");
        return sb.toString();
    }

    /**
     * Finds the longest palindromic substring using Manacher's algorithm.
     * @param s The input string (e.g., "1001011").
     * @return The longest palindromic substring found.
     */
    public String findLongestPalindrome(String s) {
        // Handle null or empty input
        if (s == null || s.length() == 0) {
            return "";
        }

        // T is the transformed string (e.g., "^#a#b#b#a#$")
        String T = preprocess(s);
        int n = T.length();

        // P[i] is the core of the algorithm.
        // P[i] stores the "radius" of the palindrome centered at T[i].
        // The radius is the number of matching character pairs on either side of the center.
        // Example: T = "...#a#b#a#..."
        // At the center 'b' (let's say index i), the palindrome is "#a#b#a#".
        // The radius P[i] would be 3 (for the pairs (#,#), (a,a), (#,#)).
        int[] P = new int[n];

        // C = Center of the *right-most* palindrome found so far.
        // R = Right boundary of that same palindrome (R = C + P[C]).
        // This (C, R) pair defines a "window" that we use for optimization.
        int C = 0, R = 0;

        // We iterate from i = 1 to n-2.
        // We skip i = 0 (^) and i = n-1 ($) because they are sentinels
        // and cannot be centers of palindromes.
        for (int i = 1; i < n - 1; i++) {

            // --- This is the O(n) magic ---
            // Calculate the "mirror" index of i with respect to the center C.
            // If i is inside the (C, R) window, i_mirror is its reflection on the left side.
            int i_mirror = 2 * C - i; // i_mirror = C - (i - C)

            // Check if the current position 'i' is inside the right-most palindrome boundary 'R'.
            if (R > i) {
                // We are *inside* the (C, R) window.
                // We can use the information we already computed for i_mirror.
                //
                // We know P[i] is *at least* P[i_mirror].
                // However, it can't extend beyond the right boundary 'R'.
                //
                // Case 1: The palindrome at i_mirror is fully *contained* within the (C,R) window.
                //         In this case, P[i] = P[i_mirror].
                // Case 2: The palindrome at i_mirror *extends to or beyond* the left boundary of (C,R).
                //         This means the palindrome at 'i' will *at least* extend to the *right* boundary 'R'.
                //         We set P[i] = R - i (the remaining distance to the edge).
                //         We must then try to expand it further in the 'while' loop.
                //
                // Math.min handles both cases.
                P[i] = Math.min(R - i, P[i_mirror]);
            } else {
                // We are *outside* or *on the edge* of the (C, R) window.
                // We have no pre-computed information to use.
                // We must start with a radius of 0 and expand from scratch.
                P[i] = 0;
            }

            // --- Naive Expansion Phase ---
            // Attempt to expand the palindrome centered at i.
            // We start from the radius P[i] we just calculated (which was either 0 or a non-trivial value from the optimization).
            // We check T[i + (1 + P[i])] == T[i - (1 + P[i])]
            // (i.e., the characters just *outside* the current radius)
            //
            // This loop is safe from ArrayOutOfBounds errors because of the
            // sentinel characters '^' and '$', which will never match.
            while (T.charAt(i + 1 + P[i]) == T.charAt(i - 1 + P[i])) {
                P[i]++; // Expand the radius
            }

            // --- Update C and R ---
            // If the palindrome we just found (centered at i) expands past
            // the old right boundary 'R', it becomes the new "right-most palindrome".
            if (i + P[i] > R) {
                C = i;         // Update the center
                R = i + P[i];  // Update the right boundary
            }
        }

        // --- Find the Result ---
        // Now that the P array is fully computed, we just need to find
        // the largest radius (maxLen) and its center (centerIndex).
        int maxLen = 0;
        int centerIndex = 0;
        for (int i = 1; i < n - 1; i++) {
            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }

        // --- Convert Back to Original String ---
        // We have the centerIndex and maxLen from the *transformed* string T.
        // We need to find the start index in the *original* string s.
        //
        // A key insight: maxLen (the radius in T) is exactly equal to
        // the *length* of the corresponding palindrome in the *original* string s.
        // Example: s = "abba", T = "#a#b#b#a#", maxLen = 4 (at center '#')
        // Example: s = "aba",  T = "#a#b#a#",  maxLen = 3 (at center 'b')
        //
        // So, the length of our answer is `maxLen`.
        // The start index in s is calculated from the T-center and T-radius.
        int start = (centerIndex - maxLen) / 2;

        // Return the substring from the original string s.
        return s.substring(start, start + maxLen);
    }
}