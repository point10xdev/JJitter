package encoding;

import core.SignalFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements NRZ-L (Non-Return-to-Zero Level) encoding.
 * Assumes: '0' -> High level (+1), '1' -> Low level (-1)
 */
public class NRZL implements LineEncoder {
    @Override
    public List<SignalFrame> encode(String data) {
        List<SignalFrame> frames = new ArrayList<>();
        int time = 0;
        for (char bit : data.toCharArray()) {
            if (bit == '0') {
                frames.add(new SignalFrame(time, 1.0)); // '0' is High
            } else { // '1'
                frames.add(new SignalFrame(time, -1.0)); // '1' is Low
            }
            time++;
        }
        return frames;
    }
}