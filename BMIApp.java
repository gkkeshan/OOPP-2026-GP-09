import javax.swing.*;
import java.awt.*;

public class BMIApp extends JFrame {
    private final JTextField weightField = new JTextField(12);
    private final JTextField heightField = new JTextField(12);
    private final JLabel weightLabel = new JLabel("Weight (kg):");
    private final JLabel heightLabel = new JLabel("Height (cm):");
    private final JTextArea resultArea = new JTextArea(3, 22);

    private final JRadioButton metricButton =
            new JRadioButton("Metric (kg, cm)", true);
    private final JRadioButton englishButton =
            new JRadioButton("English (lb, in)");

    public BMIApp() {
        setTitle("BMI Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 400);
        setLocationRelativeTo(null);

        ButtonGroup units = new ButtonGroup();
        units.add(metricButton);
        units.add(englishButton);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        panel.add(new JLabel("Select units:"));
        panel.add(metricButton);
        panel.add(englishButton);

        panel.add(weightLabel);
        panel.add(weightField);
        panel.add(heightLabel);
        panel.add(heightField);

        JButton calculateButton = new JButton("Calculate");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel();
        buttons.add(calculateButton);
        buttons.add(clearButton);
        panel.add(buttons);

        resultArea.setEditable(false);
        resultArea.setText("Your BMI will appear here.");
        panel.add(new JScrollPane(resultArea));

        panel.add(new JLabel(
                "<html>BMI categories:<br>" +
                        "Underweight: below 18.5<br>" +
                        "Normal: 18.5–24.9<br>" +
                        "Overweight: 25–29.9<br>" +
                        "Obese: 30 or above</html>"
        ));

        metricButton.addActionListener(e -> setUnitLabels("kg", "cm"));
        englishButton.addActionListener(e -> setUnitLabels("lb", "in"));
        calculateButton.addActionListener(e -> calculateBMI());
        clearButton.addActionListener(e -> clearFields());

        add(panel);
    }

    private void setUnitLabels(String weightUnit, String heightUnit) {
        weightLabel.setText("Weight (" + weightUnit + "):");
        heightLabel.setText("Height (" + heightUnit + "):");
    }

    private void calculateBMI() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());

            if (weight <= 0 || height <= 0) {
                throw new IllegalArgumentException(
                        "Weight and height must be greater than zero."
                );
            }

            double bmi;

            if (metricButton.isSelected()) {
                // Height is entered in centimetres; convert to metres.
                double heightMetres = height / 100.0;
                bmi = weight / (heightMetres * heightMetres);
            } else {
                // Standard BMI formula for pounds and inches.
                bmi = 703.0 * weight / (height * height);
            }

            resultArea.setText(String.format(
                    "BMI: %.1f%nCategory: %s",
                    bmi, getCategory(bmi)
            ));

        } catch (NumberFormatException e) {
            showError("Enter valid numbers for weight and height.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private String getCategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    private void clearFields() {
        weightField.setText("");
        heightField.setText("");
        resultArea.setText("Your BMI will appear here.");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Input error", JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BMIApp().setVisible(true));
    }
}