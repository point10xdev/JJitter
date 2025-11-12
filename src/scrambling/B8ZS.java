package scrambling;

/**
 * Implements B8ZS (Bipolar 8-Zero Substitution) scrambling.
 * Replaces "00000000" with "000VB0VB".
 * The AmiLineEncoder must be aware of 'V' and 'B'.
 * 'V' = Bipolar Violation (same polarity as last 1)
 * 'B' = Bipolar Pulse (conforms to polarity of last 1)
 */
public class B8ZS implements Scrambler {
    @Override
    public String scramble(String binaryData) {
        // We use special character sequences to mark substitutions.
        // 'V' = Violation, 'B' = Bipolar.
        // The AMI encoder will know how to interpret "000VB0VB".
        return binaryData.replaceAll("00000000", "000VB0VB");
    }
}