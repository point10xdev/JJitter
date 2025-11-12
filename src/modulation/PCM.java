package modulation;

/**
 * Implements a simple Pulse Code Modulation (PCM) modulator.
 * This is a simplified version for demonstration.
 */
public class PCM {

    /**
     * Generates a sample analog signal (a sine wave).
     * @param length Number of samples to generate.
     * @return Array of doubles representing the signal.
     */
    public double[] getSampleAnalogSignal(int length) {
        double[] signal = new double[length];
        for (int i = 0; i < length; i++) {
            // Simple sine wave
            signal[i] = 5.0 * Math.sin(2.0 * Math.PI * i / (length / 3.0)); // 3 cycles
            // Add some noise
            signal[i] += (Math.random() - 0.5);
        }
        return signal;
    }

    /**
     * Modulates a sample analog signal into a binary string using PCM.
     * @param analogSignal The input signal.
     * @param quantizationLevels Number of levels (e.g., 8, 16, 32). Must be power of 2.
     * @return A binary data string.
     */
    public String modulate(double[] analogSignal, int quantizationLevels) {
        if (analogSignal == null || analogSignal.length == 0) {
            return "";
        }

        int bitsPerSample = (int) (Math.log(quantizationLevels) / Math.log(2));

        // 1. Find min and max
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double v : analogSignal) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // 2. Calculate step size
        double stepSize = (max - min) / quantizationLevels;
        StringBuilder binaryData = new StringBuilder();

        // 3. Quantize and Encode
        for (double sample : analogSignal) {
            int level = (int) Math.floor((sample - min) / stepSize);
            // Clamp level to max
            if (level >= quantizationLevels) {
                level = quantizationLevels - 1;
            }
            // Convert level to binary string
            String binarySample = Integer.toBinaryString(level);
            // Pad with leading zeros
            while (binarySample.length() < bitsPerSample) {
                binarySample = "0" + binarySample;
            }
            binaryData.append(binarySample);
        }
        return binaryData.toString();
    }
}