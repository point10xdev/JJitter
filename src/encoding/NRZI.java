package encoding;

import core.SignalFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements NRZ-I (Non-Return-to-Zero Inverted) encoding.
 * Assumes: '0' -> No transition, '1' -> Transition at the start of the bit.
 * Starts at High level (+1).
 */
public class NRZI implements LineEncoder {
    @Override
    public List<SignalFrame> encode(String data) {
        List<SignalFrame> frames = new ArrayList<>();
        double currentLevel = 1.0; // Start at High
        int time = 0;

        for (char bit : data.toCharArray()) {
            if (bit == '1') {
                // '1' = Transition
                currentLevel *= -1; // Invert the level
            }
            // '0' = No transition, stay at currentLevel

            frames.add(new SignalFrame(time, currentLevel));
            time++;
        }
        return frames;
    }
}