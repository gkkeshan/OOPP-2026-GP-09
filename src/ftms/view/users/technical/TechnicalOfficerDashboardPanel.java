package ftms.view.users.technical;

import ftms.exception.DatabaseException;
import ftms.service.DashboardService;
import ftms.util.NavigationManager;
import ftms.view.users.common.StatCard;

import javax.swing.*;
import java.awt.*;

public class TechnicalOfficerDashboardPanel extends JPanel {
    private final DashboardService dashboardService = new DashboardService();
    private final StatCard pendingCard = new StatCard("--", "Pending Medicals");
    private final StatCard totalCard = new StatCard("--", "Total Medicals");

    public TechnicalOfficerDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Technical Officer Dashboard");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 10, 10));
        cards.add(pendingCard);
        cards.add(totalCard);
        add(cards, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        actions.add(navButton("Mark Attendance", "ATTENDANCE_ENTRY"));
        actions.add(navButton("Review Medicals", "MEDICAL_VIEW"));
        actions.add(navButton("Attendance Report", "ATTENDANCE_REPORT"));
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
            pendingCard.setValue(String.valueOf(dashboardService.countPendingMedicals()));
            totalCard.setValue(String.valueOf(dashboardService.countMedicals()));
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}