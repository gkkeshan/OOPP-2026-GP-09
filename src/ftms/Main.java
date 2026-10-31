package ftms;

import ftms.util.Theme;
import ftms.view.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run on the Swing Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            Theme.applyGlobalTheme();
            new LoginFrame().setVisible(true);
        });
    }
}