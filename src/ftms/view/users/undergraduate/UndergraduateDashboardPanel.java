package ftms.view.users.undergraduate;

import ftms.exception.DatabaseException;
import ftms.service.DashboardService;
import ftms.util.NavigationManager;
import ftms.util.SessionManager;
import ftms.view.users.common.StatCard;

import javax.swing.*;
import java.awt.*;

public class UndergraduateDashboardPanel extends JPanel {
    private final DashboardService dashboardService = new DashboardService();
    private final StatCard cgpaCard = new StatCard("--", "Latest CGPA");
    private final StatCard attendanceCard = new StatCard("--", "Attendance %");
    private final StatCard coursesCard = new StatCard("--", "Active Courses");
    private final StatCard medicalCard = new StatCard("--", "Pending Medicals");

    public UndergraduateDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String regNo = SessionManager.getCurrentRegistrationNo();
        JLabel heading = new JLabel("Welcome, " + (regNo != null ? regNo : "Student"));
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 10));
        cards.add(cgpaCard);
        cards.add(attendanceCard);
        cards.add(coursesCard);
        cards.add(medicalCard);
        add(cards, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        actions.add(navButton("My Results", "RESULTS"));
        actions.add(navButton("My GPA", "GPA"));
        actions.add(navButton("My Attendance", "ATTENDANCE_VIEW"));
        actions.add(navButton("My Medicals", "MEDICAL_VIEW"));
        actions.add(navButton("My Courses", "MY_COURSES"));
        add(actions, BorderLayout.SOUTH);

        loadStats();
    }

    private JButton navButton(String label, String key) {
        JButton b = new JButton(label);
        b.addActionListener(e -> NavigationManager.showPanel(key));
        return b;
    }

    private void loadStats() {
        int studentId = SessionManager.getCurrentStudentId();
        if (studentId == -1) return;   // identity not cached — nothing to query
        try {
            Double cgpa = dashboardService.latestCgpa(studentId);
            if (cgpa != null) cgpaCard.setValue(String.format("%.2f", cgpa));

            Integer percent = dashboardService.attendancePercent(studentId);
            if (percent != null) attendanceCard.setValue(percent + "%");

            coursesCard.setValue(String.valueOf(dashboardService.countActiveEnrollments(studentId)));
            medicalCard.setValue(String.valueOf(dashboardService.countPendingMedicalsFor(studentId)));
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}