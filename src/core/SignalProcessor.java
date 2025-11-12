package core;

import encoding.*;
import modulation.*;
import scrambling.*;
import palindrome.PalindromeFinder;

import java.util.List;

/**
 * Main logic class to process inputs and generate signals.
 * This class orchestrates the modulation, scrambling, and encoding.
 * Placed in the default package.
 */
public class SignalProcessor {

    private PCM pcmModulator;
    private Delta deltaModulator;
    private PalindromeFinder palindromeFinder;

    public SignalProcessor() {
        this.pcmModulator = new PCM();
        this.deltaModulator = new Delta();
        this.palindromeFinder = new PalindromeFinder();
    }

    /**
     * Public enum to represent the results of signal processing.
     */
    public static class SignalResult {
        public final String originalData;
        public final String scrambledData;
        public final String longestPalindrome;
        public final List<SignalFrame> encodedSignal;
        public final List<SignalFrame> scrambledSignal; // May be null

        public SignalResult(String original, String scrambled, String palindrome,
                            List<SignalFrame> encoded, List<SignalFrame> scrambledEnc) {
            this.originalData = original;
            this.scrambledData = scrambled;
            this.longestPalindrome = palindrome;
            this.encodedSignal = encoded;
            this.scrambledSignal = scrambledEnc;
        }
    }

    /**
     * Processes an analog input.
     * @param modulationType "PCM" or "DM"
     * @param encodingType "NRZ-L", "AMI", etc.
     * @param scramblingType "NONE", "B8ZS", "HDB3"
     * @return A SignalResult object.
     */
    public SignalResult processAnalogInput(String modulationType, String encodingType, String scramblingType) {
        // 1. Generate sample analog signal
        double[] analogSignal = pcmModulator.getSampleAnalogSignal(50); // 50 samples

        // 2. Modulate
        String binaryData;
        if ("PCM".equals(modulationType)) {
            binaryData = pcmModulator.modulate(analogSignal, 16); // 16 levels = 4 bits/sample
        } else { // "DM"
            binaryData = deltaModulator.modulate(analogSignal, 0.5); // Step size 0.5
        }

        // 3. Process the resulting digital data
        return processDigitalInput(binaryData, encodingType, scramblingType);
    }

    /**
     * Processes a digital input string.
     * @param binaryData The user-provided binary string.
     * @param encodingType "NRZ-L", "AMI", etc.
     * @param scramblingType "NONE", "B8ZS", "HDB3"
     * @return A SignalResult object.
     */
    public SignalResult processDigitalInput(String binaryData, String encodingType, String scramblingType) {

        // 1. Find Longest Palindrome
        String palindrome = palindromeFinder.findLongestPalindrome(binaryData);

        // 2. Handle Scrambling (if applicable)
        String scrambledData = null;
        List<SignalFrame> scrambledSignal = null;

        // Note: Scrambling is only for AMI
        if ("AMI".equals(encodingType) && !"NONE".equals(scramblingType)) {
            Scrambler scrambler = null;
            if ("B8ZS".equals(scramblingType)) {
                scrambler = new B8ZS();
            } else if ("HDB3".equals(scramblingType)) {
                scrambler = new HDB3();
            }

            if (scrambler != null) {
                scrambledData = scrambler.scramble(binaryData);
                // The "scrambled signal" is the AMI encoding of the scrambled *data*
                AMI amiEncoder = new AMI();
                scrambledSignal = amiEncoder.encode(scrambledData);
            }
        }

        // 3. Handle main encoding (of the *original* data)
        LineEncoder encoder = getEncoder(encodingType);
        List<SignalFrame> encodedSignal = encoder.encode(binaryData);

        // 4. Return results
        return new SignalResult(binaryData, scrambledData, palindrome, encodedSignal, scrambledSignal);
    }

    private LineEncoder getEncoder(String encodingType) {
        switch (encodingType) {
            case "NRZ-L":
                return new NRZL();
            case "NRZ-I":
                return new NRZI();
            case "Manchester":
                return new Manchester();
            case "Differential Manchester":
                return new DifferentialManchester();
            case "AMI":
                return new AMI();
            default:
                throw new IllegalArgumentException("Unknown encoding type: " + encodingType);
        }
    }
}