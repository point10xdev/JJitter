package modulation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Generates an analog signal array from a text specification.
 * Inspired by ai-alan/digitalsignalgenerator/DigitalSignalGenerator.../dsg/processing/SignalSynth.java
 */
public class SignalSynth {

    public static double[] generate(String spec) {
        if (spec == null) return new double[0];
        String s = spec.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return new double[0];

        // Detect function name and optional parameter list: name(k=v, ...)
        String name = s;
        Map<String, Double> params = new HashMap<>();
        int idxOpen = s.indexOf('(');
        if (idxOpen >= 0 && s.endsWith(")")) {
            name = s.substring(0, idxOpen).trim();
            String inside = s.substring(idxOpen + 1, s.length() - 1);
            for (String part : inside.split(",")) {
                part = part.trim();
                if (part.isEmpty()) continue;
                int eq = part.indexOf('=');
                if (eq > 0) {
                    String key = part.substring(0, eq).trim();
                    String val = part.substring(eq + 1).trim();
                    try { params.put(key, Double.parseDouble(val)); } catch (NumberFormatException ignore) {}
                }
            }
        }

        // Defaults
        double fs = params.getOrDefault("fs", 100.0); // samples per second
        double duration = params.getOrDefault("d", 1.0); // seconds
        int n = Math.max(1, (int)Math.round(fs * duration));
        double a = params.getOrDefault("a", 1.0);
        double f = params.getOrDefault("f", 5.0); // Hz
        double phi = params.getOrDefault("phi", 0.0); // radians
        double k = params.getOrDefault("k", 1.0); // growth/decay rate for exp

        double[] out = new double[n];
        switch (name) {
            case "sin":
            case "sine":
                for (int i = 0; i < n; i++) {
                    double t = i / fs;
                    out[i] = a * Math.sin(2 * Math.PI * f * t + phi);
                }
                break;
            case "cos":
            case "cosine":
                for (int i = 0; i < n; i++) {
                    double t = i / fs;
                    out[i] = a * Math.cos(2 * Math.PI * f * t + phi);
                }
                break;
            case "exp":
            case "exponential":
                for (int i = 0; i < n; i++) {
                    double t = i / fs;
                    out[i] = a * Math.exp(k * t);
                }
                break;
            case "square":
                for (int i = 0; i < n; i++) {
                    double t = i / fs;
                    double sgn = Math.sin(2 * Math.PI * f * t + phi);
                    out[i] = a * (sgn >= 0 ? 1.0 : -1.0);
                }
                break;
            case "saw":
            case "sawtooth":
                for (int i = 0; i < n; i++) {
                    double t = i / fs;
                    double period = 1.0 / Math.max(1e-9, f);
                    double u = t % period;
                    double frac = u / period; // 0..1
                    out[i] = a * (2.0 * frac - 1.0); // -a .. +a
                }
                break;
            default:
                // Unknown: return empty
                return new double[0];
        }
        return out;
    }
}