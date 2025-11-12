package graphics;

import core.SignalProcessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Main application class.
 * Creates the JFrame and organizes all the UI components.
 *
 * (Updated with UI/UX improvements, full parameter names, and layout adjustments for graphs)
 * (Modernized with Nimbus L&F and cleaner control panel layout)
 */
public class JJitter extends JFrame {

    private SignalProcessor processor;

    // --- UI Components ---
    private JRadioButton radioDigital, radioAnalog;
    private JTextField textDigitalInput;

    // --- CardLayout for inputs ---
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- Analog Input Components ---
    // Removed textAnalogInput, as spec is built dynamically
    private JComboBox<String> comboAnalogWaveform;
    private JTextField paramF, paramA, paramPhi, paramFs, paramD, paramK;

    private JComboBox<String> comboModulation;
    private JComboBox<String> comboEncoding;
    private JComboBox<String> comboScrambling;
    private JLabel labelScrambling;

    private JTextArea textOutput;
    private SimpleSignalRenderer signalRenderer;
    private SimpleSignalRenderer scrambledSignalRenderer;
    private JButton btnGenerate;

    // --- UI Constants ---
    private static final Insets PANEL_PADDING = new Insets(10, 10, 10, 10);
    private static final Insets COMPONENT_INSETS = new Insets(5, 5, 5, 5);

    public JJitter() {
        this.processor = new SignalProcessor();

        setTitle("JJitter - Digital Signal Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Main Panels ---
        JPanel controlPanel = createControlPanel();
        JPanel outputPanel = createOutputPanel();

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlPanel, outputPanel);
        mainSplit.setResizeWeight(0.45); // Give control panel a bit more space

        add(mainSplit, BorderLayout.CENTER);

        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(PANEL_PADDING));

        // --- Input Panel (Top Left) ---
        JPanel inputTypePanel = createInputTypePanel();

        // --- Configuration Panel (Top Right) ---
        JPanel configPanel = createConfigurationPanel();

        // --- Generate Button (Bottom Center) ---
        btnGenerate = new JButton("Generate Signal");
        btnGenerate.setFont(btnGenerate.getFont().deriveFont(Font.BOLD, 14f));
        btnGenerate.addActionListener(e -> generateSignal());
        JPanel generatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        generatePanel.add(btnGenerate);

        panel.add(inputTypePanel, BorderLayout.CENTER);
        panel.add(configPanel, BorderLayout.EAST);
        panel.add(generatePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInputTypePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("1. Input Source"));

        // --- Radio Button Panel ---
        radioDigital = new JRadioButton("Digital Input", true);
        radioAnalog = new JRadioButton("Analog Input");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radioDigital);
        bg.add(radioAnalog);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.add(radioDigital);
        radioPanel.add(radioAnalog);
        panel.add(radioPanel, BorderLayout.NORTH);

        // --- Card Panel ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createDigitalCard(), "Digital");
        cardPanel.add(createAnalogCard(), "Analog");

        panel.add(cardPanel, BorderLayout.CENTER);

        // --- Add Listeners to Radio Buttons ---
        radioDigital.addActionListener(e -> cardLayout.show(cardPanel, "Digital"));
        radioAnalog.addActionListener(e -> cardLayout.show(cardPanel, "Analog"));

        return panel;
    }

    private JPanel createDigitalCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(PANEL_PADDING));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = COMPONENT_INSETS;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Binary Data:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textDigitalInput = new JTextField("1000000001100001", 30);
        panel.add(textDigitalInput, gbc);

        return panel;
    }

    private JPanel createAnalogCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(PANEL_PADDING));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = COMPONENT_INSETS;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int gridY = 0;

        // --- Modulation ---
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Modulation:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = gridY++;
        gbc.gridwidth = 2; // Span 2 cols
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboModulation = new JComboBox<>(new String[]{"PCM", "DM"});
        panel.add(comboModulation, gbc);

        // --- Analog Signal Definition ---
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Waveform:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = gridY++;
        gbc.gridwidth = 2; // Span 2 cols
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboAnalogWaveform = new JComboBox<>(new String[]{"Sine", "Cosine", "Exponential", "Square", "Sawtooth"});
        panel.add(comboAnalogWaveform, gbc);

        // --- Analog Parameters Panel (Now in a neat grid) ---
        gbc.gridwidth = 1; // Reset gridwidth

        // Column 1
        panel.add(new JLabel("Frequency (f):"), gbc(0, gridY));
        paramF = new JTextField("5", 6);
        panel.add(paramF, gbc(1, gridY++));

        panel.add(new JLabel("Amplitude (a):"), gbc(0, gridY));
        paramA = new JTextField("1", 6);
        panel.add(paramA, gbc(1, gridY++));

        panel.add(new JLabel("Phase (phi):"), gbc(0, gridY));
        paramPhi = new JTextField("0", 6);
        panel.add(paramPhi, gbc(1, gridY++));

        // Column 2
        gridY -= 3; // Reset Y to align
        panel.add(new JLabel("Sample Rate (fs):"), gbc(2, gridY));
        paramFs = new JTextField("100", 6);
        panel.add(paramFs, gbc(3, gridY++));

        panel.add(new JLabel("Duration (d):"), gbc(2, gridY));
        paramD = new JTextField("1", 6);
        panel.add(paramD, gbc(3, gridY++));

        panel.add(new JLabel("Rate (k):"), gbc(2, gridY));
        paramK = new JTextField("1", 6);
        panel.add(paramK, gbc(3, gridY++));

        return panel;
    }

    // Helper for analog card GBC
    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = COMPONENT_INSETS;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = (x % 2 == 0) ? GridBagConstraints.EAST : GridBagConstraints.WEST;
        gbc.fill = (x % 2 == 0) ? GridBagConstraints.NONE : GridBagConstraints.HORIZONTAL;
        return gbc;
    }


    private JPanel createConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("2. Signal Configuration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Encoding ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Line Encoding:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboEncoding = new JComboBox<>(new String[]{"NRZ-L", "NRZ-I", "Manchester", "Differential Manchester", "AMI"});
        panel.add(comboEncoding, gbc);

        // --- Scrambling (Context-Aware) ---
        labelScrambling = new JLabel("Scrambling (AMI only):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(labelScrambling, gbc);

        comboScrambling = new JComboBox<>(new String[]{"NONE", "B8ZS", "HDB3"});
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comboScrambling, gbc);

        // Add listener to show/hide scrambling
        comboEncoding.addActionListener(e -> {
            boolean amiSelected = "AMI".equals(comboEncoding.getSelectedItem());
            labelScrambling.setVisible(amiSelected);
            comboScrambling.setVisible(amiSelected);
            if (!amiSelected) {
                comboScrambling.setSelectedItem("NONE");
            }
        });

        // Initially hide scrambling options
        labelScrambling.setVisible(false);
        comboScrambling.setVisible(false);

        return panel;
    }

    /**
     * Builds the analog spec string directly from the UI fields.
     */
    private String buildAnalogSpecString() {
        String type = (String) comboAnalogWaveform.getSelectedItem();
        if (type == null) type = "Sine";
        String base;
        switch (type) {
            case "Sine": base = "sin"; break;
            case "Cosine": base = "cos"; break;
            case "Exponential": base = "exp"; break;
            case "Square": base = "square"; break;
            case "Sawtooth": base = "saw"; break;
            default: base = "sin"; break;
        }
        String spec = String.format(java.util.Locale.ROOT,
                "%s(f=%s,a=%s,phi=%s,fs=%s,d=%s,k=%s)",
                base,
                paramF.getText().trim(),
                paramA.getText().trim(),
                paramPhi.getText().trim(),
                paramFs.getText().trim(),
                paramD.getText().trim(),
                paramK.getText().trim());
        return spec;
    }


    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(PANEL_PADDING));

        // --- Text Output ---
        textOutput = new JTextArea(8, 60);
        textOutput.setEditable(false);
        textOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textOutput.setBorder(new TitledBorder("Processing Results"));
        JScrollPane scrollPane = new JScrollPane(textOutput);

        // --- Signal Renderers ---
        signalRenderer = new SimpleSignalRenderer();
        signalRenderer.setBorder(new TitledBorder("Encoded Signal"));

        scrambledSignalRenderer = new SimpleSignalRenderer();
        scrambledSignalRenderer.setBorder(new TitledBorder("Scrambled Signal (if applicable)"));

        JSplitPane renderSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, signalRenderer, scrambledSignalRenderer);
        renderSplit.setResizeWeight(0.5);

        JSplitPane mainOutputSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, renderSplit);
        mainOutputSplit.setResizeWeight(0.15); // Give a bit more space to text by default

        panel.add(mainOutputSplit, BorderLayout.CENTER);
        return panel;
    }

    private void generateSignal() {
        String encoding = (String) comboEncoding.getSelectedItem();
        String scrambling = (String) comboScrambling.getSelectedItem();

        SignalProcessor.SignalResult result;

        try {
            if (radioDigital.isSelected()) {
                String data = textDigitalInput.getText().replaceAll("[^01]", ""); // Sanitize input
                textDigitalInput.setText(data);
                if (data.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid binary string.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                result = processor.processDigitalInput(data, encoding, scrambling);
            } else {
                // Analog Input
                String modulation = (String) comboModulation.getSelectedItem();
                String signalSpec = buildAnalogSpecString();
                if (signalSpec.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter valid analog signal parameters.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                result = processor.processAnalogInput(signalSpec, modulation, encoding, scrambling);
            }

            // --- Display Results ---
            displayResults(result);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating signal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayResults(SignalProcessor.SignalResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Signal Generation Report ---\n");
        sb.append(String.format("Original Data:    %s\n", result.originalData));
        sb.append(String.format("Data Length:      %d bits\n", result.originalData.length()));
        sb.append(String.format("Longest Palindrome: %s (Length: %d)\n",
                result.longestPalindrome, result.longestPalindrome.length()));

        if (result.scrambledData != null) {
            sb.append("\n--- Scrambling ---\n");
            sb.append(String.format("Scrambled Data:   %s\n", result.scrambledData));
            // Update scrambled renderer
            scrambledSignalRenderer.setSignal(result.scrambledSignal, result.scrambledData.replaceAll("[^01VB]", "?"));
        } else {
            scrambledSignalRenderer.setSignal(null, ""); // Clear
        }

        textOutput.setText(sb.toString());
        textOutput.setCaretPosition(0); // Scroll to top

        // Update main renderer
        signalRenderer.setSignal(result.encodedSignal, result.originalData);
    }

    /**
     * Shows a welcome and instruction dialog.
     * @param parent The parent frame to center the dialog on.
     */
    private static void showInstructions(JFrame parent) {
        String title = "Welcome to JJitter!";
        String message = "<html><b>How to use this tool:</b><br><br>"
                + "1. <b>Choose Input Source:</b><br>"
                + "&nbsp;&nbsp;&bull; <b>Digital:</b> Enter a binary string (e.g., '100110').<br>"
                + "&nbsp;&nbsp;&bull; <b>Analog:</b> Select a modulation (PCM/DM) and waveform.<br><br>"
                + "2. <b>Select Line Encoding:</b><br>"
                + "&nbsp;&nbsp;&bull; Choose an encoding like NRZ-L, Manchester, or AMI.<br>"
                + "&nbsp;&nbsp;&bull; If you select <b>AMI</b>, scrambling options (B8ZS, HDB3) will appear.<br><br>"
                + "3. <b>Generate Signal:</b><br>"
                + "&nbsp;&nbsp;&bull; Click the 'Generate Signal' button.<br><br>"
                + "4. <b>View Results:</b><br>"
                + "&nbsp;&nbsp;&bull; See text results in the top panel.<br>"
                + "&nbsp;&nbsp;&bull; See the final waveform in the 'Encoded Signal' graph.<br>"
                + "&nbsp;&m&nbsp;&bull; If scrambling was used, see its effect in the 'Scrambled Signal' graph.<br><br>"
                + "<b>Tip:</b> All panels are resizable! You can drag the dividers to get more space.</html>";

        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        // Set Look and Feel for a more modern appearance
        try {
            // Use Nimbus for a modern look
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to system L&F
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e2) {
                System.err.println("Couldn't set Look and Feel.");
            }
        }

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JJitter generator = new JJitter();
            generator.setVisible(true);
            // Show instructions on startup
            showInstructions(generator);
        });
    }
}