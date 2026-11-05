package ftms.view.users.admin;

import ftms.exception.DatabaseException;
import ftms.service.DashboardService;
import ftms.util.NavigationManager;
import ftms.view.users.common.StatCard;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private final DashboardService dashboardService = new DashboardService();
    private final StatCard usersCard = new StatCard("--", "Total Users");
    private final StatCard coursesCard = new StatCard("--", "Courses");
    private final StatCard medicalsCard = new StatCard("--", "Pending Medicals");
    private final StatCard noticesCard = new StatCard("--", "Notices");

    public AdminDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Administrator Dashboard");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 10));
        cards.add(usersCard);
        cards.add(coursesCard);
        cards.add(medicalsCard);
        cards.add(noticesCard);
        add(cards, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        actions.add(navButton("Manage Users", "USER_MANAGEMENT"));
        actions.add(navButton("Manage Courses", "COURSE_PANEL"));
        actions.add(navButton("Review Medicals", "MEDICAL_VIEW"));
        actions.add(navButton("Post Notice", "NOTICE_MANAGEMENT"));
        add(actions, BorderLayout.SOUTH);

        loadStats();
    }

    private JButton navButton(String label, String key) {
        JButton b = new JButton(label);
        b.addActionListener(e -> NavigationManager.showPanel(key));
        return b;
    }

    private void loadStats() {
        try {
            usersCard.setValue(String.valueOf(dashboardService.countUsers()));
            coursesCard.setValue(String.valueOf(dashboardService.countCourses()));
            medicalsCard.setValue(String.valueOf(dashboardService.countPendingMedicals()));
            noticesCard.setValue(String.valueOf(dashboardService.countNotices()));
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}