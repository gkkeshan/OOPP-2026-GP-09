import javax.swing.*;
import java.awt.*;

public class DivisionCalculator extends JFrame {
    private final JTextField number1Field = new JTextField();
    private final JTextField number2Field = new JTextField();
    private final JTextField resultField = new JTextField();
    private final JLabel messageLabel = new JLabel(" ");

    public DivisionCalculator() {
        setTitle("Division Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        resultField.setEditable(false);

        JButton divideButton = new JButton("Divide");
        divideButton.addActionListener(e -> divide());

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Number 1:"));
        panel.add(number1Field);
        panel.add(new JLabel("Number 2:"));
        panel.add(number2Field);
        panel.add(new JLabel("Result:"));
        panel.add(resultField);
        panel.add(divideButton);
        panel.add(messageLabel);

        add(panel);
    }

    private void divide() {
        resultField.setText("");
        messageLabel.setText(" ");

        try {
            double number1 = Double.parseDouble(number1Field.getText().trim());
            double number2 = Double.parseDouble(number2Field.getText().trim());

            if (number2 == 0) {
                messageLabel.setText("You can't enter 0 for second number");
            } else {
                resultField.setText(String.valueOf(number1 / number2));
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Please enter valid numbers");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new DivisionCalculator().setVisible(true));
    }
}