package modulation;
/**
 * Implements a simple Pulse Code Modulation (PCM) modulator.
 * This is a simplified version for demonstration.
 *
 * PCM works by sampling an analog signal at regular intervals,
 * measuring the amplitude of each sample, and then converting that
 * amplitude into a binary number.
 */
public class PCM {
    /**
     * (Note: This application uses SignalSynth.java ).
     *
     * @param length Number of samples to generate.
     * @return Array of doubles representing the signal.
     */
    public double[] getSampleAnalogSignal(int length) {
        double[] signal = new double[length];
        for (int i = 0; i < length; i++) {
            // Simple sine wave:
            // 5.0 = Amplitude
            // 2.0 * Math.PI * i = Current angle in radians
            // (length / 3.0) = Wavelength, adjusted so 3 cycles fit in the total length
            signal[i] = 5.0 * Math.sin(2.0 * Math.PI * i / (length / 3.0)); // 3 cycles

            // Add some random noise to make the signal less perfect
            signal[i] += (Math.random() - 0.5); // Adds a random value between -0.5 and +0.5
        }
        return signal;
    }

    /**
     * Modulates a sample analog signal into a binary string using PCM.
     *
     * @param analogSignal       The input signal (an array of voltage samples).
     * @param quantizationLevels Number of discrete levels to divide the signal into
     * (e.g., 8, 16, 32). Must be a power of 2.
     * @return A binary data string representing the entire signal.
     */
    public String modulate(double[] analogSignal, int quantizationLevels) {
        // Guard clause for empty or null input
        if (analogSignal == null || analogSignal.length == 0) {
            return "";
        }

        // Calculate how many bits are needed for each sample.
        // e.g., 16 levels = log2(16) = 4 bits per sample.
        // e.g., 256 levels = log2(256) = 8 bits per sample.
        int bitsPerSample = (int) (Math.log(quantizationLevels) / Math.log(2));

        // --- Step 1: Find the signal's total range ---
        // We need to find the absolute lowest and highest voltage
        // to know how to divide our quantization levels.
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double v : analogSignal) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // --- Step 2: Calculate the voltage "height" of a single step ---
        // This is the size of each discrete level.
        // e.g., if range (max - min) is 10V and we have 16 levels,
        // stepSize = 10 / 16 = 0.625V.
        double stepSize = (max - min) / quantizationLevels;

        // Use a StringBuilder for efficient string building in a loop.
        StringBuilder binaryData = new StringBuilder();

        // --- Step 3: Quantize and Encode each sample ---
        for (double sample : analogSignal) {

            // This is the core quantization logic:
            // 1. (sample - min): Shift the sample so the range starts at 0
            //    (e.g., a -4.0V sample in a -5V to +5V range becomes 1.0V)
            // 2. (... / stepSize): Find out how many steps "up" this sample is
            //    (e.g., 1.0V / 0.625V = 1.6)
            // 3. Math.floor(...): Round down to get the integer level index
            //    (e.g., Math.floor(1.6) = 1. The sample is in level 1.)
            int level = (int) Math.floor((sample - min) / stepSize);

            // Clamp the level to the maximum possible value.
            // This handles the edge case where a sample is exactly equal to 'max',
            // which would put it at 'quantizationLevels' (e.g., 16),
            // but our levels are 0-indexed (0 to 15).
            if (level >= quantizationLevels) {
                level = quantizationLevels - 1;
            }

            // Convert the integer level (e.g., 1) to its binary string (e.g., "1")
            String binarySample = Integer.toBinaryString(level);

            // Pad with leading zeros to ensure fixed-width samples.
            // e.g., if bitsPerSample is 4:
            // "1" (level 1) becomes "0001"
            // "10" (level 2) becomes "0010"
            // "111" (level 7) becomes "0111"
            while (binarySample.length() < bitsPerSample) {
                binarySample = "0" + binarySample;
            }

            // Add this sample's binary code to the final string
            binaryData.append(binarySample);
        }

        // Return the complete binary data string for the entire signal
        return binaryData.toString();
    }
}