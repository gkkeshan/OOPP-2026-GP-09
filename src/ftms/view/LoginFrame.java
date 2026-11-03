package ftms.view;

import ftms.exception.AuthenticationException;
import ftms.exception.DatabaseException;
import ftms.model.User;
import ftms.service.AuthService;
import ftms.util.AlertHelper;
import ftms.util.Theme;

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
        setSize(440, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        // Main container with CLOUD background
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setBackground(Theme.CLOUD);

        // White card container for login
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.WHITE);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GREY_LINE, 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        card.setPreferredSize(new Dimension(360, 290));

        // Title and Subtitle
        JLabel titleLabel = new JLabel("FTMS LOGIN");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.INK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Faculty Management System");
        subtitleLabel.setFont(Theme.FONT_CAPTION);
        subtitleLabel.setForeground(Theme.GREY_TEXT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email Label & Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(Theme.FONT_BOLD);
        emailLabel.setForeground(Theme.INK);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField(20);
        Theme.styleTextField(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password Label & Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT_BOLD);
        passLabel.setForeground(Theme.INK);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(20);
        Theme.styleTextComponent(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button
        loginButton = new JButton("Sign In");
        Theme.stylePrimaryButton(loginButton);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        // Press Enter to login
        getRootPane().setDefaultButton(loginButton);

        // Assemble card
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);

        bgPanel.add(card);
        setContentPane(bgPanel);
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