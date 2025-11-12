package encoding;

import core.SignalFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements Differential Manchester encoding.
 * Assumes: '0' -> Transition at start of bit, '1' -> No transition at start.
 * ALWAYS has a transition in the middle.
 * Starts with a Low-to-High bit (as if previous bit was '0').
 */
public class DifferentialManchester implements LineEncoder {

    @Override
    public List<SignalFrame> encode(String data) {
        List<SignalFrame> frames = new ArrayList<>();
        // Start assuming the "previous" bit ended on a High level.
        // This means the first bit's starting level will be High
        // if it's a '1' (no transition) or Low if it's a '0' (transition).
        double currentStartLevel = 1.0;
        int time = 0;

        for (char bit : data.toCharArray()) {
            double t0 = time;
            double t1 = time + 0.5;
            double t2 = time + 1.0;

            double firstHalfLevel;
            double secondHalfLevel;

            if (bit == '0') {
                // '0' = Transition at start
                firstHalfLevel = -currentStartLevel;
            } else { // '1'
                // '1' = No transition at start
                firstHalfLevel = currentStartLevel;
            }

            // Always transition in middle
            secondHalfLevel = -firstHalfLevel;

            frames.add(new SignalFrame(t0, t1, firstHalfLevel));
            frames.add(new SignalFrame(t1, t2, secondHalfLevel));

            // Save the level for the next bit's decision
            currentStartLevel = secondHalfLevel;
            time++;
        }
        return frames;
    }
}