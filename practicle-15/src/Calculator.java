import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class Calculator extends JFrame {
    private final JTextField display = new JTextField();
    private final Deque<String> history = new ArrayDeque<>();

    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 400);
        setLocationRelativeTo(null);

        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.PLAIN, 24));

        JPanel buttonsPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "C", "0", "=", "+"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(e -> handleButton(text));
            buttonsPanel.add(button);
        }

        JButton historyButton = new JButton("History");
        historyButton.addActionListener(e -> showHistory());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(historyButton, BorderLayout.CENTER);

        setLayout(new BorderLayout(8, 8));
        add(display, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleButton(String text) {
        if (text.equals("C")) {
            display.setText("");
        } else if (text.equals("=")) {
            calculate();
        } else {
            display.setText(display.getText() + text);
        }
    }

    private void calculate() {
        String expression = display.getText();

        try {
            double answer = evaluate(expression);
            String calculation = expression + " = " + answer;

            history.addFirst(calculation);
            if (history.size() > 10) {
                history.removeLast();
            }

            display.setText(String.valueOf(answer));
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    // Supports expressions with two numbers and one operator.
    private double evaluate(String expression) {
        String[] operators = {"+", "-", "*", "/"};

        for (String operator : operators) {
            int position = expression.indexOf(operator, 1);

            if (position > 0) {
                double firstNumber =
                        Double.parseDouble(expression.substring(0, position));
                double secondNumber =
                        Double.parseDouble(expression.substring(position + 1));

                switch (operator) {
                    case "+":
                        return firstNumber + secondNumber;
                    case "-":
                        return firstNumber - secondNumber;
                    case "*":
                        return firstNumber * secondNumber;
                    case "/":
                        if (secondNumber == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        return firstNumber / secondNumber;
                }
            }
        }

        throw new IllegalArgumentException("Invalid calculation");
    }

    private void showHistory() {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No calculations yet.");
            return;
        }

        StringBuilder historyText = new StringBuilder();
        for (String item : history) {
            historyText.append(item).append("\n");
        }

        JOptionPane.showMessageDialog(
                this,
                historyText.toString(),
                "Last 10 Calculations",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Calculator().setVisible(true));
    }
}