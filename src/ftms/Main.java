package ftms;

import ftms.config.DBConnection;
import ftms.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Set standard system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Test Database Connection on startup
        System.out.println("Starting FOT Management System...");
        DBConnection.getConnection();

        // 3. Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            System.out.println("UI Frame initialized successfully.");
        });

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });

    }
}