package ftms.service;

import javax.swing.*;
import java.awt.*;

public class AlertHelper {
    public static void showInformation(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirmation(Component parent, String title, String message) {
        int option = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return option == JOptionPane.YES_OPTION;
    }
}