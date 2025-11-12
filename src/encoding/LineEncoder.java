package encoding;

import core.SignalFrame;
import java.util.List;

/**
 * Interface for all line encoding schemes.
 */
public interface LineEncoder {
    /**
     * Encodes a binary data string into a list of SignalFrames for rendering.
     * @param data A string of '0's and '1's.
     * @return A List of SignalFrames representing the encoded waveform.
     */
    List<SignalFrame> encode(String data);
}