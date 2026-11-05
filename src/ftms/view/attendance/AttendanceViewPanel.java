
package ftms.view.attendance;

import ftms.model.AttendanceRecord;
import ftms.service.AttendanceService;
import ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AttendanceViewPanel extends JPanel {

    private final JTextField studentIdField;
    private final JTextField courseCodeField;

    private final JTable attendanceTable;
    private AttendanceTableModel tableModel;

    private final AttendanceService attendanceService =
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
            studentIdField.setText(SessionManager.getCurrentRegistrationNo());
            studentIdField.setEditable(false);
        }

        JButton searchButton = new JButton("Search");

        JPanel searchPanel = new JPanel(new FlowLayout());

        searchPanel.add(new JLabel("Registration No:"));
        searchPanel.add(studentIdField);

        searchPanel.add(new JLabel("Course Code Filter:"));
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
        searchAttendance();
    }

    private void searchAttendance() {
        String registrationNo = SessionManager.isUndergraduate()
                ? SessionManager.getCurrentRegistrationNo()
                : studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim();

        new SwingWorker<List<AttendanceRecord>, Void>() {
            @Override
            protected List<AttendanceRecord> doInBackground() throws SQLException {
                return attendanceService.getAttendance(registrationNo, courseCode);
            }

            @Override
            protected void done() {
                try {
                    List<AttendanceRecord> records = get();
                    tableModel = new AttendanceTableModel(records);
                    attendanceTable.setModel(tableModel);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(AttendanceViewPanel.this,
                            "Loading attendance was interrupted.");
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    JOptionPane.showMessageDialog(AttendanceViewPanel.this,
                            "Unable to load attendance: "
                                    + (cause == null ? exception.getMessage() : cause.getMessage()));
                }
            }
        }.execute();
    }
}
