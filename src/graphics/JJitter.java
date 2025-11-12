package graphics;

import core.SignalProcessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Main application class.
 * Creates the JFrame and organizes all the UI components.
 *
 * (Updated with UI/UX improvements, full parameter names, and layout adjustments for graphs)
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
    private JTextField textAnalogInput;
    private JComboBox<String> comboAnalogWaveform;
    private JTextField paramF, paramA, paramPhi, paramFs, paramD, paramK;
    // private JButton applyAnalogButton; // Removed for simplicity

    private JComboBox<String> comboModulation;
    private JComboBox<String> comboEncoding;
    private JComboBox<String> comboScrambling; // Added for intuitive UI
    private JLabel labelScrambling; // Added for intuitive UI

    private JTextArea textOutput;
    private SimpleSignalRenderer signalRenderer;
    private SimpleSignalRenderer scrambledSignalRenderer;
    private JButton btnGenerate;

    public JJitter() {
        this.processor = new SignalProcessor();

        setTitle("Digital Signal Generator");
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
        JPanel panel = new JPanel(new GridBagLayout());
        // --- CHANGE: Reduced bottom padding from 5 to 2 ---
        panel.setBorder(new EmptyBorder(10, 10, 2, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Input Type ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(createInputTypePanel(), gbc);

        // --- Encoding ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Line Encoding:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboEncoding = new JComboBox<>(new String[]{"NRZ-L", "NRZ-I", "Manchester", "Differential Manchester", "AMI"});
        panel.add(comboEncoding, gbc);

        // --- Scrambling (Context-Aware) ---
        labelScrambling = new JLabel("Scrambling (AMI only):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(labelScrambling, gbc);

        comboScrambling = new JComboBox<>(new String[]{"NONE", "B8ZS", "HDB3"});
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
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


        // --- Generate Button ---
        gbc.gridx = 0;
        gbc.gridy = 3; // Updated gridy
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnGenerate = new JButton("Generate Signal");
        btnGenerate.setFont(btnGenerate.getFont().deriveFont(Font.BOLD, 14f));
        btnGenerate.addActionListener(e -> generateSignal());
        panel.add(btnGenerate, gbc);

        return panel;
    }

    private JPanel createInputTypePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Input Source"));

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
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

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
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Modulation ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Modulation:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboModulation = new JComboBox<>(new String[]{"PCM", "DM"});
        panel.add(comboModulation, gbc);

        // --- Analog Signal Definition ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Waveform:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        comboAnalogWaveform = new JComboBox<>(new String[] { "Sine", "Cosine", "Exponential", "Square", "Sawtooth" });
        panel.add(comboAnalogWaveform, gbc);

        // --- Analog Parameters Panel ---
        // Use a smaller gap to accommodate longer labels
        JPanel paramsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paramsPanel.add(new JLabel("Frequency (f):"));
        paramF = new JTextField("5", 4);
        paramsPanel.add(paramF);
        paramsPanel.add(new JLabel("Amplitude (a):"));
        paramA = new JTextField("1", 4);
        paramsPanel.add(paramA);
        paramsPanel.add(new JLabel("Phase (phi):"));
        paramPhi = new JTextField("0", 4);
        paramsPanel.add(paramPhi);
        paramsPanel.add(new JLabel("Sample Rate (fs):"));
        paramFs = new JTextField("100", 4);
        paramsPanel.add(paramFs);
        paramsPanel.add(new JLabel("Duration (d):"));
        paramD = new JTextField("1", 4);
        paramsPanel.add(paramD);
        paramsPanel.add(new JLabel("Rate (k):"));
        paramK = new JTextField("1", 4);
        paramsPanel.add(paramK);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(paramsPanel, gbc);

        return panel;
    }

    /**
     * This method is no longer needed as CardLayout handles visibility.
     */
    // private void toggleInputControls() { ... } // DELETED

    /**
     * Builds the analog spec string directly from the UI fields.
     * Replaces the old applyAnalogSpecToInput() and textAnalogInput field.
     * @return The formatted specification string.
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
        // --- CHANGE: Reduced top padding from 5 to 2 ---
        panel.setBorder(new EmptyBorder(2, 10, 10, 10));

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

        // --- CHANGE: Reduced resizeWeight from 0.15 to 0.10 ---
        // This gives even less space to the top (text area) and more to the bottom (graphs).
        // The user can manually drag the divider up if they need to read the text.
        mainOutputSplit.setResizeWeight(0.10);

        panel.add(mainOutputSplit, BorderLayout.CENTER);
        return panel;
    }

    private void generateSignal() {
        String encoding = (String) comboEncoding.getSelectedItem();
        // Get scrambling from the combo box, not a popup
        String scrambling = (String) comboScrambling.getSelectedItem();

        // The disruptive JOptionPane has been removed.
        // if ("AMI".equals(encoding)) { ... } // DELETED

        SignalProcessor.SignalResult result;

        try {
            if (radioDigital.isSelected()) {
                String data = textDigitalInput.getText().replaceAll("[^01]", ""); // Sanitize input
                textDigitalInput.setText(data);
                if(data.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid binary string.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                result = processor.processDigitalInput(data, encoding, scrambling);
            } else {
                // Analog Input
                String modulation = (String) comboModulation.getSelectedItem();
                // Build spec string directly from fields, don't read from text field
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
                + "&nbsp;&nbsp;&bull; If scrambling was used, see its effect in the 'Scrambled Signal' graph.<br><br>"
                + "<b>Tip:</b> All panels are resizable! You can drag the dividers to get more space.</html>";

        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        // Set Look and Feel for a more modern appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't set system Look and Feel.");
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