
package ftms.view.attendance;

import ftms.model.AttendanceRecord;
import ftms.service.AttendanceService;
import ftms.service.MedicalService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AttendanceViewPanel extends JPanel {

    private JTextField studentIdField;
    private JTextField courseCodeField;

    private JTable attendanceTable;
    private AttendanceTableModel tableModel;

    private AttendanceService attendanceService =
            new AttendanceService();

    // South strip showing Theory / Practical / Combined percentages and
    // the exam-eligibility verdict for the currently loaded records.
    private JLabel summaryLabel = new JLabel(" ");

    public AttendanceViewPanel() {

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("View Attendance");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        studentIdField = new JTextField(10);
        courseCodeField = new JTextField(10);

        if ("UNDERGRADUATE".equalsIgnoreCase(
                SessionManager.getCurrentUser().getRole().trim())) {
            int myStudentId = new MedicalService().getStudentIdByUserId(
                    SessionManager.getCurrentUser().getUserID());
            studentIdField.setText(SessionManager.getCurrentRegistrationNo());
            studentIdField.setEditable(false);
        }

        JButton searchButton = new JButton("Search");

        JPanel searchPanel = new JPanel(new FlowLayout());

        searchPanel.add(new JLabel("Registration No:"));
        searchPanel.add(studentIdField);

        searchPanel.add(new JLabel("Course Code:"));
        searchPanel.add(courseCodeField);

        searchPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        tableModel = new AttendanceTableModel(
                new java.util.ArrayList<>());

        attendanceTable = new JTable(tableModel);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        add(summaryLabel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchAttendance());
    }

    private void searchAttendance() {

        try {
            int studentId;

            if (SessionManager.isUndergraduate()) {
                studentId = SessionManager.getCurrentStudentId();
            } else {
                String input = studentIdField.getText().trim();

                if (input.matches("\\d+")) {
                    studentId = Integer.parseInt(input);
                } else {
                    // Staff may type a registration number (e.g. TG2101)
                    studentId = attendanceService
                            .findStudentIdByRegistrationNo(input);
                    if (studentId == -1) {
                        JOptionPane.showMessageDialog(this,
                                "No student found with registration no: "
                                        + input);
                        return;
                    }
                }
            }

            String courseCode =
                    courseCodeField.getText().trim();

            if (courseCode.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a course code.");
                return;
            }

            List<AttendanceRecord> records =
                    attendanceService.getAttendance(
                            studentId, courseCode);

            tableModel = new AttendanceTableModel(records);
            attendanceTable.setModel(tableModel);

            summaryLabel.setText(buildSummary(records, courseCode));

            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "No attendance records found.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Please enter a valid Student ID.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load attendance: " + ex.getMessage());
        }
    }

    // Theory / Practical / Combined percentages with medical excusals applied,
    // plus the exam-eligibility verdict (same 80% rule as EligibilityStatus).
    private String buildSummary(List<AttendanceRecord> records,
                                String courseCode) {

        int thPres = 0, thTotal = 0, prPres = 0, prTotal = 0, excused = 0;

        for (AttendanceRecord r : records) {
            if (r.getMedicalId() != null) {
                excused++;                       // medically excused session
                continue;
            }

            boolean present = "Present".equals(r.getStatus());

            if ("Practical".equals(r.getSessionType())) {
                prTotal++;
                if (present) prPres++;
            } else {
                thTotal++;
                if (present) thPres++;
            }
        }

        int total = thTotal + prTotal;
        int present = thPres + prPres;
        double combined = total == 0 ? 0.0 : present * 100.0 / total;

        boolean eligible = combined >= 80;       // university 80% rule
        String color = eligible ? "#10893e" : "#c42b1c";

        return String.format(
                "<html><b>%s</b> &nbsp; Theory: %d/%d (%.2f%%) &nbsp; "
                        + "Practical: %d/%d (%.2f%%) &nbsp; Excused: %d &nbsp;|&nbsp; "
                        + "Combined: %.2f%% (%d/%d) &nbsp;&#8594;&nbsp; "
                        + "Exam Eligibility: <font color='%s'><b>%s</b></font></html>",
                courseCode.toUpperCase(),
                thPres, thTotal, pct(thPres, thTotal),
                prPres, prTotal, pct(prPres, prTotal),
                excused,
                combined, present, total,
                color, eligible ? "Yes" : "No");
    }

    private double pct(int present, int total) {
        return total == 0 ? 0.0 : present * 100.0 / total;
    }
}
