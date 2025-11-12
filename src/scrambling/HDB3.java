package scrambling;

/**
 * Implements HDB3 (High-Density Bipolar 3-Zero) scrambling.
 * Replaces "0000" with "000V" or "B00V".
 * Choice depends on the number of '1's since the last substitution.
 * 'V' = Bipolar Violation (same polarity as last 1)
 * 'B' = Bipolar Pulse (conforms to polarity of last 1)
 */
public class HDB3 implements Scrambler {
    @Override
    public String scramble(String binaryData) {
        StringBuilder scrambled = new StringBuilder();
        int onesSinceLastSub = 0;
        int i = 0;

        while (i < binaryData.length()) {
            if (i + 4 <= binaryData.length() && "0000".equals(binaryData.substring(i, i + 4))) {
                // Found "0000", apply substitution
                if (onesSinceLastSub % 2 == 0) {
                    // Even '1's -> B00V
                    scrambled.append("B00V");
                } else {
                    // Odd '1's -> 000V
                    scrambled.append("000V");
                }
                i += 4;
                onesSinceLastSub = 0; // Reset count
            } else {
                char bit = binaryData.charAt(i);
                if (bit == '1') {
                    onesSinceLastSub++;
                }
                scrambled.append(bit);
                i++;
            }
        }
        return scrambled.toString();
    }
}