package ftms.view.course;

import ftms.model.Timetable;
import ftms.model.Venue;
import ftms.service.TimetableService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TimetableViewPanel extends JPanel {

    private TimetableService timetableService = new TimetableService();

    private TimetableTableModel tableModel;
    private JTable timetableTable;

    private JTextField txtDate;
    private JTextField txtCourseCode;
    private JLabel lblInfo;

    public TimetableViewPanel() {
        setLayout(new BorderLayout(10, 10));

        // 1. Title Header + Filter Bar (North)
        JLabel headerLabel = new JLabel("Timetable View", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        txtDate = new JTextField(10);
        txtCourseCode = new JTextField(10);
        JButton btnByDate = new JButton("View by Date");
        JButton btnByCourse = new JButton("View by Course");
        JButton btnAll = new JButton("Show All");

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Date (yyyy-mm-dd):"));
        filterPanel.add(txtDate);
        filterPanel.add(btnByDate);
        filterPanel.add(new JLabel("Course Code:"));
        filterPanel.add(txtCourseCode);
        filterPanel.add(btnByCourse);
        filterPanel.add(btnAll);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(headerLabel, BorderLayout.NORTH);
        northPanel.add(filterPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // 2. Session Table (Center)
        tableModel = new TimetableTableModel(timetableService.getAllSessions());
        timetableTable = new JTable(tableModel);
        timetableTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            onRowSelect();
        });
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        // 3. Status Bar + Refresh (South)
        lblInfo = new JLabel("Showing all sessions", JLabel.CENTER);
        JButton btnRefresh = new JButton("Refresh");
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(lblInfo, BorderLayout.CENTER);
        southPanel.add(btnRefresh, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        btnByDate.addActionListener(e -> viewByDate());
        btnByCourse.addActionListener(e -> viewByCourse());
        btnAll.addActionListener(e -> showAll());
        btnRefresh.addActionListener(e -> showAll());
    }

    // 4. View Actions

    private void viewByDate() {
        String text = txtDate.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date (yyyy-mm-dd)");
            return;
        }
        try {
            displaySessions(timetableService.getSessionsByDate(LocalDate.parse(text)));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format: " + ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viewByCourse() {
        String code = txtCourseCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course code");
            return;
        }
        try {
            displaySessions(timetableService.getSessionsByCourse(code));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot load sessions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAll() {
        try {
            displaySessions(timetableService.getAllSessions());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Cannot load sessions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelect() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Timetable t = tableModel.getTimetableAt(row);
        Venue venue = timetableService.getVenue(t.getVenueId());
        if (venue != null) {
            lblInfo.setText("Venue: " + venue.getVenueName() + " (" + venue.getBuilding() + ")");
        } else {
            lblInfo.setText("Venue #" + t.getVenueId() + " not found");
        }
    }

    // 5. Helpers

    private void displaySessions(List<Timetable> sessions) {
        tableModel = new TimetableTableModel(sessions);
        timetableTable.setModel(tableModel);
        lblInfo.setText(sessions.size() + " session(s) found");
    }

    // 6. Standalone Test Runner

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Timetable View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new TimetableViewPanel());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}