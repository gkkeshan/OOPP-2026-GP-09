import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DivisionCalculator extends JFrame {
    private JTextField txtNum1;
    private JTextField txtNum2;
    private JLabel lblResultValue;

    public DivisionCalculator() {
        setTitle("Division Calculator");
        setSize(400, 160);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Number 1:"), gbc);

        txtNum1 = new JTextField(8);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(txtNum1, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(new JLabel("Result"), gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Number 2:"), gbc);

        txtNum2 = new JTextField(8);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtNum2, gbc);

        lblResultValue = new JLabel("");
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(lblResultValue, gbc);

        // Row 2
        JButton btnDivide = new JButton("Divide");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(btnDivide, gbc);

        btnDivide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDivision();
            }
        });
    }

    private void performDivision() {
        try {
            double num1 = Double.parseDouble(txtNum1.getText().trim());
            double num2 = Double.parseDouble(txtNum2.getText().trim());

            if (num2 == 0) {
                lblResultValue.setText("");
                JOptionPane.showMessageDialog(this,
                        "You can't enter 0 for second number",
                        "Message",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                double result = num1 / num2;
                lblResultValue.setText(String.valueOf(result));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DivisionCalculator().setVisible(true);
            }
        });
    }
}
