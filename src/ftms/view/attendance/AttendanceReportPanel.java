
package ftms.view.attendance;

import ftms.model.AttendanceSummary;
import ftms.service.AttendanceReportService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AttendanceReportPanel extends JPanel {

    private AttendanceReportService reportService =
            new AttendanceReportService();

    private JTextField studentIdField;
    private JTextField courseCodeField;

    private JLabel theoryLabel;
    private JLabel practicalLabel;
    private JLabel combinedLabel;

    public AttendanceReportPanel() {

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Attendance Report");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        studentIdField = new JTextField(10);
        courseCodeField = new JTextField(10);

        JButton searchButton = new JButton("Generate Report");

        JPanel inputPanel = new JPanel();

        inputPanel.add(new JLabel("Student ID:"));
        inputPanel.add(studentIdField);
        inputPanel.add(new JLabel("Course Code:"));
        inputPanel.add(courseCodeField);
        inputPanel.add(searchButton);

        JPanel resultPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        theoryLabel = new JLabel("Theory Attendance: -");
        practicalLabel = new JLabel("Practical Attendance: -");
        combinedLabel = new JLabel("Combined Attendance: -");

        resultPanel.add(theoryLabel);
        resultPanel.add(practicalLabel);
        resultPanel.add(combinedLabel);

        add(title, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> generateReport());
    }

    private void generateReport() {

        try {
            int studentId = Integer.parseInt(
                    studentIdField.getText().trim());

            String courseCode = courseCodeField.getText().trim();

            if (courseCode.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Enter a course code.");
                return;
            }

            AttendanceSummary summary =
                    reportService.getAttendanceSummary(
                            studentId, courseCode);

            theoryLabel.setText(String.format(
                    "Theory Attendance: %.2f%%",
                    summary.getTheoryPercentage()));

            practicalLabel.setText(String.format(
                    "Practical Attendance: %.2f%%",
                    summary.getPracticalPercentage()));

            combinedLabel.setText(String.format(
                    "Combined Attendance: %.2f%%",
                    summary.getCombinedPercentage()));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid Student ID.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Error: " + e.getMessage());
        }
    }
}
