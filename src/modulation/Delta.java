package modulation;

/**
 * Implements a simple Delta Modulation (DM) modulator.
 * This is a simplified version for demonstration.
 *
 * Delta Modulation (DM) is a technique that encodes an analog signal
 * using only a single bit per sample. It does this by tracking the
 * *change* (or 'delta') from the previous sample, rather than the
 * absolute value of the sample itself (like PCM does).
 *
 * It generates a "staircase" approximation of the analog signal.
 */
public class Delta {

    /**
     * Modulates a sample analog signal into a binary string using DM.
     *
     * @param analogSignal The input signal (an array of voltage samples).
     * @param delta        The fixed step size. This is how much the internal
     * approximation will move up or down for each '1' or '0' bit.
     * @return A binary data string (a stream of 1s and 0s).
     */
    public String modulate(double[] analogSignal, double delta) {
        // Guard clause for empty or null input
        if (analogSignal == null || analogSignal.length == 0) {
            return "";
        }

        // Use a StringBuilder for efficient string building in a loop
        StringBuilder binaryData = new StringBuilder();

        // This 'accumulator' represents the current value of the internal
        // 'staircase' approximation of the analog signal. We start it at 0.
        double accumulator = 0.0;

        // Iterate through each sample of the original analog signal
        for (double sample : analogSignal) {

            // This is the core logic of Delta Modulation:
            // Compare the current analog sample to our staircase's level.

            if (sample > accumulator) {
                // The signal is HIGHER than our approximation.

                // Output a '1' to indicate the signal is rising.
                binaryData.append('1');
                // Move our staircase UP by one 'delta' step to try and catch up.
                accumulator += delta;

            } else {
                // The signal is LOWER than (or equal to) our approximation.

                // Output a '0' to indicate the signal is falling.
                binaryData.append('0');
                // Move our staircase DOWN by one 'delta' step.
                accumulator -= delta;
            }
        }

        // Return the complete stream of 1s and 0s
        return binaryData.toString();
    }
}