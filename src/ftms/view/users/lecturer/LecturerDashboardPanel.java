package ftms.view.users.lecturer;

import ftms.exception.DatabaseException;
import ftms.service.DashboardService;
import ftms.util.NavigationManager;
import ftms.view.users.common.StatCard;

import javax.swing.*;
import java.awt.*;

public class LecturerDashboardPanel extends JPanel {
    private final DashboardService dashboardService = new DashboardService();
    private final StatCard coursesCard = new StatCard("--", "Courses");
    private final StatCard studentsCard = new StatCard("--", "Students");
    private final StatCard medicalsCard = new StatCard("--", "Pending Medicals");

    public LecturerDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Lecturer Dashboard");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 10, 10));
        cards.add(coursesCard);
        cards.add(studentsCard);
        cards.add(medicalsCard);
        add(cards, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        actions.add(navButton("Enter Marks", "MARKS_ENTRY"));
        actions.add(navButton("Review Medicals", "MEDICAL_VIEW"));
        actions.add(navButton("Upload Materials", "MATERIALS"));
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
            coursesCard.setValue(String.valueOf(dashboardService.countCourses()));
            studentsCard.setValue(String.valueOf(dashboardService.countStudents()));
            medicalsCard.setValue(String.valueOf(dashboardService.countPendingMedicals()));
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}