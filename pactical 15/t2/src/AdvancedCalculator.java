import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

public class AdvancedCalculator extends JFrame implements ActionListener {
    private JTextField display;
    // History list to store up to the last 10 calculations
    private ArrayList<String> calculationHistory = new ArrayList<>();

    public AdvancedCalculator() {
        setTitle("Swing Calculator with History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: Display screen and History button
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        topPanel.add(display, BorderLayout.CENTER);

        // Part (b) Requirement: History button
        JButton btnHistory = new JButton("History 📋");
        btnHistory.setFont(new Font("Arial", Font.PLAIN, 14));
        btnHistory.addActionListener(e -> showHistoryDialog());
        topPanel.add(btnHistory, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Grid Panel: Button layout matching the figure exactly
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 8, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        String[] buttons = {
                "C", "(", ")", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                ".", "0", "=", "%"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 20));
            button.setFocusPainted(false);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("C")) {
            display.setText("0");
        } else if (command.equals("=")) {
            String expression = display.getText();
            try {
                double result = evaluateExpression(expression);
                String resultStr = formatResult(result);
                display.setText(resultStr);

                // Add to history list (Keep only the last 10 entries)
                addHistoryRecord(expression + " = " + resultStr);
            } catch (Exception ex) {
                display.setText("Error");
            }
        } else {
            String currentText = display.getText();
            if (currentText.equals("0") || currentText.equals("Error")) {
                display.setText(command);
            } else {
                display.setText(currentText + command);
            }
        }
    }

    // Standard Math expression evaluator using Stacks (Handles +, -, *, /, (, ))
    private double evaluateExpression(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') continue;

            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
                i--;
            } else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '%') {
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(tokens[i]);
            }
        }

        while (!ops.empty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/' || op1 == '%') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            case '%': return a % b;
        }
        return 0;
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        }
        return String.valueOf(result);
    }

    // Track the last 10 calculations seamlessly
    private void addHistoryRecord(String record) {
        if (calculationHistory.size() >= 10) {
            calculationHistory.remove(0); // Remove oldest log
        }
        calculationHistory.add(record);
    }

    // Modal popup window displaying past history data
    private void showHistoryDialog() {
        StringBuilder historyBuilder = new StringBuilder();
        if (calculationHistory.isEmpty()) {
            historyBuilder.append("No calculations performed yet.");
        } else {
            for (int i = 0; i < calculationHistory.size(); i++) {
                historyBuilder.append(i + 1).append(". ").append(calculationHistory.get(i)).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, historyBuilder.toString(), "Calculation History (Last 10)", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdvancedCalculator().setVisible(true));
    }
}
