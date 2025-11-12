package scrambling;

/**
 * Interface for scrambling schemes.
 * Scrambling modifies the binary data stream to insert special symbols
 * that the AMI encoder will understand.
 */
public interface Scrambler {
    /**
     * Scrambles a binary data string.
     * @param binaryData The input string of '0's and '1's.
     * @return A modified string containing '0', '1', and special symbols
     * (like 'V' for Violation, 'B' for Bipolar) that the AMI
     * encoder can interpret.
     */
    String scramble(String binaryData);
}