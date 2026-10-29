package ftms;

import ftms.view.LoginFrame;
import ftms.view.course.CourseManagementPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run on the Swing Event Dispatch Thread for thread safety


        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}