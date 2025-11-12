package graphics;

import core.SignalFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.util.List;
import java.util.Collections;

/**
 * A JPanel component responsible for drawing the digital signal waveform.
 */
public class SimpleSignalRenderer extends JPanel {

    private List<SignalFrame> signal = Collections.emptyList();
    private String dataString = "";

    private static final int PADDING = 40;
    private static final int VOLTAGE_AMP = 50; // Pixels for +1V
    private static final Color COLOR_SIGNAL = new Color(0, 119, 190); // Blue
    private static final Color COLOR_GRID = new Color(220, 220, 220);
    private static final Color COLOR_TEXT = Color.BLACK;

    public SimpleSignalRenderer() {
        setBackground(Color.WHITE);
    }

    /**
     * Sets the signal data to be rendered.
     * @param signal The list of SignalFrames.
     * @param data The original binary data string for labels.
     */
    public void setSignal(List<SignalFrame> signal, String data) {
        this.signal = (signal != null) ? signal : Collections.emptyList();
        this.dataString = (data != null) ? data : "";
        repaint(); // Request a redraw
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (signal.isEmpty()) {
            drawEmpty(g2d);
            return;
        }

        int w = getWidth();
        int h = getHeight();
        int y_zero = h / 2; // Y-coordinate for 0V
        int y_plus_one = y_zero - VOLTAGE_AMP;
        int y_minus_one = y_zero + VOLTAGE_AMP;

        // Calculate bit width
        double maxTime = signal.get(signal.size() - 1).endTime;
        double bitWidth = (w - 2.0 * PADDING) / maxTime;

        // --- Draw Axes and Labels ---
        g2d.setColor(COLOR_GRID);
        // 0V line
        g2d.drawLine(PADDING, y_zero, w - PADDING, y_zero);
        // +1V line
        g2d.drawLine(PADDING, y_plus_one, w - PADDING, y_plus_one);
        // -1V line
        g2d.drawLine(PADDING, y_minus_one, w - PADDING, y_minus_one);

        g2d.setColor(COLOR_TEXT);
        g2d.drawString(" 0V", 5, y_zero + 5);
        g2d.drawString("+1V", 5, y_plus_one + 5);
        g2d.drawString("-1V", 5, y_minus_one + 5);

        // --- Draw Signal ---
        g2d.setColor(COLOR_SIGNAL);
        g2d.setStroke(new java.awt.BasicStroke(2));

        double lastLevel = 0; // Assume starts at 0
        int lastX = PADDING;

        // Draw initial transition from 0
        if (!signal.isEmpty()) {
            int startX = (int) (PADDING + signal.get(0).startTime * bitWidth);
            int startY = (int) (y_zero - signal.get(0).level * VOLTAGE_AMP);
            g2d.drawLine(PADDING, y_zero, startX, startY);
            lastLevel = signal.get(0).level;
            lastX = startX;
        }

        for (SignalFrame frame : signal) {
            int x0 = (int) (PADDING + frame.startTime * bitWidth);
            int x1 = (int) (PADDING + frame.endTime * bitWidth);
            int y = (int) (y_zero - frame.level * VOLTAGE_AMP);

            // Vertical line for transition (if level changed)
            if (frame.level != lastLevel) {
                g2d.drawLine(x0, (int)(y_zero - lastLevel * VOLTAGE_AMP), x0, y);
            }

            // Horizontal line for signal level
            g2d.drawLine(x0, y, x1, y);

            lastLevel = frame.level;
            lastX = x1;
        }

        // --- Draw Bit Grid Lines and Data Labels ---
        g2d.setColor(COLOR_GRID);
        g2d.setStroke(new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
                java.awt.BasicStroke.JOIN_MITER, 10.0f, new float[]{2.0f}, 0.0f));

        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(COLOR_TEXT);

        for (int i = 0; i <= (int)maxTime; i++) {
            int x = (int) (PADDING + i * bitWidth);
            g2d.setColor(COLOR_GRID);
            g2d.drawLine(x, PADDING, x, h - PADDING);

            // Draw data bit label
            if (i < dataString.length()) {
                g2d.setColor(COLOR_TEXT);
                String bit = String.valueOf(dataString.charAt(i));
                int bitX = (int) (x + (bitWidth / 2) - fm.stringWidth(bit) / 2);
                g2d.drawString(bit, bitX, PADDING - 10);
            }
        }
    }

    private void drawEmpty(Graphics2D g2d) {
        String msg = "No Signal Generated";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2d.setColor(Color.GRAY);
        g2d.drawString(msg, x, y);
    }
}