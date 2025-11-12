package core;

/**
 * Represents a single "frame" or segment of the signal waveform.
 * This structure allows for signals that change level in the middle of a bit period,
 * like Manchester encoding.
 *
 * This is a core data structure used by the encoders and the renderer.
 */
public class SignalFrame {
    public final double startTime;
    public final double endTime;
    public final double level; // e.g., +1, 0, -1

    /**
     * Constructor for a signal frame with a flat level.
     * @param time The bit time index (e.g., 0, 1, 2...)
     * @param level The voltage level for this bit time.
     */
    public SignalFrame(int time, double level) {
        this.startTime = time;
        this.endTime = time + 1.0;
        this.level = level;
    }

    /**
     * Constructor for a signal frame with a specific start and end time.
     * Useful for mid-bit transitions.
     * @param startTime The start time of this frame.
     * @param endTime The end time of this frame.
     * @param level The voltage level for this frame.
     */
    public SignalFrame(double startTime, double endTime, double level) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.level = level;
    }

    @Override
    public String toString() {
        return String.format("[%.1f-%.1f @ %.1fV]", startTime, endTime, level);
    }
}