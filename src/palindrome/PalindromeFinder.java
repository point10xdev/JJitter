package palindrome;

/**
 * Finds the longest palindromic substring in a string.
 * This implementation uses Manacher's Algorithm for O(n) time complexity.
 *
 * Placed in the default package as per the project structure.
 */
public class PalindromeFinder {

    /**
     * Preprocesses the string to handle even-length palindromes.
     * "aba" -> "^#a#b#a#$"
     * "abba" -> "^#a#b#b#a#$"
     */
    private String preprocess(String s) {
        StringBuilder sb = new StringBuilder("^");
        for (int i = 0; i < s.length(); i++) {
            sb.append('#').append(s.charAt(i));
        }
        sb.append("#$");
        return sb.toString();
    }

    /**
     * Finds the longest palindromic substring using Manacher's algorithm.
     * @param s The input string (e.g., "1001011").
     * @return The longest palindromic substring found.
     */
    public String findLongestPalindrome(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        String T = preprocess(s);
        int n = T.length();
        int[] P = new int[n]; // P[i] = length of palindrome centered at i
        int C = 0, R = 0; // C = Center of current palindrome, R = Right edge

        for (int i = 1; i < n - 1; i++) {
            int i_mirror = 2 * C - i; // mirror of i around C

            P[i] = (R > i) ? Math.min(R - i, P[i_mirror]) : 0;

            // Attempt to expand palindrome centered at i
            while (T.charAt(i + 1 + P[i]) == T.charAt(i - 1 - P[i])) {
                P[i]++;
            }

            // If palindrome centered at i expands past R,
            // adjust center C and right edge R
            if (i + P[i] > R) {
                C = i;
                R = i + P[i];
            }
        }

        // Find the maximum element in P
        int maxLen = 0;
        int centerIndex = 0;
        for (int i = 1; i < n - 1; i++) {
            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }

        // Calculate start and end index in original string s
        int start = (centerIndex - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }
}