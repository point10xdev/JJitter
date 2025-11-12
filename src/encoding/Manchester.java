package encoding;

import core.SignalFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements Manchester encoding.
 * Assumes (IEEE 802.3): '0' -> High-to-Low transition, '1' -> Low-to-High transition
 */
public class Manchester implements LineEncoder {
    @Override
    public List<SignalFrame> encode(String data) {
        List<SignalFrame> frames = new ArrayList<>();
        int time = 0;
        for (char bit : data.toCharArray()) {
            double t0 = time;
            double t1 = time + 0.5;
            double t2 = time + 1.0;

            if (bit == '0') {
                // '0' is High-to-Low
                frames.add(new SignalFrame(t0, t1, 1.0));
                frames.add(new SignalFrame(t1, t2, -1.0));
            } else { // '1'
                // '1' is Low-to-High
                frames.add(new SignalFrame(t0, t1, -1.0));
                frames.add(new SignalFrame(t1, t2, 1.0));
            }
            time++;
        }
        return frames;
    }
}