
package ftms.view.attendance;

import ftms.model.AttendanceRecord;
import ftms.service.AttendanceService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceEntryPanel extends JPanel {

    private JTextField courseCodeField;
    private JTextField sessionNoField;
    private JTextField dateField;
    private JTextField toIdField;

    private JComboBox<String> sessionTypeBox;

    private JTable attendanceTable;
    private AttendanceTableModel tableModel;

    private List<AttendanceRecord> attendanceRecords;

    private AttendanceService attendanceService =
            new AttendanceService();

    public AttendanceEntryPanel() {

        attendanceRecords = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Attendance Entry");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20));

        courseCodeField = new JTextField();
        sessionNoField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        toIdField = new JTextField();

        sessionTypeBox = new JComboBox<>(
                new String[]{"Theory", "Practical"});

        formPanel.add(new JLabel("Course Code:"));
        formPanel.add(courseCodeField);

        formPanel.add(new JLabel("Session Number:"));
        formPanel.add(sessionNoField);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Session Type:"));
        formPanel.add(sessionTypeBox);

        formPanel.add(new JLabel("TO ID:"));
        formPanel.add(toIdField);

        JButton saveButton = new JButton("Save Attendance");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        tableModel = new AttendanceTableModel(attendanceRecords);
        attendanceTable = new JTable(tableModel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(saveButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveAttendance());
    }

    private void saveAttendance() {

        try {
            String courseCode = courseCodeField.getText().trim();
            int sessionNo = Integer.parseInt(
                    sessionNoField.getText().trim());
            LocalDate date = LocalDate.parse(
                    dateField.getText().trim());
            String sessionType =
                    (String) sessionTypeBox.getSelectedItem();
            int toId = Integer.parseInt(
                    toIdField.getText().trim());

            if (courseCode.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Enter a course code.");
                return;
            }

            if (sessionNo <= 0 || toId <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Session Number and TO ID must be positive.");
                return;
            }

            int savedCount = attendanceService.addSession(
                    courseCode, sessionNo, date, sessionType, toId);

            JOptionPane.showMessageDialog(
                    this,
                    savedCount + " attendance records saved successfully.");

            attendanceRecords =
                    attendanceService.getAttendanceByCourse(courseCode);

            tableModel = new AttendanceTableModel(attendanceRecords);
            attendanceTable.setModel(tableModel);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Enter valid numbers for Session Number and TO ID.");

        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this, "Enter the date as YYYY-MM-DD.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Database error: " + e.getMessage());
        }
    }
}

