package encoding;

import core.SignalFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements AMI (Alternate Mark Inversion) encoding.
 * '0' -> Zero level (0)
 * '1' -> Alternating High (+1) and Low (-1)
 *
 * This encoder is also aware of scrambling codes 'V' and 'B'
 * from the Scrambler classes.
 * 'V' (Violation): Pulse with the *same* polarity as the last pulse.
 * 'B' (Bipolar): Pulse with the *opposite* polarity of the last pulse.
 */
public class AMI implements LineEncoder {

    // State: what was the polarity of the last '1' pulse?
    private double lastPulseLevel = -1.0; // Start assuming last pulse was Low

    @Override
    public List<SignalFrame> encode(String data) {
        List<SignalFrame> frames = new ArrayList<>();
        lastPulseLevel = -1.0; // Reset for each encoding
        int time = 0;

        for (char bit : data.toCharArray()) {
            double level = 0.0;
            switch (bit) {
                case '0':
                    level = 0.0;
                    break;
                case '1':
                case 'B': // Bipolar pulse (normal AMI rule)
                    lastPulseLevel *= -1; // Invert
                    level = lastPulseLevel;
                    break;
                case 'V': // Violation pulse (same as last)
                    level = lastPulseLevel;
                    break;
            }
            frames.add(new SignalFrame(time, level));
            time++;
        }
        return frames;
    }
}