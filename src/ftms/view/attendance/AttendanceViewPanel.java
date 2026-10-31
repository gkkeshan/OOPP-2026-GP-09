
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

        searchPanel.add(new JLabel("Student ID:"));
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

        searchButton.addActionListener(e -> searchAttendance());
    }

    private void searchAttendance() {

        try {
            int studentId = SessionManager.isUndergraduate()
                    ? SessionManager.getCurrentStudentId()
                    : Integer.parseInt(studentIdField.getText().trim());

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
}
