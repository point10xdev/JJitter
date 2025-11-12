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
 */
public class JJitter extends JFrame {

    private SignalProcessor processor;

    // --- UI Components ---
    private JRadioButton radioDigital, radioAnalog;
    private JTextField textDigitalInput;

    // New components for Analog Input
    private JTextField textAnalogInput;
    private JComboBox<String> comboAnalogWaveform;
    private JTextField paramF, paramA, paramPhi, paramFs, paramD, paramK;
    private JButton applyAnalogButton;

    private JComboBox<String> comboModulation;
    private JComboBox<String> comboEncoding;
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
        mainSplit.setResizeWeight(0.4); // Give control panel more space

        add(mainSplit, BorderLayout.CENTER);

        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
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
        panel.add(new JLabel("Line Encoding:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        comboEncoding = new JComboBox<>(new String[]{"NRZ-L", "NRZ-I", "Manchester", "Differential Manchester", "AMI"});
        panel.add(comboEncoding, gbc);

        // --- Generate Button ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnGenerate = new JButton("Generate Signal");
        btnGenerate.setFont(btnGenerate.getFont().deriveFont(Font.BOLD, 14f));
        btnGenerate.addActionListener(e -> generateSignal());
        panel.add(btnGenerate, gbc);

        // Toggle UI based on initial selection
        toggleInputControls();

        return panel;
    }

    private JPanel createInputTypePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Input Source"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Digital Input Row ---
        radioDigital = new JRadioButton("Digital Input:", true);
        textDigitalInput = new JTextField("1000000001100001", 30);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(radioDigital, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(textDigitalInput, gbc);

        // --- Analog Input Row ---
        radioAnalog = new JRadioButton("Analog Input:");
        comboModulation = new JComboBox<>(new String[]{"PCM", "DM"});

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(radioAnalog, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(comboModulation, gbc);

        // --- Analog Signal Definition ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Analog Signal:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        comboAnalogWaveform = new JComboBox<>(new String[] { "Sine", "Cosine", "Exponential", "Square", "Sawtooth" });
        panel.add(comboAnalogWaveform, gbc);

        // --- Analog Parameters Row 1 ---
        JPanel paramsRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paramsRow1.add(new JLabel("f:"));
        paramF = new JTextField("5", 4);
        paramsRow1.add(paramF);
        paramsRow1.add(new JLabel("a:"));
        paramA = new JTextField("1", 4);
        paramsRow1.add(paramA);
        paramsRow1.add(new JLabel("phi:"));
        paramPhi = new JTextField("0", 4);
        paramsRow1.add(paramPhi);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        panel.add(paramsRow1, gbc);

        // --- Analog Parameters Row 2 ---
        JPanel paramsRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paramsRow2.add(new JLabel("fs:"));
        paramFs = new JTextField("100", 4);
        paramsRow2.add(paramFs);
        paramsRow2.add(new JLabel("d:"));
        paramD = new JTextField("1", 4);
        paramsRow2.add(paramD);
        paramsRow2.add(new JLabel("k:"));
        paramK = new JTextField("1", 4);
        paramsRow2.add(paramK);
        applyAnalogButton = new JButton("Apply");
        applyAnalogButton.addActionListener(e -> applyAnalogSpecToInput());
        paramsRow2.add(applyAnalogButton);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        panel.add(paramsRow2, gbc);

        // --- Analog Input Text Field ---
        textAnalogInput = new JTextField("sin(f=5,a=1,fs=100,d=1)", 30);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        panel.add(textAnalogInput, gbc);


        // --- Radio Button Group ---
        ButtonGroup bg = new ButtonGroup();
        bg.add(radioDigital);
        bg.add(radioAnalog);

        ActionListener listener = e -> toggleInputControls();
        radioDigital.addActionListener(listener);
        radioAnalog.addActionListener(listener);

        return panel;
    }

    private void toggleInputControls() {
        boolean isDigital = radioDigital.isSelected();

        textDigitalInput.setEnabled(isDigital);

        comboModulation.setEnabled(!isDigital);
        textAnalogInput.setEnabled(!isDigital);
        comboAnalogWaveform.setEnabled(!isDigital);
        paramF.setEnabled(!isDigital);
        paramA.setEnabled(!isDigital);
        paramPhi.setEnabled(!isDigital);
        paramFs.setEnabled(!isDigital);
        paramD.setEnabled(!isDigital);
        paramK.setEnabled(!isDigital);
        applyAnalogButton.setEnabled(!isDigital);
    }

    private void applyAnalogSpecToInput() {
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
        textAnalogInput.setText(spec);
    }


    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        mainOutputSplit.setResizeWeight(0.25);

        panel.add(mainOutputSplit, BorderLayout.CENTER);
        return panel;
    }

    private void generateSignal() {
        String encoding = (String) comboEncoding.getSelectedItem();
        String scrambling = "NONE";

        // Check for AMI scrambling
        if ("AMI".equals(encoding)) {
            String[] options = {"No Scrambling", "B8ZS", "HDB3"};
            int choice = JOptionPane.showOptionDialog(this,
                    "AMI Encoding selected. Apply scrambling?",
                    "Scrambling Option",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            switch (choice) {
                case 1: scrambling = "B8ZS"; break;
                case 2: scrambling = "HDB3"; break;
                default: scrambling = "NONE";
            }
        }

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
                String signalSpec = textAnalogInput.getText().trim();
                if (signalSpec.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid analog signal specification.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
        });
    }
}