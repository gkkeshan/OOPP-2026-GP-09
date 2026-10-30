package ftms.view;

import ftms.exception.AuthenticationException;
import ftms.exception.DatabaseException;
import ftms.model.User;
import ftms.service.AuthService;
import ftms.util.AlertHelper;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        setTitle("FTAMS - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        emailPanel.add(emailField);

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        passPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        panel.add(new JLabel("Faculty Management System - Login", JLabel.CENTER));
        panel.add(emailPanel);
        panel.add(passPanel);
        panel.add(loginButton);

        add(panel);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.showError(this, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            User user = authService.login(email, password);
            AlertHelper.showInformation(this, "Success", "Welcome, " + user.getUserName() + "!");

            // Open Main Dashboard and dispose login frame
            new MainFrame().setVisible(true);
            this.dispose();

        } catch (AuthenticationException | DatabaseException ex) {
            AlertHelper.showError(this, "Login Failed", ex.getMessage());
        }
    }
}