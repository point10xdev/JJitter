package modulation;

/**
 * Implements a simple Delta Modulation (DM) modulator.
 * This is a simplified version for demonstration.
 */
public class Delta {

    /**
     * Modulates a sample analog signal into a binary string using DM.
     * @param analogSignal The input signal.
     * @param delta The step size.
     * @return A binary data string.
     */
    public String modulate(double[] analogSignal, double delta) {
        if (analogSignal == null || analogSignal.length == 0) {
            return "";
        }

        StringBuilder binaryData = new StringBuilder();
        double accumulator = 0.0; // Start staircase at 0

        for (double sample : analogSignal) {
            if (sample > accumulator) {
                binaryData.append('1');
                accumulator += delta;
            } else {
                binaryData.append('0');
                accumulator -= delta;
            }
        }
        return binaryData.toString();
    }
}