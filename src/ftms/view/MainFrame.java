package ftms.view;

import ftms.util.NavigationManager;
import ftms.util.SessionManager;
import ftms.view.admin.AdminDashboardPanel;
import ftms.view.admin.NoticeManagementPanel;
import ftms.view.admin.UserManagementPanel;


import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel cardContainer;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("FTAMS - Faculty Management System (" + SessionManager.getCurrentUser().getRole() + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        // Initialize Navigation Manager
        NavigationManager.initialize(cardContainer, cardLayout);

        // Add role-specific or general panels
        cardContainer.add(new AdminDashboardPanel(), "ADMIN_DASHBOARD");
        cardContainer.add(new UserManagementPanel(), "USER_MANAGEMENT");
        cardContainer.add(new NoticeManagementPanel(), "NOTICE_MANAGEMENT");


        add(cardContainer, BorderLayout.CENTER);

        // Start with Admin dashboard (or switch dynamically based on user role)
        NavigationManager.showPanel("ADMIN_DASHBOARD");
    }
}