package ftms.view.attendance;

import ftms.model.AttendanceSummary;
import ftms.service.AttendanceReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AttendanceReportPanel extends JPanel {

    private AttendanceReportService reportService = new AttendanceReportService();

    private JTextField studentIdField;
    private JTextField courseCodeField;
    private JTextField batchCourseField;

    private JLabel theoryLabel;
    private JLabel practicalLabel;
    private JLabel combinedLabel;

    private JTable batchTable;

    public AttendanceReportPanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Attendance Report");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // ---------- Whole batch section ----------
        batchCourseField = new JTextField(10);
        JButton batchButton = new JButton("Whole Class Report");
        JPanel batchInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        batchInput.setBorder(BorderFactory.createTitledBorder("Whole Batch Summary"));
        batchInput.add(new JLabel("Course Code:"));
        batchInput.add(batchCourseField);
        batchInput.add(batchButton);

        batchTable = new JTable();
        JPanel batchPanel = new JPanel(new BorderLayout(5, 5));
        batchPanel.add(batchInput, BorderLayout.NORTH);
        batchPanel.add(new JScrollPane(batchTable), BorderLayout.CENTER);

        // ---------- Individual section ----------
        studentIdField = new JTextField(10);
        courseCodeField = new JTextField(10);
        JButton searchButton = new JButton("Generate Report");

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Individual Summary"));
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

        JPanel south = new JPanel(new BorderLayout());
        south.add(inputPanel, BorderLayout.NORTH);
        south.add(resultPanel, BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(batchPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> generateReport());
        batchButton.addActionListener(e -> generateBatchReport());
    }

    private void generateReport() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText().trim());
            String courseCode = courseCodeField.getText().trim();

            if (courseCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a course code.");
                return;
            }

            AttendanceSummary summary = reportService.getAttendanceSummary(studentId, courseCode);

            theoryLabel.setText(String.format("Theory Attendance: %.2f%%", summary.getTheoryPercentage()));
            practicalLabel.setText(String.format("Practical Attendance: %.2f%%", summary.getPracticalPercentage()));
            combinedLabel.setText(String.format("Combined Attendance: %.2f%%", summary.getCombinedPercentage()));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid Student ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void generateBatchReport() {
        String courseCode = batchCourseField.getText().trim();
        if (courseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a course code.");
            return;
        }
        try {
            List<AttendanceSummary> summaries = reportService.getBatchSummary(courseCode);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Student ID", "Theory %", "Practical %", "Combined %", "Attendance Eligible (>=80%)"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (AttendanceSummary s : summaries) {
                model.addRow(new Object[]{
                        s.getStudentId(),
                        String.format("%.2f%%", s.getTheoryPercentage()),
                        String.format("%.2f%%", s.getPracticalPercentage()),
                        String.format("%.2f%%", s.getCombinedPercentage()),
                        s.getCombinedPercentage() >= 80 ? "Yes" : "No"
                });
            }
            batchTable.setModel(model);
            if (summaries.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No attendance records found for " + courseCode);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}